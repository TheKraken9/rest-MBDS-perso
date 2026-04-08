package org.tpmbds.restmbds.domain.fieldtype;

import org.springframework.stereotype.Component;
import org.tpmbds.restmbds.domain.constraint.Constraint;
import org.tpmbds.restmbds.domain.constraint.EmailConstraint;
import org.tpmbds.restmbds.domain.generator.DataGenerator;
import org.tpmbds.restmbds.domain.generator.EmailGenerator;

import java.util.Map;

@Component
public class EmailFieldType implements FieldType {

    private final EmailGenerator generator;

    public EmailFieldType(EmailGenerator generator) {
        this.generator = generator;
    }

    @Override
    public String code() {
        return "EMAIL";
    }

    @Override
    public DataGenerator getGenerator() {
        return generator;
    }

    @Override
    public Constraint createConstraint(Map<String, Object> config) {
        String domain = (String) config.getOrDefault("domain", "example.com");
        return new EmailConstraint(domain);
    }
}