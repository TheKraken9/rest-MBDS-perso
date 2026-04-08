package org.tpmbds.restmbds.domain.generator;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;

@Component
public class RandomIntegerGenerator implements DataGenerator {

    private final Random random = new Random();

    @Override
    public Object generate(Map<String, Object> config, int rowIndex) {
        int min = ((Number) config.getOrDefault("min", 0)).intValue();
        int max = ((Number) config.getOrDefault("max", 100)).intValue();
        if (min >= max) return min;
        return random.nextInt(max - min + 1) + min;
    }
}
