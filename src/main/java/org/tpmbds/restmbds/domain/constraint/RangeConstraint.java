package org.tpmbds.restmbds.domain.constraint;

public class RangeConstraint extends Constraint {

    private final double min;
    private final double max;

    public RangeConstraint(double min, double max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean isValid(Object value) {
        double v = ((Number) value).doubleValue();
        return v >= min && v <= max;
    }
}