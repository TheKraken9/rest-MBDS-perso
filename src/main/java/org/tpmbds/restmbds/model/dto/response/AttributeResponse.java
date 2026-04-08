package org.tpmbds.restmbds.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttributeResponse {
    private Long id;
    private String name;
    private String type;
    private Map<String, Object> constraints;
}
