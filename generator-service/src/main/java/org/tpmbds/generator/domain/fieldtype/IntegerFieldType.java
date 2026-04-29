package org.tpmbds.generator.domain.fieldtype;
import org.springframework.stereotype.Component;
import org.tpmbds.generator.domain.constraint.Constraint;
import org.tpmbds.generator.domain.constraint.RangeConstraint;
import org.tpmbds.generator.domain.generator.DataGenerator;
import org.tpmbds.generator.domain.generator.RandomIntegerGenerator;
import java.util.Map;
@Component
public class IntegerFieldType extends FieldType {
    private final RandomIntegerGenerator generator;
    public IntegerFieldType(RandomIntegerGenerator g) { this.generator = g; }
    @Override public String code() { return "INTEGER"; }
    @Override public DataGenerator getGenerator() { return generator; }
    @Override public Constraint createConstraint(Map<String, Object> config) {
        double min = ((Number) config.getOrDefault("min", 0)).doubleValue();
        double max = ((Number) config.getOrDefault("max", 100)).doubleValue();
        return new RangeConstraint(min, max);
    }
}
