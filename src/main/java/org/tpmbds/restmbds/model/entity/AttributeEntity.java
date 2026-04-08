package org.tpmbds.restmbds.model.entity;

import jakarta.persistence.*;
import org.tpmbds.restmbds.model.constraint.ConstraintConfig;

@Entity
public class AttributeEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String fieldTypeCode;

    @Transient
    private ConstraintConfig constraintConfig;

    @ManyToOne
    private EntityModelEntity entityModel;

}