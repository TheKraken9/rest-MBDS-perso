package org.tpmbds.restmbds.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tpmbds.restmbds.model.entity.EntityModelEntity;

import java.util.List;

public interface EntityModelRepository extends JpaRepository<EntityModelEntity, Long> {

    /** Toutes les entités d'un projet (racines + sous-entités). */
    List<EntityModelEntity> findByProjectId(Long projectId);

    /** Uniquement les entités racines d'un projet (sans parent). */
    List<EntityModelEntity> findByProjectIdAndParentEntityIsNull(Long projectId);
}
