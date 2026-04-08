package org.tpmbds.restmbds.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.tpmbds.restmbds.project.entity.DatasetProjectEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "entity_models")
@Getter @Setter
public class EntityModelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    /**
     * Nombre de lignes à générer pour cette entité.
     * Nullable : si absent, on se rabat sur la taille du projet (project.size).
     * Pour une sous-entité c'est le nombre d'occurrences PAR ligne parente.
     */
    @Column(nullable = true)
    private Integer rowCount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id")
    private DatasetProjectEntity project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_entity_id")
    private EntityModelEntity parentEntity;

    /**
     * orphanRemoval = true : quand on vide la liste (updateEntity),
     * les anciennes sous-entités sont bien supprimées en base.
     */
    @OneToMany(mappedBy = "parentEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EntityModelEntity> subEntities = new ArrayList<>();

    @OneToMany(mappedBy = "entityModel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttributeEntity> attributes = new ArrayList<>();
}
