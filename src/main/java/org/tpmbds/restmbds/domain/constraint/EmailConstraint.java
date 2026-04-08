package org.tpmbds.restmbds.domain.constraint;

public class EmailConstraint implements Constraint {

    private final String domain;

    public EmailConstraint(String domain) {
        this.domain = domain;
    }

    @Override
    public boolean isValid(Object value) {
        return value.toString().endsWith("@" + domain);
    }
}