package org.tpmbds.restmbds.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class AttributeRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String type;

    @NotNull
    private Map<String, Object> constraints;
}