package org.tpmbds.manager.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AttributeResponse {
    private Long id;
    private String name;
    private String type;
    private Map<String, Object> constraints;
}
