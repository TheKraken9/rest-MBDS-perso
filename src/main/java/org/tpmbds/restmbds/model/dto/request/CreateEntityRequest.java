package org.tpmbds.restmbds.model.dto.request;

import jakarta.validation.Valid;
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

    /**
     * Sous-entités rattachées à cette entité (optionnel).
     * Ex : un User peut avoir plusieurs Contact.
     * Chaque sous-entité a son propre rowCount : nombre d'occurrences
     * générées PAR ligne parente.
     */
    @Valid
    private List<CreateEntityRequest> subEntities;
}
