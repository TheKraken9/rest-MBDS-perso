package org.tpmbds.restmbds.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tpmbds.restmbds.project.entity.DatasetProjectEntity;

public interface ProjectRepository extends JpaRepository<DatasetProjectEntity, Long> {
}