package org.tpmbds.manager.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EntityResponse {
    private Long id;
    private String name;
    private Integer rowCount;
    private List<AttributeResponse> fields;
    private List<EntityResponse> subEntities;
}
