package org.tpmbds.generator.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter @NoArgsConstructor
public class ProjectDefinitionDto {
    private Long id;
    private String name;
    private Integer size;
    private List<EntityDefinitionDto> entities;
}
