package org.tpmbds.manager.model.mapper;

import org.springframework.stereotype.Component;
import org.tpmbds.manager.model.dto.request.AttributeRequest;
import org.tpmbds.manager.model.dto.request.CreateEntityRequest;
import org.tpmbds.manager.model.dto.request.UpdateEntityRequest;
import org.tpmbds.manager.model.dto.response.AttributeResponse;
import org.tpmbds.manager.model.dto.response.EntityResponse;
import org.tpmbds.manager.model.entity.AttributeEntity;
import org.tpmbds.manager.model.entity.EntityModelEntity;
import org.tpmbds.manager.project.entity.DatasetProjectEntity;

import java.util.List;

@Component
public class EntityModelMapper {

    private final AttributeMapper attributeMapper;

    public EntityModelMapper(AttributeMapper attributeMapper) {
        this.attributeMapper = attributeMapper;
    }

    public EntityModelEntity toEntity(CreateEntityRequest request, DatasetProjectEntity project) {
        return buildEntity(request, project, null);
    }

    private EntityModelEntity buildEntity(CreateEntityRequest request,
                                          DatasetProjectEntity project,
                                          EntityModelEntity parent) {
        EntityModelEntity entity = new EntityModelEntity();
        entity.setName(request.getName());
        entity.setRowCount(request.getRowCount());
        entity.setProject(project);
        entity.setParentEntity(parent);

        if (request.getFields() != null) {
            for (AttributeRequest field : request.getFields()) {
                entity.getAttributes().add(attributeMapper.toEntity(field, entity));
            }
        }
        if (request.getSubEntities() != null) {
            for (CreateEntityRequest sub : request.getSubEntities()) {
                entity.getSubEntities().add(buildEntity(sub, project, entity));
            }
        }
        return entity;
    }

    public void updateEntity(EntityModelEntity entity, UpdateEntityRequest request) {
        entity.setName(request.getName());
        entity.setRowCount(request.getRowCount());
        entity.getAttributes().clear();
        entity.getSubEntities().clear();

        if (request.getFields() != null) {
            for (AttributeRequest field : request.getFields()) {
                entity.getAttributes().add(attributeMapper.toEntity(field, entity));
            }
        }
        if (request.getSubEntities() != null) {
            for (CreateEntityRequest sub : request.getSubEntities()) {
                entity.getSubEntities().add(buildEntity(sub, entity.getProject(), entity));
            }
        }
    }

    public EntityResponse toResponse(EntityModelEntity entity) {
        List<AttributeResponse> fields = entity.getAttributes().stream()
                .map(attributeMapper::toResponse).toList();
        List<EntityResponse> subs = entity.getSubEntities().stream()
                .map(this::toResponse).toList();
        return new EntityResponse(entity.getId(), entity.getName(), entity.getRowCount(), fields, subs);
    }
}
