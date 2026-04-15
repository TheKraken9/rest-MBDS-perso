package org.tpmbds.generator.domain.fieldtype;
import org.springframework.stereotype.Component;
import org.tpmbds.generator.domain.constraint.Constraint;
import org.tpmbds.generator.domain.constraint.EnumConstraint;
import org.tpmbds.generator.domain.generator.DataGenerator;
import org.tpmbds.generator.domain.generator.EnumGenerator;
import java.util.List;
import java.util.Map;
@Component
public class EnumFieldType extends FieldType {
    private final EnumGenerator generator;
    public EnumFieldType(EnumGenerator g) { this.generator = g; }
    @Override public String code() { return "ENUM"; }
    @Override public DataGenerator getGenerator() { return generator; }
    @Override @SuppressWarnings("unchecked")
    public Constraint createConstraint(Map<String, Object> config) {
        return new EnumConstraint((List<String>) config.get("values"));
    }
}
