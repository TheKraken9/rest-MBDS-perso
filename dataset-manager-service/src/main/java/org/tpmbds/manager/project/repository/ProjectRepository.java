package org.tpmbds.manager.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tpmbds.manager.project.entity.DatasetProjectEntity;

public interface ProjectRepository extends JpaRepository<DatasetProjectEntity, Long> {
}
