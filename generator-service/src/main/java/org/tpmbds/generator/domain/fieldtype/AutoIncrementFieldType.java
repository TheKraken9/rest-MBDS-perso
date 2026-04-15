package org.tpmbds.generator.domain.fieldtype;
import org.springframework.stereotype.Component;
import org.tpmbds.generator.domain.constraint.Constraint;
import org.tpmbds.generator.domain.constraint.NoConstraint;
import org.tpmbds.generator.domain.generator.AutoIncrementGenerator;
import org.tpmbds.generator.domain.generator.DataGenerator;
import java.util.Map;
@Component
public class AutoIncrementFieldType extends FieldType {
    private final AutoIncrementGenerator generator;
    public AutoIncrementFieldType(AutoIncrementGenerator g) { this.generator = g; }
    @Override public String code() { return "AUTOINCREMENT"; }
    @Override public DataGenerator getGenerator() { return generator; }
    @Override public Constraint createConstraint(Map<String, Object> config) { return new NoConstraint(); }
}
