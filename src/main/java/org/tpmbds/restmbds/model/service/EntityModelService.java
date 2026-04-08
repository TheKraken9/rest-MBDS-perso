package org.tpmbds.restmbds.model.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tpmbds.restmbds.common.exception.ResourceNotFoundException;
import org.tpmbds.restmbds.domain.fieldtype.FieldTypeRegistry;
import org.tpmbds.restmbds.model.dto.request.AttributeRequest;
import org.tpmbds.restmbds.model.dto.request.CreateEntityRequest;
import org.tpmbds.restmbds.model.dto.request.UpdateEntityRequest;
import org.tpmbds.restmbds.model.dto.response.EntityResponse;
import org.tpmbds.restmbds.model.entity.EntityModelEntity;
import org.tpmbds.restmbds.model.mapper.AttributeMapper;
import org.tpmbds.restmbds.model.mapper.EntityModelMapper;
import org.tpmbds.restmbds.model.repository.EntityModelRepository;
import org.tpmbds.restmbds.project.entity.DatasetProjectEntity;
import org.tpmbds.restmbds.project.repository.ProjectRepository;

import java.util.List;

@Service
@Transactional
public class EntityModelService {

    private final EntityModelRepository entityRepository;
    private final ProjectRepository projectRepository;
    private final FieldTypeRegistry fieldTypeRegistry;
    private final EntityModelMapper entityModelMapper;
    private final AttributeMapper attributeMapper;

    public EntityModelService(EntityModelRepository entityRepository,
                               ProjectRepository projectRepository,
                               FieldTypeRegistry fieldTypeRegistry,
                               EntityModelMapper entityModelMapper,
                               AttributeMapper attributeMapper) {
        this.entityRepository = entityRepository;
        this.projectRepository = projectRepository;
        this.fieldTypeRegistry = fieldTypeRegistry;
        this.entityModelMapper = entityModelMapper;
        this.attributeMapper = attributeMapper;
    }

    public EntityResponse create(Long projectId, CreateEntityRequest request) {
        DatasetProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        validateFieldTypes(request.getFields());

        EntityModelEntity entity = entityModelMapper.toEntity(request, project);
        return entityModelMapper.toResponse(entityRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<EntityResponse> list(Long projectId) {
        return entityRepository.findByProjectId(projectId).stream()
                .map(entityModelMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EntityResponse getById(Long id) {
        return entityModelMapper.toResponse(findOrThrow(id));
    }

    public EntityResponse update(Long id, UpdateEntityRequest request) {
        EntityModelEntity entity = findOrThrow(id);
        validateFieldTypes(request.getFields());
        entityModelMapper.updateEntity(entity, request);
        return entityModelMapper.toResponse(entity);
    }

    public void delete(Long id) {
        if (!entityRepository.existsById(id)) {
            throw new ResourceNotFoundException("Entity not found with id " + id);
        }
        entityRepository.deleteById(id);
    }

    private void validateFieldTypes(List<AttributeRequest> fields) {
        if (fields == null) return;
        for (AttributeRequest field : fields) {
            // Throws BadRequestException if the type is unknown
            fieldTypeRegistry.getByCode(field.getType());
        }
    }

    private EntityModelEntity findOrThrow(Long id) {
        return entityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entity not found with id " + id));
    }
}
