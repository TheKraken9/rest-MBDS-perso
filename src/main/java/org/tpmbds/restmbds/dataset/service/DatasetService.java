package org.tpmbds.restmbds.dataset.service;

import org.springframework.stereotype.Service;
import org.tpmbds.restmbds.domain.fieldtype.FieldTypeRegistry;
import org.tpmbds.restmbds.project.repository.ProjectRepository;
import tools.jackson.databind.ObjectMapper;

import java.util.*;

@Service
public class DatasetService {

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

    public Map<String, List<Map<String,Object>>> preview(Long id) {

        var project = repo.findById(id).orElseThrow();

        Map<String, List<Map<String,Object>>> result = new HashMap<>();

        for (var entity : project.getEntities()) {

            List<Map<String,Object>> rows = new ArrayList<>();

            int size = entity.getRowCount() != null
                    ? entity.getRowCount()
                    : project.getSize();

            for (int i = 0; i < size; i++) {

                Map<String,Object> row = new HashMap<>();

                for (var attr : entity.getAttributes()) {

                    var fieldType = registry.getByCode(attr.getFieldTypeCode());

                    Map<String,Object> config = fromJson(attr.getConstraintJson());

                    var constraint = fieldType.createConstraint(config);
                    var generator = fieldType.getGenerator();

                    Object value;

                    do {
                        value = generator.generate(attr);
                    } while (!constraint.isValid(value));

                    row.put(attr.getName(), value);
                }

                rows.add(row);
            }

            result.put(entity.getName(), rows);
        }

        return result;
    }

    private Map<String,Object> fromJson(String json) {
        try {
            return mapper.readValue(json, Map.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}