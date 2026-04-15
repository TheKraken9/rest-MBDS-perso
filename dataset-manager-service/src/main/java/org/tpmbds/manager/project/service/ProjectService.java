package org.tpmbds.manager.project.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tpmbds.manager.common.exception.ResourceNotFoundException;
import org.tpmbds.manager.model.dto.response.EntityResponse;
import org.tpmbds.manager.model.mapper.EntityModelMapper;
import org.tpmbds.manager.project.dto.request.CreateProjectRequest;
import org.tpmbds.manager.project.dto.request.UpdateProjectRequest;
import org.tpmbds.manager.project.dto.response.ProjectResponse;
import org.tpmbds.manager.project.dto.response.ProjectSummaryResponse;
import org.tpmbds.manager.project.entity.DatasetProjectEntity;
import org.tpmbds.manager.project.mapper.ProjectMapper;
import org.tpmbds.manager.project.repository.ProjectRepository;

import java.util.List;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository repo;
    private final ProjectMapper projectMapper;
    private final EntityModelMapper entityModelMapper;

    public ProjectService(ProjectRepository repo, ProjectMapper projectMapper,
                          EntityModelMapper entityModelMapper) {
        this.repo = repo;
        this.projectMapper = projectMapper;
        this.entityModelMapper = entityModelMapper;
    }

    public ProjectResponse create(CreateProjectRequest request) {
        return toResponse(repo.save(projectMapper.toEntity(request)));
    }

    @Transactional(readOnly = true)
    public List<ProjectSummaryResponse> getAll() {
        return repo.findAll().stream().map(projectMapper::toSummaryResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public ProjectResponse update(Long id, UpdateProjectRequest request) {
        DatasetProjectEntity project = findOrThrow(id);
        projectMapper.updateEntity(project, request);
        return toResponse(project);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Project not found: " + id);
        repo.deleteById(id);
    }

    private ProjectResponse toResponse(DatasetProjectEntity project) {
        List<EntityResponse> entities = project.getEntities().stream()
                .filter(e -> e.getParentEntity() == null)
                .map(entityModelMapper::toResponse).toList();
        return projectMapper.toResponse(project, entities);
    }

    private DatasetProjectEntity findOrThrow(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));
    }
}
