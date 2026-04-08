package org.tpmbds.restmbds.domain.generator;

import org.springframework.stereotype.Component;
import org.tpmbds.restmbds.model.entity.AttributeEntity;

import java.util.UUID;

@Component
public class EmailGenerator implements DataGenerator {

    @Override
    public Object generate(AttributeEntity attribute) {
        return "user" + UUID.randomUUID().toString().substring(0, 5) + "@example.com";
    }
}