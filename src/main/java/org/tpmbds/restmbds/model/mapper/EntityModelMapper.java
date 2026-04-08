package org.tpmbds.restmbds.model.mapper;

import org.springframework.stereotype.Component;
import org.tpmbds.restmbds.model.dto.request.AttributeRequest;
import org.tpmbds.restmbds.model.dto.request.CreateEntityRequest;
import org.tpmbds.restmbds.model.dto.request.UpdateEntityRequest;
import org.tpmbds.restmbds.model.dto.response.AttributeResponse;
import org.tpmbds.restmbds.model.dto.response.EntityResponse;
import org.tpmbds.restmbds.model.entity.AttributeEntity;
import org.tpmbds.restmbds.model.entity.EntityModelEntity;
import org.tpmbds.restmbds.project.entity.DatasetProjectEntity;

import java.util.ArrayList;
import java.util.List;

@Component
public class EntityModelMapper {

    private final AttributeMapper attributeMapper;

    public EntityModelMapper(AttributeMapper attributeMapper) {
        this.attributeMapper = attributeMapper;
    }

    public EntityModelEntity toEntity(CreateEntityRequest request, DatasetProjectEntity project) {
        EntityModelEntity entity = new EntityModelEntity();
        entity.setName(request.getName());
        entity.setRowCount(request.getRowCount());
        entity.setProject(project);

        if (request.getFields() != null) {
            List<AttributeEntity> attributes = new ArrayList<>();
            for (AttributeRequest field : request.getFields()) {
                attributes.add(attributeMapper.toEntity(field, entity));
            }
            entity.setAttributes(attributes);
        }

        return entity;
    }

    public void updateEntity(EntityModelEntity entity, UpdateEntityRequest request) {
        entity.setName(request.getName());
        entity.setRowCount(request.getRowCount());
        entity.getAttributes().clear();

        if (request.getFields() != null) {
            for (AttributeRequest field : request.getFields()) {
                entity.getAttributes().add(attributeMapper.toEntity(field, entity));
            }
        }
    }

    public EntityResponse toResponse(EntityModelEntity entity) {
        List<AttributeResponse> fields = entity.getAttributes().stream()
                .map(attributeMapper::toResponse)
                .toList();

        return new EntityResponse(
                entity.getId(),
                entity.getName(),
                entity.getRowCount(),
                fields
        );
    }
}
