package org.tpmbds.restmbds.dataset.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.tpmbds.restmbds.common.exception.ResourceNotFoundException;
import org.tpmbds.restmbds.dataset.dto.response.PreviewResponse;
import org.tpmbds.restmbds.domain.constraint.Constraint;
import org.tpmbds.restmbds.domain.fieldtype.FieldType;
import org.tpmbds.restmbds.domain.fieldtype.FieldTypeRegistry;
import org.tpmbds.restmbds.project.repository.ProjectRepository;

import java.util.*;

@Service
public class DatasetService {

    private static final int MAX_RETRIES = 100;

    private final ProjectRepository repo;
    private final FieldTypeRegistry registry;
    private final ObjectMapper mapper;

    public DatasetService(ProjectRepository repo,
                          FieldTypeRegistry registry,
                          ObjectMapper mapper) {
        this.repo = repo;
        this.registry = registry;
        this.mapper = mapper;
    }

    public PreviewResponse preview(Long projectId) {
        var project = repo.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        Map<String, List<Map<String, Object>>> data = new LinkedHashMap<>();

        for (var entity : project.getEntities()) {
            int size = entity.getRowCount() != null ? entity.getRowCount() : project.getSize();
            List<Map<String, Object>> rows = new ArrayList<>(size);

            for (int rowIndex = 0; rowIndex < size; rowIndex++) {
                Map<String, Object> row = new LinkedHashMap<>();

                for (var attr : entity.getAttributes()) {
                    FieldType fieldType = registry.getByCode(attr.getFieldTypeCode());
                    Map<String, Object> config = parseConstraintJson(attr.getConstraintJson());

                    Constraint constraint = fieldType.createConstraint(config);
                    var generator = fieldType.getGenerator();

                    Object value = null;
                    for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
                        value = generator.generate(config, rowIndex);
                        if (constraint.isValid(value)) break;
                    }

                    row.put(attr.getName(), value);
                }

                rows.add(row);
            }

            data.put(entity.getName(), rows);
        }

        return new PreviewResponse(projectId, data);
    }

    private Map<String, Object> parseConstraintJson(String json) {
        if (json == null || json.isBlank()) return Collections.emptyMap();
        try {
            return mapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}
