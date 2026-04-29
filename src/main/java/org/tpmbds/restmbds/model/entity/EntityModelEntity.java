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

    @Column(nullable = true)
    private Integer rowCount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id")
    private DatasetProjectEntity project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_entity_id")
    private EntityModelEntity parentEntity;

    @OneToMany(mappedBy = "parentEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EntityModelEntity> subEntities = new ArrayList<>();

    @OneToMany(mappedBy = "entityModel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttributeEntity> attributes = new ArrayList<>();
}
