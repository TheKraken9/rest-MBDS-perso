package org.tpmbds.restmbds.domain.fieldtype;

import org.springframework.stereotype.Component;
import org.tpmbds.restmbds.domain.constraint.Constraint;
import org.tpmbds.restmbds.domain.generator.DataGenerator;
import org.tpmbds.restmbds.domain.generator.RandomStringGenerator;

import java.util.Map;

@Component
public class StringFieldType implements FieldType {

    private final RandomStringGenerator generator;

    public StringFieldType(RandomStringGenerator generator) {
        this.generator = generator;
    }

    @Override
    public String code() {
        return "STRING";
    }

    @Override
    public DataGenerator getGenerator() {
        return generator;
    }

    @Override
    public Constraint createConstraint(Map<String, Object> config) {
        return value -> true; // aucune contrainte
    }
}