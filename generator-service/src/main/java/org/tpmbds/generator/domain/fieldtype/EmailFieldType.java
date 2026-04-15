package org.tpmbds.generator.domain.fieldtype;
import org.springframework.stereotype.Component;
import org.tpmbds.generator.domain.constraint.Constraint;
import org.tpmbds.generator.domain.constraint.EmailConstraint;
import org.tpmbds.generator.domain.generator.DataGenerator;
import org.tpmbds.generator.domain.generator.EmailGenerator;
import java.util.Map;
@Component
public class EmailFieldType extends FieldType {
    private final EmailGenerator generator;
    public EmailFieldType(EmailGenerator g) { this.generator = g; }
    @Override public String code() { return "EMAIL"; }
    @Override public DataGenerator getGenerator() { return generator; }
    @Override public Constraint createConstraint(Map<String, Object> config) {
        return new EmailConstraint((String) config.getOrDefault("domain", "example.com"));
    }
}
