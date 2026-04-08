package org.tpmbds.restmbds.dataset.service;

import org.springframework.stereotype.Service;
import org.tpmbds.restmbds.common.exception.ResourceNotFoundException;
import org.tpmbds.restmbds.dataset.dto.response.PreviewResponse;
import org.tpmbds.restmbds.domain.constraint.Constraint;
import org.tpmbds.restmbds.domain.fieldtype.FieldType;
import org.tpmbds.restmbds.domain.fieldtype.FieldTypeRegistry;
import org.tpmbds.restmbds.model.entity.AttributeEntity;
import org.tpmbds.restmbds.model.entity.EntityModelEntity;
import org.tpmbds.restmbds.model.mapper.AttributeMapper;
import org.tpmbds.restmbds.project.entity.DatasetProjectEntity;
import org.tpmbds.restmbds.project.repository.ProjectRepository;

import java.util.*;

@Service
public class DatasetService {

    private static final int MAX_RETRIES = 100;

    private final ProjectRepository repo;
    private final FieldTypeRegistry registry;
    private final AttributeMapper attributeMapper;

    public DatasetService(ProjectRepository repo,
                          FieldTypeRegistry registry,
                          AttributeMapper attributeMapper) {
        this.repo = repo;
        this.registry = registry;
        this.attributeMapper = attributeMapper;
    }

    public PreviewResponse preview(Long projectId) {
        DatasetProjectEntity project = repo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        Map<String, List<Map<String, Object>>> data = new LinkedHashMap<>();

        // On ne traite que les entités racines (sans parent)
        project.getEntities().stream()
                .filter(e -> e.getParentEntity() == null)
                .forEach(entity -> {
                    int size = resolveSize(entity, project);
                    List<Map<String, Object>> rows = new ArrayList<>(size);

                    for (int rowIndex = 0; rowIndex < size; rowIndex++) {
                        Map<String, Object> row = generateRow(entity, rowIndex);

                        // Embed les sous-entités comme tableau dans la ligne parente
                        for (EntityModelEntity sub : entity.getSubEntities()) {
                            row.put(sub.getName(), generateSubRows(sub, project));
                        }

                        rows.add(row);
                    }

                    data.put(entity.getName(), rows);
                });

        return new PreviewResponse(projectId, data);
    }

    // ─── Génération d'une liste de lignes pour une sous-entité ───────────────

    private List<Map<String, Object>> generateSubRows(EntityModelEntity subEntity,
                                                       DatasetProjectEntity project) {
        int size = resolveSize(subEntity, project);
        List<Map<String, Object>> rows = new ArrayList<>(size);

        for (int rowIndex = 0; rowIndex < size; rowIndex++) {
            Map<String, Object> row = generateRow(subEntity, rowIndex);

            // Récursion : sous-sous-entités
            for (EntityModelEntity nested : subEntity.getSubEntities()) {
                row.put(nested.getName(), generateSubRows(nested, project));
            }

            rows.add(row);
        }

        return rows;
    }

    // ─── Génération d'une ligne (champs uniquement, sans sous-entités) ────────

    private Map<String, Object> generateRow(EntityModelEntity entity, int rowIndex) {
        Map<String, Object> row = new LinkedHashMap<>();

        for (AttributeEntity attr : entity.getAttributes()) {
            FieldType fieldType = registry.getByCode(attr.getFieldTypeCode());
            Map<String, Object> config = attributeMapper.deserializeConstraints(attr.getConstraintJson());

            Constraint constraint = fieldType.createConstraint(config);
            var generator = fieldType.getGenerator();

            Object value = null;
            for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
                value = generator.generate(config, rowIndex);
                if (constraint.isValid(value)) break;
            }

            row.put(attr.getName(), value);
        }

        return row;
    }

    // ─── Taille d'une entité : rowCount propre ou taille du projet ────────────

    private int resolveSize(EntityModelEntity entity, DatasetProjectEntity project) {
        return entity.getRowCount() != null ? entity.getRowCount() : project.getSize();
    }
}
