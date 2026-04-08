package org.tpmbds.restmbds.domain.constraint;

/**
 * Contrat de base pour toutes les contraintes de validation.
 * Classe abstraite (et non interface) afin d'empêcher les implémentations
 * anonymes par lambda et de forcer des sous-classes nommées, testables et
 * documentables. Permet également d'ajouter du comportement partagé
 * (composition, description) sans casser les implémentations existantes.
 */
public abstract class Constraint {

    /** Vérifie si la valeur respecte la contrainte. */
    public abstract boolean isValid(Object value);

    /** Description lisible de la contrainte (utile pour les messages d'erreur). */
    public String describe() {
        return this.getClass().getSimpleName();
    }
}
