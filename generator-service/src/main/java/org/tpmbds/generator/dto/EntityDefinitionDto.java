package org.tpmbds.generator.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter @NoArgsConstructor
public class EntityDefinitionDto {
    private Long id;
    private String name;
    private Integer rowCount;
    private List<FieldDefinitionDto> fields;
    private List<EntityDefinitionDto> subEntities;
}
