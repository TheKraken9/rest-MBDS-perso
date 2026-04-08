package org.tpmbds.restmbds.domain.exporter;

import java.util.List;
import java.util.Map;

/**
 * Classe abstraite pour l'export de datasets.
 *
 * Utilise le patron de conception Template Method : le squelette de l'algorithme
 * d'export (en-tête → corps → pied) est fixé ici, seule la mise en forme spécifique
 * au format est déléguée aux sous-classes.
 *
 * Cela garantit :
 *  - Cohérence du flux d'export quel que soit le format (OCP).
 *  - Possibilité d'ajouter un nouveau format en sous-classant uniquement (OCP).
 *  - Partage de logique commune (gestion des entités, itération des lignes) sans
 *    duplication (DRY).
 */
public abstract class Exporter {

    /** Identifiant du format (ex : "csv", "json", "xml"). Retourné en minuscules. */
    public abstract String format();

    /**
     * Point d'entrée public — implémente le Template Method.
     * Construit le document complet à partir des données générées.
     */
    public String export(Map<String, List<Map<String, Object>>> data) {
        StringBuilder sb = new StringBuilder();
        appendDocumentStart(sb, data);
        for (var entry : data.entrySet()) {
            appendEntityBlock(sb, entry.getKey(), entry.getValue());
        }
        appendDocumentEnd(sb);
        return sb.toString();
    }

    /** En-tête du document (optionnel, implémentation par défaut vide). */
    protected void appendDocumentStart(StringBuilder sb, Map<String, List<Map<String, Object>>> data) {}

    /**
     * Formate le bloc complet d'une entité (toutes ses lignes).
     * Délégué aux sous-classes pour la mise en forme propre au format.
     */
    protected abstract void appendEntityBlock(StringBuilder sb, String entityName, List<Map<String, Object>> rows);

    /** Pied du document (optionnel, implémentation par défaut vide). */
    protected void appendDocumentEnd(StringBuilder sb) {}
}
