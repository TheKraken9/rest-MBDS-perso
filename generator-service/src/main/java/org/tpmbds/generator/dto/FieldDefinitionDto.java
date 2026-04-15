package org.tpmbds.generator.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter @Setter @NoArgsConstructor
public class FieldDefinitionDto {
    private Long id;
    private String name;
    private String type;
    private Map<String, Object> constraints;
}
