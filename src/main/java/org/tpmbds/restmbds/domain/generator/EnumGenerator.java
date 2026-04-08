package org.tpmbds.restmbds.domain.generator;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class EnumGenerator implements DataGenerator {

    private final Random random = new Random();

    @Override
    @SuppressWarnings("unchecked")
    public Object generate(Map<String, Object> config, int rowIndex) {
        List<String> values = (List<String>) config.get("values");
        if (values == null || values.isEmpty()) {
            return "UNKNOWN";
        }

        List<Number> probabilities = (List<Number>) config.get("probabilities");
        if (probabilities != null && probabilities.size() == values.size()) {
            return pickWithProbability(values, probabilities);
        }

        return values.get(random.nextInt(values.size()));
    }

    private String pickWithProbability(List<String> values, List<Number> probabilities) {
        double r = random.nextDouble();
        double cumulative = 0.0;
        for (int i = 0; i < values.size(); i++) {
            cumulative += probabilities.get(i).doubleValue();
            if (r <= cumulative) {
                return values.get(i);
            }
        }
        return values.getLast();
    }
}
