package org.tpmbds.generator.domain.constraint;

public class EmailConstraint extends Constraint {
    private final String domain;
    public EmailConstraint(String domain) { this.domain = domain; }
    @Override public boolean isValid(Object value) { return value.toString().endsWith("@" + domain); }
}
