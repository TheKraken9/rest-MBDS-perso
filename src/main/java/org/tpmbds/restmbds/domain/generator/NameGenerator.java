package org.tpmbds.restmbds.domain.generator;

import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Génère des noms réalistes via l'API randomuser.me.
 * En cas d'échec réseau (timeout, API down), repasse automatiquement
 * sur les listes locales codées en dur.
 *
 * API utilisée : https://randomuser.me/api/?nat=fr&results=1
 *  - Gratuite, sans clé API
 *  - Supporte : nat=fr (français), nat=us (anglais)
 *
 * Config supportée :
 *  - "language" : "fr" (défaut) ou "en"
 *  - "style"    : "firstname" | "lastname" | "lastname_firstname" | "firstname_lastname" (défaut)
 */
@Component
public class NameGenerator implements DataGenerator {

    private static final String API_URL = "https://randomuser.me/api/?nat=%s&results=1";
    private static final Duration TIMEOUT = Duration.ofSeconds(3);

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(TIMEOUT)
            .build();

    // ── Fallback local (si l'API ne répond pas) ──────────────────────────────

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

    // ── Génération ───────────────────────────────────────────────────────────

    @Override
    public Object generate(Map<String, Object> config, int rowIndex) {
        String language = (String) config.getOrDefault("language", "fr");
        String style    = (String) config.getOrDefault("style", "firstname_lastname");

        String[] name = fetchFromApi(language);
        String first  = name[0];
        String last   = name[1];

        return switch (style) {
            case "firstname"          -> first;
            case "lastname"           -> last;
            case "lastname_firstname" -> last + " " + first;
            default                   -> first + " " + last;
        };
    }

    // ── Appel API randomuser.me ───────────────────────────────────────────────

    private String[] fetchFromApi(String language) {
        try {
            String nat = "en".equalsIgnoreCase(language) ? "us" : "fr";
            String url = String.format(API_URL, nat);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(TIMEOUT)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();

            // Extraction manuelle — pas d'ObjectMapper
            // Réponse : {"results":[{"name":{"title":"M","first":"Camille","last":"Dupont"}}],...}
            String first = extractJsonValue(body, "first");
            String last  = extractJsonValue(body, "last");

            return new String[]{ capitalize(first), capitalize(last) };

        } catch (Exception e) {
            // Fallback silencieux sur les listes locales
            return localFallback(language);
        }
    }

    /** Extrait la valeur d'une clé dans un JSON brut : "first":"camille" → "camille" */
    private String extractJsonValue(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search) + search.length();
        int end   = json.indexOf("\"", start);
        return json.substring(start, end);
    }

    /** Met la première lettre en majuscule (l'API renvoie parfois en minuscule) */
    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    /** Fallback sur les listes locales si l'API est inaccessible */
    private String[] localFallback(String language) {
        List<String> firstNames = "en".equalsIgnoreCase(language) ? EN_FIRST_NAMES : FR_FIRST_NAMES;
        List<String> lastNames  = "en".equalsIgnoreCase(language) ? EN_LAST_NAMES  : FR_LAST_NAMES;
        return new String[]{
            firstNames.get(random.nextInt(firstNames.size())),
            lastNames.get(random.nextInt(lastNames.size()))
        };
    }
}
