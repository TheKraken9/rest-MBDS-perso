package org.tpmbds.restmbds.domain.generator;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;
import java.util.Random;

@Component
public class DateGenerator implements DataGenerator {

    private final Random random = new Random();

    @Override
    public Object generate(Map<String, Object> config, int rowIndex) {
        String from = (String) config.getOrDefault("from", LocalDate.now().minusYears(10).toString());
        String to   = (String) config.getOrDefault("to",   LocalDate.now().toString());

        LocalDate start = LocalDate.parse(from);
        LocalDate end   = LocalDate.parse(to);

        long days = end.toEpochDay() - start.toEpochDay();
        if (days <= 0) return start.toString();

        return start.plusDays(random.nextLong(days + 1)).toString();
    }
}