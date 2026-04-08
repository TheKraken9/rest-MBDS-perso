package org.tpmbds.restmbds.domain.constraint;

/** Contrainte neutre — accepte toujours la valeur. */
public class NoConstraint extends Constraint {

    @Override
    public boolean isValid(Object value) {
        return true;
    }

    @Override
    public String describe() {
        return "no constraint";
    }
}
