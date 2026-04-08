package org.tpmbds.restmbds.domain.fieldtype;

import org.springframework.stereotype.Component;
import org.tpmbds.restmbds.domain.constraint.Constraint;
import org.tpmbds.restmbds.domain.constraint.EnumConstraint;
import org.tpmbds.restmbds.domain.generator.DataGenerator;
import org.tpmbds.restmbds.domain.generator.EnumGenerator;

import java.util.List;
import java.util.Map;

@Component
public class EnumFieldType extends FieldType {

    private final EnumGenerator generator;

    public EnumFieldType(EnumGenerator generator) {
        this.generator = generator;
    }

    @Override
    public String code() {
        return "ENUM";
    }

    @Override
    public DataGenerator getGenerator() {
        return generator;
    }

    @Override
    public Constraint createConstraint(Map<String, Object> config) {
        List<String> values = (List<String>) config.get("values");
        return new EnumConstraint(values);
    }
}