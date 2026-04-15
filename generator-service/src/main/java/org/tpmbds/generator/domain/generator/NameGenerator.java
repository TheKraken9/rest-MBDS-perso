package org.tpmbds.generator.domain.generator;

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
 * En cas d'échec réseau, repasse sur des listes locales.
 * API : https://randomuser.me/api/?nat=fr&results=1
 */
@Component
public class NameGenerator implements DataGenerator {

    private static final String API_URL = "https://randomuser.me/api/?nat=%s&results=1";
    private static final Duration TIMEOUT = Duration.ofSeconds(3);

    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();

    private static final List<String> FR_FIRST = List.of(
            "Lucas","Emma","Louis","Jade","Gabriel","Chloé","Léo","Manon",
            "Raphaël","Inès","Nathan","Camille","Thomas","Léa","Hugo","Sarah");
    private static final List<String> FR_LAST  = List.of(
            "Martin","Bernard","Thomas","Petit","Robert","Durand",
            "Dubois","Moreau","Laurent","Simon","Michel","Leroy");
    private static final List<String> EN_FIRST = List.of(
            "James","Emma","Oliver","Ava","William","Sophia","Benjamin","Isabella",
            "Lucas","Mia","Henry","Charlotte","Alexander","Amelia","Mason","Harper");
    private static final List<String> EN_LAST  = List.of(
            "Smith","Johnson","Williams","Brown","Jones","Garcia",
            "Miller","Davis","Rodriguez","Martinez","Wilson","Anderson");

    private final Random random = new Random();

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

    private String[] fetchFromApi(String language) {
        try {
            String nat = "en".equalsIgnoreCase(language) ? "us" : "fr";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format(API_URL, nat)))
                    .timeout(TIMEOUT).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String first = capitalize(extractJsonValue(response.body(), "first"));
            String last  = capitalize(extractJsonValue(response.body(), "last"));
            return new String[]{ first, last };
        } catch (Exception e) {
            return localFallback(language);
        }
    }

    private String extractJsonValue(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search) + search.length();
        int end   = json.indexOf("\"", start);
        return json.substring(start, end);
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private String[] localFallback(String language) {
        List<String> fn = "en".equalsIgnoreCase(language) ? EN_FIRST : FR_FIRST;
        List<String> ln = "en".equalsIgnoreCase(language) ? EN_LAST  : FR_LAST;
        return new String[]{ fn.get(random.nextInt(fn.size())), ln.get(random.nextInt(ln.size())) };
    }
}
