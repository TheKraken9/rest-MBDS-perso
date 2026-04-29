package org.tpmbds.generator.domain.constraint;

public abstract class Constraint {
    public abstract boolean isValid(Object value);
    public String describe() { return this.getClass().getSimpleName(); }
}
