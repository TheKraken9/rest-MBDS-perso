package org.tpmbds.generator.domain.fieldtype;
import org.springframework.stereotype.Component;
import org.tpmbds.generator.domain.constraint.Constraint;
import org.tpmbds.generator.domain.constraint.NoConstraint;
import org.tpmbds.generator.domain.generator.DataGenerator;
import org.tpmbds.generator.domain.generator.NameGenerator;
import java.util.Map;
@Component
public class NameFieldType extends FieldType {
    private final NameGenerator generator;
    public NameFieldType(NameGenerator g) { this.generator = g; }
    @Override public String code() { return "NAME"; }
    @Override public DataGenerator getGenerator() { return generator; }
    @Override public Constraint createConstraint(Map<String, Object> config) { return new NoConstraint(); }
}
