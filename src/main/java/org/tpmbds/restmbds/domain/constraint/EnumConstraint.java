package org.tpmbds.restmbds.domain.constraint;

import java.util.List;

public class EnumConstraint extends Constraint {

    private final List<String> values;

    public EnumConstraint(List<String> values) {
        this.values = values;
    }

    @Override
    public boolean isValid(Object value) {
        return values.contains(value);
    }
}