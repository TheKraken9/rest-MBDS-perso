package org.tpmbds.restmbds.domain.constraint;

public class DistributionConstraint extends Constraint {

    private final String type;

    public DistributionConstraint(String type) {
        this.type = type;
    }

    @Override
    public boolean isValid(Object value) {
        return true;
    }
}