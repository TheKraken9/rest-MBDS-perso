package org.tpmbds.restmbds.domain.generator;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Random;

// ─── OPTION API DISTANTE (commentée) ────────────────────────────────────────
// Source : https://randomuser.me  — gratuit, sans clé API, ~100 req/s
//
// Pour l'activer :
//  1. Décommenter les imports ci-dessous
//  2. Décommenter la méthode fetchFromApi() en bas de la classe
//  3. Dans generate(), remplacer les 4 lignes "MODE LOCAL" par les 3 lignes "MODE API"
//
// import java.net.URI;
// import java.net.http.HttpClient;
// import java.net.http.HttpRequest;
// import java.net.http.HttpResponse;
// ────────────────────────────────────────────────────────────────────────────

/**
 * Génère des noms réalistes (prénoms/noms français par défaut).
 *
 * Deux modes disponibles :
 *  1. LOCAL (actif par défaut) — pioche dans des listes codées en dur, aucune dépendance réseau.
 *  2. API DISTANTE (commenté)  — appelle randomuser.me pour obtenir de vrais noms par nationalité.
 *
 * Config supportée :
 *  - "language" : "fr" (défaut) ou "en"
 *  - "style"    : "firstname" | "lastname" | "firstname_lastname" (défaut)
 */
@Component
public class NameGenerator implements DataGenerator {

    // ── Données locales ──────────────────────────────────────────────────────

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

    // ── Génération (mode local actif) ────────────────────────────────────────

    @Override
    public Object generate(Map<String, Object> config, int rowIndex) {
        String language = (String) config.getOrDefault("language", "fr");
        String style    = (String) config.getOrDefault("style", "firstname_lastname");

        // MODE LOCAL ─────────────────────────────────────────────────────────
        List<String> firstNames = "en".equalsIgnoreCase(language) ? EN_FIRST_NAMES : FR_FIRST_NAMES;
        List<String> lastNames  = "en".equalsIgnoreCase(language) ? EN_LAST_NAMES  : FR_LAST_NAMES;

        String first = firstNames.get(random.nextInt(firstNames.size()));
        String last  = lastNames.get(random.nextInt(lastNames.size()));

        // MODE API (à la place des 4 lignes ci-dessus, décommenter) ──────────
        // String[] name = fetchFromApi(language, style);
        // String first  = name[0];
        // String last   = name[1];
        // ────────────────────────────────────────────────────────────────────

        return switch (style) {
            case "firstname"          -> first;
            case "lastname"           -> last;
            case "lastname_firstname" -> last + " " + first;
            default                   -> first + " " + last; // firstname_lastname
        };
    }

    // ── Méthode API distante (commentée) ────────────────────────────────────
    //
    // Appelle : GET https://randomuser.me/api/?nat=fr&results=1
    //
    // Exemple de réponse JSON :
    // {
    //   "results": [{
    //     "name": {
    //       "first": "Camille",
    //       "last":  "Dupont"
    //     }
    //   }]
    // }
    //
    // private String[] fetchFromApi(String language, String style) {
    //     try {
    //         // "fr" → nat=fr, "en" → nat=us
    //         String nat = "en".equalsIgnoreCase(language) ? "us" : "fr";
    //         String url = "https://randomuser.me/api/?nat=" + nat + "&results=1";
    //
    //         HttpClient client = HttpClient.newHttpClient();
    //         HttpRequest request = HttpRequest.newBuilder()
    //                 .uri(URI.create(url))
    //                 .GET()
    //                 .build();
    //
    //         HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    //         String body = response.body();
    //
    //         // Extraction manuelle du prénom et du nom (pas d'ObjectMapper)
    //         // Le JSON a la forme : ..."first":"Camille","last":"Dupont"...
    //         String first = extractJsonValue(body, "first");
    //         String last  = extractJsonValue(body, "last");
    //
    //         return new String[]{ first, last };
    //
    //     } catch (Exception e) {
    //         // En cas d'échec réseau, on repasse sur les listes locales
    //         List<String> fallbackFirst = "en".equalsIgnoreCase(language) ? EN_FIRST_NAMES : FR_FIRST_NAMES;
    //         List<String> fallbackLast  = "en".equalsIgnoreCase(language) ? EN_LAST_NAMES  : FR_LAST_NAMES;
    //         return new String[]{
    //             fallbackFirst.get(random.nextInt(fallbackFirst.size())),
    //             fallbackLast.get(random.nextInt(fallbackLast.size()))
    //         };
    //     }
    // }
    //
    // /** Extrait la valeur d'une clé dans un JSON brut, ex: "first":"Camille" → "Camille" */
    // private String extractJsonValue(String json, String key) {
    //     String search = "\"" + key + "\":\"";
    //     int start = json.indexOf(search) + search.length();
    //     int end   = json.indexOf("\"", start);
    //     return json.substring(start, end);
    // }
    // ────────────────────────────────────────────────────────────────────────
}
