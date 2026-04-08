package org.tpmbds.restmbds.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSummaryResponse {
    private Long id;
    private String name;
    private Integer size;
}
