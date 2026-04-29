package org.tpmbds.manager.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.tpmbds.manager.model.dto.response.EntityResponse;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponse {
    private Long id;
    private String name;
    private Integer size;
    private List<EntityResponse> entities;
}
