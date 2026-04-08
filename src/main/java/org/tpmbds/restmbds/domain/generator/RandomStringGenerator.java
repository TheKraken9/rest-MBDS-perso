package org.tpmbds.restmbds.domain.generator;

import org.springframework.stereotype.Component;
import org.tpmbds.restmbds.model.entity.AttributeEntity;

import java.util.UUID;

@Component
public class RandomStringGenerator implements DataGenerator {

    @Override
    public Object generate(AttributeEntity attribute) {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}