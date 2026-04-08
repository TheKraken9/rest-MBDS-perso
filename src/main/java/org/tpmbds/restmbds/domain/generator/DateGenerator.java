package org.tpmbds.restmbds.domain.generator;

import org.springframework.stereotype.Component;
import org.tpmbds.restmbds.model.entity.AttributeEntity;

import java.time.LocalDate;
import java.util.Random;

@Component
public class DateGenerator implements DataGenerator {

    private final Random random = new Random();

    @Override
    public Object generate(AttributeEntity attribute) {
        return LocalDate.now().minusDays(random.nextInt(1000));
    }
}