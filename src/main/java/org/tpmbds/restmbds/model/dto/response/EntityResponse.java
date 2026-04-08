package org.tpmbds.restmbds.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EntityResponse {
    private Long id;
    private String name;
    private Integer rowCount;
    private List<AttributeResponse> fields;
}
