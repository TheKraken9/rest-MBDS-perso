package org.tpmbds.generator.service;

import org.springframework.stereotype.Service;
import org.tpmbds.generator.domain.constraint.Constraint;
import org.tpmbds.generator.domain.fieldtype.FieldType;
import org.tpmbds.generator.domain.fieldtype.FieldTypeRegistry;
import org.tpmbds.generator.dto.EntityDefinitionDto;
import org.tpmbds.generator.dto.FieldDefinitionDto;
import org.tpmbds.generator.dto.PreviewResponse;
import org.tpmbds.generator.dto.ProjectDefinitionDto;
import org.tpmbds.generator.exception.ServiceUnavailableException;
import org.tpmbds.generator.feign.DatasetManagerClient;

import java.util.*;

@Service
public class GeneratorService {

    private static final int MAX_RETRIES = 100;

    private final DatasetManagerClient managerClient;
    private final FieldTypeRegistry registry;

    public GeneratorService(DatasetManagerClient managerClient, FieldTypeRegistry registry) {
        this.managerClient = managerClient;
        this.registry = registry;
    }

    public PreviewResponse preview(Long projectId) {
        ProjectDefinitionDto project = managerClient.getProject(projectId);

        // Fallback propre: pas de stacktrace, on renvoie une réponse JSON explicite
        // (utile quand dataset-manager-service est down ou trop lent).
        if (project == null || project.getEntities() == null || project.getEntities().isEmpty()) {
            PreviewResponse fallback = new PreviewResponse();
            fallback.setProjectId(projectId);
            fallback.setData(Collections.emptyMap());
            fallback.setStatus("PARTIAL");
            fallback.setMessage("Service de génération momentanément indisponible (dataset-manager-service). Réessayez plus tard.");
            return fallback;
        }

        Map<String, List<Map<String, Object>>> data = new LinkedHashMap<>();

        for (EntityDefinitionDto entity : project.getEntities()) {
            int size = resolveSize(entity, project);
            List<Map<String, Object>> rows = new ArrayList<>(size);

            for (int rowIndex = 0; rowIndex < size; rowIndex++) {
                Map<String, Object> row = generateRow(entity, rowIndex);

                if (entity.getSubEntities() != null) {
                    for (EntityDefinitionDto sub : entity.getSubEntities()) {
                        row.put(sub.getName(), generateSubRows(sub, project));
                    }
                }

                rows.add(row);
            }

            data.put(entity.getName(), rows);
        }

        PreviewResponse ok = new PreviewResponse();
        ok.setProjectId(projectId);
        ok.setData(data);
        ok.setStatus("OK");
        ok.setMessage(null);
        return ok;
    }

    private List<Map<String, Object>> generateSubRows(EntityDefinitionDto subEntity,
                                                       ProjectDefinitionDto project) {
        int size = resolveSize(subEntity, project);
        List<Map<String, Object>> rows = new ArrayList<>(size);

        for (int rowIndex = 0; rowIndex < size; rowIndex++) {
            Map<String, Object> row = generateRow(subEntity, rowIndex);

            if (subEntity.getSubEntities() != null) {
                for (EntityDefinitionDto nested : subEntity.getSubEntities()) {
                    row.put(nested.getName(), generateSubRows(nested, project));
                }
            }

            rows.add(row);
        }

        return rows;
    }

    private Map<String, Object> generateRow(EntityDefinitionDto entity, int rowIndex) {
        Map<String, Object> row = new LinkedHashMap<>();

        if (entity.getFields() == null) return row;

        for (FieldDefinitionDto field : entity.getFields()) {
            FieldType fieldType = registry.getByCode(field.getType());
            Map<String, Object> config = field.getConstraints() != null
                    ? field.getConstraints()
                    : Collections.emptyMap();

            Constraint constraint = fieldType.createConstraint(config);
            var generator = fieldType.getGenerator();

            Object value = null;
            for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
                value = generator.generate(config, rowIndex);
                if (constraint.isValid(value)) break;
            }

            row.put(field.getName(), value);
        }

        return row;
    }

    private int resolveSize(EntityDefinitionDto entity, ProjectDefinitionDto project) {
        return entity.getRowCount() != null ? entity.getRowCount() : project.getSize();
    }
}
