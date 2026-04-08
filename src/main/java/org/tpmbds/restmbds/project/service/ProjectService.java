package org.tpmbds.restmbds.project.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tpmbds.restmbds.common.exception.ResourceNotFoundException;
import org.tpmbds.restmbds.model.dto.response.EntityResponse;
import org.tpmbds.restmbds.project.dto.request.CreateProjectRequest;
import org.tpmbds.restmbds.project.dto.request.UpdateProjectRequest;
import org.tpmbds.restmbds.project.dto.response.ProjectResponse;
import org.tpmbds.restmbds.project.dto.response.ProjectSummaryResponse;
import org.tpmbds.restmbds.project.entity.DatasetProjectEntity;
import org.tpmbds.restmbds.project.repository.ProjectRepository;

import java.util.List;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository repo;

    public ProjectService(ProjectRepository repo) {
        this.repo = repo;
    }

    public ProjectResponse create(CreateProjectRequest req) {
        DatasetProjectEntity entity = new DatasetProjectEntity();
        entity.setName(req.getName());
        entity.setSize(req.getSize());

        DatasetProjectEntity saved = repo.save(entity);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ProjectSummaryResponse> getAll() {
        return repo.findAll().stream()
                .map(p -> new ProjectSummaryResponse(p.getId(), p.getName(), p.getSize()))
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse getById(Long id) {
        DatasetProjectEntity project = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + id));
        return toResponse(project);
    }

    public ProjectResponse update(Long id, UpdateProjectRequest req) {
        DatasetProjectEntity project = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + id));

        project.setName(req.getName());
        project.setSize(req.getSize());

        return toResponse(project);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Project not found with id " + id);
        }
        repo.deleteById(id);
    }

    private ProjectResponse toResponse(DatasetProjectEntity entity) {
        List<EntityResponse> entities = entity.getEntities().stream()
                .map(e -> new EntityResponse(e.getId(), e.getName(), e.getRowCount(), List.of()))
                .toList();

        return new ProjectResponse(entity.getId(), entity.getName(), entity.getSize(), entities);
    }
}