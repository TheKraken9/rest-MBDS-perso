package org.tpmbds.restmbds.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "attributes")
@Getter @Setter
public class AttributeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String fieldTypeCode;

    /**
     * Configuration de contrainte sérialisée en JSON.
     * Stockée en base pour être rechargée lors de la génération.
     * Ex : {"min":18,"max":90,"distribution":"uniform"}
     */
    @Column(columnDefinition = "TEXT")
    private String constraintJson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_model_id")
    private EntityModelEntity entityModel;
}
