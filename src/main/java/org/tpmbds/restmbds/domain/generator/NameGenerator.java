package org.tpmbds.restmbds.domain.generator;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Génère des noms réalistes (prénoms/noms français par défaut).
 *
 * Config supportée :
 *  - "language" : "fr" (défaut) ou "en"
 *  - "style"    : "firstname" | "lastname" | "firstname_lastname" (défaut)
 */
@Component
public class NameGenerator implements DataGenerator {

    private static final List<String> FR_FIRST_NAMES = List.of(
            "Lucas", "Emma", "Louis", "Jade", "Gabriel", "Chloé", "Léo", "Manon",
            "Raphaël", "Inès", "Nathan", "Camille", "Thomas", "Léa", "Hugo", "Sarah",
            "Mathis", "Zoé", "Antoine", "Clara", "Nicolas", "Alice", "Julien", "Lucie"
    );

    private static final List<String> FR_LAST_NAMES = List.of(
            "Martin", "Bernard", "Thomas", "Petit", "Robert", "Richard", "Durand",
            "Dubois", "Moreau", "Laurent", "Simon", "Michel", "Lefebvre", "Leroy",
            "Roux", "David", "Bertrand", "Morel", "Fournier", "Girard", "Bonnet"
    );

    private static final List<String> EN_FIRST_NAMES = List.of(
            "James", "Emma", "Oliver", "Ava", "William", "Sophia", "Benjamin", "Isabella",
            "Lucas", "Mia", "Henry", "Charlotte", "Alexander", "Amelia", "Mason", "Harper"
    );

    private static final List<String> EN_LAST_NAMES = List.of(
            "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller",
            "Davis", "Rodriguez", "Martinez", "Hernandez", "Lopez", "Wilson", "Anderson"
    );

    private final Random random = new Random();

    @Override
    public Object generate(Map<String, Object> config, int rowIndex) {
        String language = (String) config.getOrDefault("language", "fr");
        String style    = (String) config.getOrDefault("style", "firstname_lastname");

        List<String> firstNames = "en".equalsIgnoreCase(language) ? EN_FIRST_NAMES : FR_FIRST_NAMES;
        List<String> lastNames  = "en".equalsIgnoreCase(language) ? EN_LAST_NAMES  : FR_LAST_NAMES;

        String first = firstNames.get(random.nextInt(firstNames.size()));
        String last  = lastNames.get(random.nextInt(lastNames.size()));

        return switch (style) {
            case "firstname"           -> first;
            case "lastname"            -> last;
            case "lastname_firstname"  -> last + " " + first;
            default                    -> first + " " + last; // firstname_lastname
        };
    }
}
