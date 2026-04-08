package org.tpmbds.restmbds.domain.fieldtype;

import org.tpmbds.restmbds.domain.constraint.Constraint;
import org.tpmbds.restmbds.domain.generator.DataGenerator;

import java.util.Map;

/**
 * Classe abstraite définissant le contrat commun pour tous les types de champs.
 * Chaque implémentation concrète gère un type de donnée précis (Integer, Email, Enum…).
 *
 * Choix : classe abstraite plutôt qu'interface pour :
 *  - Rendre la hiérarchie explicite et auto-documentée.
 *  - Permettre l'ajout futur de comportement partagé (validation du config, logging)
 *    sans casser les implémentations existantes (OCP).
 *  - Ouvrir la porte au Template Method si le processus de création de contrainte
 *    doit être standardisé.
 */
public abstract class FieldType {

    /** Code identifiant le type (ex : "INTEGER", "EMAIL"). Insensible à la casse dans le registre. */
    public abstract String code();

    /** Retourne le générateur associé à ce type. */
    public abstract DataGenerator getGenerator();

    /**
     * Crée la contrainte correspondant à ce type à partir de la configuration JSON
     * désérialisée en Map. Chaque sous-classe interprète les clés qui la concernent.
     */
    public abstract Constraint createConstraint(Map<String, Object> config);
}
