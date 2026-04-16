package org.tpmbds.generator.service;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.tpmbds.generator.domain.constraint.Constraint;
import org.tpmbds.generator.domain.fieldtype.FieldType;
import org.tpmbds.generator.domain.fieldtype.FieldTypeRegistry;
import org.tpmbds.generator.dto.EntityDefinitionDto;
import org.tpmbds.generator.dto.FieldDefinitionDto;
import org.tpmbds.generator.dto.PreviewResponse;
import org.tpmbds.generator.dto.ProjectDefinitionDto;
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

    /**
     * Le @CircuitBreaker est ici (couche service, pas sur l'interface Feign) car :
     * - Spring AOP proxy CGLIB sur une classe concrète = interception garantie
     * - Sur une interface Feign, le conflit avec feign.circuitbreaker interne
     *   fait que le fallback n'est jamais appelé.
     */
    @CircuitBreaker(name = "dataset-manager", fallbackMethod = "previewFallback")
    public PreviewResponse preview(Long projectId) {
        ProjectDefinitionDto project = managerClient.getProject(projectId);

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

    /**
     * Fallback du circuit breaker "dataset-manager".
     * Distingue deux cas :
     *  - 404 NotFound → le projet n'existe pas (erreur métier, pas une panne)
     *  - tout autre exception → dataset-manager-service réellement indisponible
     */
    PreviewResponse previewFallback(Long projectId, Exception e) {
        String message = (e instanceof FeignException.NotFound)
                ? "Projet " + projectId + " introuvable dans dataset-manager-service."
                : "dataset-manager-service momentanément indisponible. Réessayez plus tard.";
        return new PreviewResponse(projectId, Collections.emptyMap(), "PARTIAL", message);
    }

    private int resolveSize(EntityDefinitionDto entity, ProjectDefinitionDto project) {
        return entity.getRowCount() != null ? entity.getRowCount() : project.getSize();
    }
}
