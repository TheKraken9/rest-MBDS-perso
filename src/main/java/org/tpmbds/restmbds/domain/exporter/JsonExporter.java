package org.tpmbds.restmbds.domain.exporter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Exporte les données au format JSON :
 * { "EntityName": [ { "field": value, ... }, ... ], ... }
 */
@Component
public class JsonExporter extends Exporter {

    private final ObjectMapper objectMapper;

    public JsonExporter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String format() {
        return "json";
    }

    @Override
    protected void appendDocumentStart(StringBuilder sb, Map<String, List<Map<String, Object>>> data) {
        try {
            sb.append(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON serialization failed", e);
        }
    }

    /**
     * Le document complet est sérialisé d'un coup dans appendDocumentStart,
     * donc appendEntityBlock n'est pas utilisé pour JSON.
     */
    @Override
    protected void appendEntityBlock(StringBuilder sb, String entityName, List<Map<String, Object>> rows) {
        // handled in appendDocumentStart
    }

    /**
     * Surcharge export() pour éviter une double itération : Jackson sérialise
     * la Map entière en une seule passe, ce qui est plus efficace.
     */
    @Override
    public final String export(Map<String, List<Map<String, Object>>> data) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON serialization failed", e);
        }
    }
}
