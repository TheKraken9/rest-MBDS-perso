package org.tpmbds.manager.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tpmbds.manager.model.entity.EntityModelEntity;

import java.util.List;

public interface EntityModelRepository extends JpaRepository<EntityModelEntity, Long> {
    List<EntityModelEntity> findByProjectId(Long projectId);
    List<EntityModelEntity> findByProjectIdAndParentEntityIsNull(Long projectId);
}
