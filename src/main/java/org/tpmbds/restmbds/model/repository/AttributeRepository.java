package org.tpmbds.restmbds.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tpmbds.restmbds.model.entity.AttributeEntity;

public interface AttributeRepository extends JpaRepository<AttributeEntity, Long> {
}