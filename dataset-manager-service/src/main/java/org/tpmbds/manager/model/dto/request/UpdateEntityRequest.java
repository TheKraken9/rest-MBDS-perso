package org.tpmbds.manager.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class UpdateEntityRequest {
    @NotBlank
    private String name;
    private Integer rowCount;
    @Valid
    private List<AttributeRequest> fields;
    @Valid
    private List<CreateEntityRequest> subEntities;
}
