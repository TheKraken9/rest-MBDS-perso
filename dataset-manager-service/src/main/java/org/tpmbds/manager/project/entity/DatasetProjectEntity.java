package org.tpmbds.manager.project.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.tpmbds.manager.model.entity.EntityModelEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "projects")
public class DatasetProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer size;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EntityModelEntity> entities = new ArrayList<>();
}
