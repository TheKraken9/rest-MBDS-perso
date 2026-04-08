package org.tpmbds.restmbds.model.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tpmbds.restmbds.common.exception.ResourceNotFoundException;
import org.tpmbds.restmbds.domain.fieldtype.FieldTypeRegistry;
import org.tpmbds.restmbds.model.constraint.ConstraintConfig;
import org.tpmbds.restmbds.model.dto.request.AttributeRequest;
import org.tpmbds.restmbds.model.dto.request.CreateEntityRequest;
import org.tpmbds.restmbds.model.dto.request.UpdateEntityRequest;
import org.tpmbds.restmbds.model.dto.response.AttributeResponse;
import org.tpmbds.restmbds.model.dto.response.EntityResponse;
import org.tpmbds.restmbds.model.entity.AttributeEntity;
import org.tpmbds.restmbds.model.entity.EntityModelEntity;
import org.tpmbds.restmbds.model.repository.EntityModelRepository;
import org.tpmbds.restmbds.project.entity.DatasetProjectEntity;
import org.tpmbds.restmbds.project.repository.ProjectRepository;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EntityModelService {

    private final EntityModelRepository entityRepository;
    private final ProjectRepository projectRepository;
    private final FieldTypeRegistry fieldTypeRegistry;
    private final ObjectMapper objectMapper;

    public EntityModelService(EntityModelRepository entityRepository,
                              ProjectRepository projectRepository,
                              FieldTypeRegistry fieldTypeRegistry,
                              ObjectMapper objectMapper) {
        this.entityRepository = entityRepository;
        this.projectRepository = projectRepository;
        this.fieldTypeRegistry = fieldTypeRegistry;
        this.objectMapper = objectMapper;
    }

    public EntityResponse create(Long projectId, CreateEntityRequest request) {
        DatasetProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + projectId));

        EntityModelEntity entity = new EntityModelEntity();
        entity.setName(request.getName());
        entity.setRowCount(request.getRowCount());
        entity.setProject(project);

        if (request.getFields() != null) {
            for (AttributeRequest field : request.getFields()) {
                fieldTypeRegistry.getByCode(field.getType());

                AttributeEntity attribute = new AttributeEntity();
                attribute.setName(field.getName());
                attribute.setFieldTypeCode(field.getType().toUpperCase());
                attribute.setConstraintJson(toJson(field.getConstraints()));
                attribute.setEntityModel(entity);

                entity.getAttributes().add(attribute);
            }
        }

        EntityModelEntity saved = entityRepository.save(entity);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<EntityResponse> list(Long projectId) {
        return entityRepository.findByProjectId(projectId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EntityResponse getById(Long id) {
        EntityModelEntity entity = entityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entity not found with id " + id));
        return toResponse(entity);
    }

    public EntityResponse update(Long id, UpdateEntityRequest request) {
        EntityModelEntity entity = entityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entity not found with id " + id));

        entity.setName(request.getName());
        entity.setRowCount(request.getRowCount());
        entity.getAttributes().clear();

        if (request.getFields() != null) {
            for (AttributeRequest field : request.getFields()) {
                fieldTypeRegistry.getByCode(field.getType());

                AttributeEntity attribute = new AttributeEntity();
                attribute.setName(field.getName());
                attribute.setFieldTypeCode(field.getType().toUpperCase());
                attribute.setConstraintJson(toJson(field.getConstraints()));
                attribute.setEntityModel(entity);

                entity.getAttributes().add(attribute);
            }
        }

        return toResponse(entity);
    }

    public void delete(Long id) {
        if (!entityRepository.existsById(id)) {
            throw new ResourceNotFoundException("Entity not found with id " + id);
        }
        entityRepository.deleteById(id);
    }

    private EntityResponse toResponse(EntityModelEntity entity) {
        List<AttributeResponse> fields = entity.getAttributes().stream()
                .map(a -> new AttributeResponse(
                        a.getId(),
                        a.getName(),
                        a.getFieldTypeCode(),
                        fromJson(a.getConstraintJson())
                ))
                .toList();

        return new EntityResponse(entity.getId(), entity.getName(), entity.getRowCount(), fields);
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize constraint config", e);
        }
    }

    private Map<String, Object> fromJson(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize constraint config", e);
        }
    }

    private ConstraintConfig mapConstraint(AttributeRequest req) {

        ConstraintConfig config = new ConstraintConfig();

        if (req.constraints == null) return config;

        if (req.constraints.get("min") != null)
            config.min = ((Number) req.constraints.get("min")).doubleValue();

        if (req.constraints.get("max") != null)
            config.max = ((Number) req.constraints.get("max")).doubleValue();

        if (req.constraints.get("values") != null)
            config.values = (List<String>) req.constraints.get("values");

        if (req.constraints.get("domain") != null)
            config.domain = (String) req.constraints.get("domain");

        if (req.constraints.get("distribution") != null)
            config.distribution = (String) req.constraints.get("distribution");

        return config;
    }
}