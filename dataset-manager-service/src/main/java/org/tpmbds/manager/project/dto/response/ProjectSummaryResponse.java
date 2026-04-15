package org.tpmbds.manager.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectSummaryResponse {
    private Long id;
    private String name;
    private Integer size;
}
