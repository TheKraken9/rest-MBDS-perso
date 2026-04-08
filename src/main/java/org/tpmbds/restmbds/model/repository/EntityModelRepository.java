package org.tpmbds.restmbds.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tpmbds.restmbds.model.entity.EntityModelEntity;

import java.util.List;

public interface EntityModelRepository extends JpaRepository<EntityModelEntity, Long> {
    List<EntityModelEntity> findByProjectId(Long projectId);
}