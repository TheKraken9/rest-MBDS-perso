package org.tpmbds.restmbds.domain.fieldtype;

import org.springframework.stereotype.Component;
import org.tpmbds.restmbds.domain.constraint.Constraint;
import org.tpmbds.restmbds.domain.constraint.NoConstraint;
import org.tpmbds.restmbds.domain.generator.DataGenerator;
import org.tpmbds.restmbds.domain.generator.NameGenerator;

import java.util.Map;

@Component
public class NameFieldType extends FieldType {

    private final NameGenerator generator;

    public NameFieldType(NameGenerator generator) {
        this.generator = generator;
    }

    @Override
    public String code() {
        return "NAME";
    }

    @Override
    public DataGenerator getGenerator() {
        return generator;
    }

    @Override
    public Constraint createConstraint(Map<String, Object> config) {
        return new NoConstraint();
    }
}
