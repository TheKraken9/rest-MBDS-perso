package org.tpmbds.restmbds.domain.generator;

import org.springframework.stereotype.Component;
import org.tpmbds.restmbds.model.entity.AttributeEntity;

import java.util.List;
import java.util.Random;

@Component
public class EnumGenerator implements DataGenerator {

    private final Random random = new Random();

    @Override
    public Object generate(AttributeEntity attribute) {
        // fallback, vraie valeur vient du constraint
        return "DEFAULT";
    }

    public String generateFromValues(List<String> values) {
        return values.get(random.nextInt(values.size()));
    }
}