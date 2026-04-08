package org.tpmbds.restmbds.domain.generator;

import java.util.Map;

/**
 * Stratégie de génération d'une valeur pour un champ donné.
 *
 * Interface (et non classe abstraite) car il s'agit d'un contrat purement fonctionnel :
 * générer une valeur à partir d'une configuration et d'un index de ligne.
 * Aucun état partagé n'est nécessaire entre les implémentations.
 *
 * @param config   contraintes désérialisées du champ (min, max, values, domain…)
 * @param rowIndex index de la ligne en cours de génération (utile pour AutoIncrement)
 */
public interface DataGenerator {
    Object generate(Map<String, Object> config, int rowIndex);
}
