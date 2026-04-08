package org.tpmbds.restmbds.domain.fieldtype;

import org.springframework.stereotype.Component;
import org.tpmbds.restmbds.domain.constraint.Constraint;
import org.tpmbds.restmbds.domain.constraint.NoConstraint;
import org.tpmbds.restmbds.domain.generator.DataGenerator;
import org.tpmbds.restmbds.domain.generator.DateGenerator;

import java.util.Map;

@Component
public class DateFieldType extends FieldType {

    private final DateGenerator generator;

    public DateFieldType(DateGenerator generator) {
        this.generator = generator;
    }

    @Override
    public String code() {
        return "DATE";
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
