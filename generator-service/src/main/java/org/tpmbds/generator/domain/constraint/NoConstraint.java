package org.tpmbds.generator.domain.constraint;

public class NoConstraint extends Constraint {
    @Override public boolean isValid(Object value) { return true; }
    @Override public String describe() { return "no constraint"; }
}
