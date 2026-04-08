package org.tpmbds.restmbds.domain.fieldtype;

import org.springframework.stereotype.Component;
import org.tpmbds.restmbds.domain.constraint.Constraint;
import org.tpmbds.restmbds.domain.constraint.NoConstraint;
import org.tpmbds.restmbds.domain.generator.AutoIncrementGenerator;
import org.tpmbds.restmbds.domain.generator.DataGenerator;

import java.util.Map;

@Component
public class AutoIncrementFieldType extends FieldType {

    private final AutoIncrementGenerator generator;

    public AutoIncrementFieldType(AutoIncrementGenerator generator) {
        this.generator = generator;
    }

    @Override
    public String code() {
        return "AUTOINCREMENT";
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
