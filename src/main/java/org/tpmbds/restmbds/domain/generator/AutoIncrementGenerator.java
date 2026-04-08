package org.tpmbds.restmbds.domain.generator;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Génère une valeur auto-incrémentée basée sur l'index de ligne.
 * Utilise les paramètres "start" (défaut 1) et "step" (défaut 1).
 */
@Component
public class AutoIncrementGenerator implements DataGenerator {

    @Override
    public Object generate(Map<String, Object> config, int rowIndex) {
        int start = ((Number) config.getOrDefault("start", 1)).intValue();
        int step  = ((Number) config.getOrDefault("step", 1)).intValue();
        return start + (long) rowIndex * step;
    }
}
