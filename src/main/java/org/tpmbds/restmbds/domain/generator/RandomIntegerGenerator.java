package org.tpmbds.restmbds.domain.generator;

import org.springframework.stereotype.Component;
import org.tpmbds.restmbds.model.entity.AttributeEntity;

import java.util.Random;

@Component
public class RandomIntegerGenerator implements DataGenerator {

    private final Random random = new Random();

    @Override
    public Object generate(AttributeEntity attribute) {
        return random.nextInt(100);
    }
}