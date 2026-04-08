package org.tpmbds.restmbds.project.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tpmbds.restmbds.common.exception.ResourceNotFoundException;
import org.tpmbds.restmbds.model.dto.response.EntityResponse;
import org.tpmbds.restmbds.model.mapper.EntityModelMapper;
import org.tpmbds.restmbds.project.dto.request.CreateProjectRequest;
import org.tpmbds.restmbds.project.dto.request.UpdateProjectRequest;
import org.tpmbds.restmbds.project.dto.response.ProjectResponse;
import org.tpmbds.restmbds.project.dto.response.ProjectSummaryResponse;
import org.tpmbds.restmbds.project.entity.DatasetProjectEntity;
import org.tpmbds.restmbds.project.mapper.ProjectMapper;
import org.tpmbds.restmbds.project.repository.ProjectRepository;

import java.util.List;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository repo;
    private final ProjectMapper projectMapper;
    private final EntityModelMapper entityModelMapper;

    public ProjectService(ProjectRepository repo,
                          ProjectMapper projectMapper,
                          EntityModelMapper entityModelMapper) {
        this.repo = repo;
        this.projectMapper = projectMapper;
        this.entityModelMapper = entityModelMapper;
    }

    public ProjectResponse create(CreateProjectRequest request) {
        DatasetProjectEntity saved = repo.save(projectMapper.toEntity(request));
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ProjectSummaryResponse> getAll() {
        return repo.findAll().stream()
                .map(projectMapper::toSummaryResponse)
                .toList();
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
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Project not found with id " + id);
        }
        repo.deleteById(id);
    }

    private ProjectResponse toResponse(DatasetProjectEntity project) {
        List<EntityResponse> entityResponses = project.getEntities().stream()
                .map(entityModelMapper::toResponse)
                .toList();
        return projectMapper.toResponse(project, entityResponses);
    }

    private DatasetProjectEntity findOrThrow(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + id));
    }
}
