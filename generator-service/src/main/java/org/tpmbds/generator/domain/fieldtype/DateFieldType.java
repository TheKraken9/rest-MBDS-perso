package org.tpmbds.generator.domain.fieldtype;
import org.springframework.stereotype.Component;
import org.tpmbds.generator.domain.constraint.Constraint;
import org.tpmbds.generator.domain.constraint.NoConstraint;
import org.tpmbds.generator.domain.generator.DataGenerator;
import org.tpmbds.generator.domain.generator.DateGenerator;
import java.util.Map;
@Component
public class DateFieldType extends FieldType {
    private final DateGenerator generator;
    public DateFieldType(DateGenerator g) { this.generator = g; }
    @Override public String code() { return "DATE"; }
    @Override public DataGenerator getGenerator() { return generator; }
    @Override public Constraint createConstraint(Map<String, Object> config) { return new NoConstraint(); }
}
