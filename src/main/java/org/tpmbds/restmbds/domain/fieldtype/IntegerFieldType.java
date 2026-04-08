package org.tpmbds.restmbds.domain.fieldtype;

import org.springframework.stereotype.Component;
import org.tpmbds.restmbds.domain.constraint.Constraint;
import org.tpmbds.restmbds.domain.constraint.RangeConstraint;
import org.tpmbds.restmbds.domain.generator.DataGenerator;
import org.tpmbds.restmbds.domain.generator.RandomIntegerGenerator;

import java.util.Map;

@Component
public class IntegerFieldType implements FieldType {

    private final RandomIntegerGenerator generator;

    public IntegerFieldType(RandomIntegerGenerator generator) {
        this.generator = generator;
    }

    @Override
    public String code() {
        return "INTEGER";
    }

    @Override
    public DataGenerator getGenerator() {
        return generator;
    }

    @Override
    public Constraint createConstraint(Map<String, Object> config) {
        double min = ((Number) config.getOrDefault("min", 0)).doubleValue();
        double max = ((Number) config.getOrDefault("max", 100)).doubleValue();
        return new RangeConstraint(min, max);
    }
}