package org.tpmbds.restmbds.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateEntityRequest {
    @NotBlank
    private String name;

    @Positive
    private Integer rowCount;

    @NotEmpty
    private List<AttributeRequest> fields;
}
