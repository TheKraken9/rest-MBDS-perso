package org.tpmbds.manager.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter @Setter
public class AttributeRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String type;
    private Map<String, Object> constraints;
}
