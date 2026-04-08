package org.tpmbds.restmbds.domain.constraint;

public interface Constraint {
    boolean isValid(Object value);
}