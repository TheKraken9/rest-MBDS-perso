package org.tpmbds.manager.model.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tpmbds.manager.common.exception.BadRequestException;
import org.tpmbds.manager.common.exception.ResourceNotFoundException;
import org.tpmbds.manager.model.dto.request.AttributeRequest;
import org.tpmbds.manager.model.dto.request.CreateEntityRequest;
import org.tpmbds.manager.model.dto.request.UpdateEntityRequest;
import org.tpmbds.manager.model.dto.response.EntityResponse;
import org.tpmbds.manager.model.entity.EntityModelEntity;
import org.tpmbds.manager.model.mapper.EntityModelMapper;
import org.tpmbds.manager.model.repository.EntityModelRepository;
import org.tpmbds.manager.project.entity.DatasetProjectEntity;
import org.tpmbds.manager.project.repository.ProjectRepository;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class EntityModelService {

    private static final Set<String> VALID_TYPES = Set.of(
            "STRING", "INTEGER", "FLOAT", "BOOLEAN", "DATE", "ENUM",
            "EMAIL", "AUTOINCREMENT", "NAME"
    );

    private final EntityModelRepository entityRepository;
    private final ProjectRepository projectRepository;
    private final EntityModelMapper entityModelMapper;

    public EntityModelService(EntityModelRepository entityRepository,
                               ProjectRepository projectRepository,
                               EntityModelMapper entityModelMapper) {
        this.entityRepository = entityRepository;
        this.projectRepository = projectRepository;
        this.entityModelMapper = entityModelMapper;
    }

    public EntityResponse create(Long projectId, CreateEntityRequest request) {
        DatasetProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));
        validateFieldTypesRecursive(request);
        return entityModelMapper.toResponse(entityRepository.save(entityModelMapper.toEntity(request, project)));
    }

    @Transactional(readOnly = true)
    public List<EntityResponse> list(Long projectId) {
        if (!projectRepository.existsById(projectId))
            throw new ResourceNotFoundException("Project not found: " + projectId);
        return entityRepository.findByProjectIdAndParentEntityIsNull(projectId).stream()
                .map(entityModelMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EntityResponse getById(Long id) {
        return entityModelMapper.toResponse(findOrThrow(id));
    }

    public EntityResponse update(Long id, UpdateEntityRequest request) {
        EntityModelEntity entity = findOrThrow(id);
        validateFieldTypesRecursive(request);
        entityModelMapper.updateEntity(entity, request);
        return entityModelMapper.toResponse(entity);
    }

    public void delete(Long id) {
        if (!entityRepository.existsById(id)) throw new ResourceNotFoundException("Entity not found: " + id);
        entityRepository.deleteById(id);
    }

    private void validateFieldTypesRecursive(CreateEntityRequest request) {
        validateFields(request.getFields());
        if (request.getSubEntities() != null) request.getSubEntities().forEach(this::validateFieldTypesRecursive);
    }

    private void validateFieldTypesRecursive(UpdateEntityRequest request) {
        validateFields(request.getFields());
        if (request.getSubEntities() != null) request.getSubEntities().forEach(this::validateFieldTypesRecursive);
    }

    private void validateFields(List<AttributeRequest> fields) {
        if (fields == null) return;
        for (AttributeRequest field : fields) {
            if (!VALID_TYPES.contains(field.getType().toUpperCase())) {
                throw new BadRequestException("Unknown field type: " + field.getType());
            }
        }
    }

    private EntityModelEntity findOrThrow(Long id) {
        return entityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entity not found: " + id));
    }
}
