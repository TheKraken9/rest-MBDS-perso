package org.tpmbds.restmbds.domain.generator;

import org.tpmbds.restmbds.model.entity.AttributeEntity;

public interface DataGenerator {
    Object generate(AttributeEntity attribute);
}