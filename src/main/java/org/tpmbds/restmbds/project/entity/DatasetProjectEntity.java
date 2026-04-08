package org.tpmbds.restmbds.project.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.tpmbds.restmbds.model.entity.EntityModelEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter @Getter
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
