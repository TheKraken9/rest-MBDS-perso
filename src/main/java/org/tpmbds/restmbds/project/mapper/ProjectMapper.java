package org.tpmbds.restmbds.project.mapper;

import org.springframework.stereotype.Component;
import org.tpmbds.restmbds.model.dto.response.EntityResponse;
import org.tpmbds.restmbds.project.dto.request.CreateProjectRequest;
import org.tpmbds.restmbds.project.dto.request.UpdateProjectRequest;
import org.tpmbds.restmbds.project.dto.response.ProjectResponse;
import org.tpmbds.restmbds.project.dto.response.ProjectSummaryResponse;
import org.tpmbds.restmbds.project.entity.DatasetProjectEntity;

import java.util.List;

@Component
public class ProjectMapper {

    public DatasetProjectEntity toEntity(CreateProjectRequest request) {
        DatasetProjectEntity entity = new DatasetProjectEntity();
        entity.setName(request.getName());
        entity.setSize(request.getSize());
        return entity;
    }

    public void updateEntity(DatasetProjectEntity entity, UpdateProjectRequest request) {
        entity.setName(request.getName());
        entity.setSize(request.getSize());
    }

    public ProjectResponse toResponse(DatasetProjectEntity entity, List<EntityResponse> entityResponses) {
        return new ProjectResponse(
                entity.getId(),
                entity.getName(),
                entity.getSize(),
                entityResponses
        );
    }

    public ProjectSummaryResponse toSummaryResponse(DatasetProjectEntity entity) {
        return new ProjectSummaryResponse(
                entity.getId(),
                entity.getName(),
                entity.getSize()
        );
    }
}
