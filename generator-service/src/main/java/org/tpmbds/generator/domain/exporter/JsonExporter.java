package org.tpmbds.generator.domain.exporter;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Exporte les données au format JSON sans dépendance externe (pas d'ObjectMapper).
 * Sérialisation manuelle de Map&lt;String, List&lt;Map&lt;String, Object&gt;&gt;&gt;.
 */
@Component
public class JsonExporter extends Exporter {

    @Override
    public String format() {
        return "json";
    }

    /**
     * Surcharge pour sérialiser la Map entière en une passe JSON complète,
     * avec indentation pour la lisibilité.
     */
    @Override
    public final String export(Map<String, List<Map<String, Object>>> data) {
        StringBuilder sb = new StringBuilder();
        appendObject(sb, data, 0);
        return sb.toString();
    }

    @Override
    protected void appendEntityBlock(StringBuilder sb, String entityName, List<Map<String, Object>> rows) {
        // non utilisé — géré dans export()
    }

    // ─── Sérialiseur JSON manuel ──────────────────────────────────────────────

    private void appendObject(StringBuilder sb, Map<?, ?> map, int indent) {
        sb.append("{\n");
        int i = 0;
        for (var entry : map.entrySet()) {
            appendIndent(sb, indent + 2);
            sb.append('"').append(escapeString(String.valueOf(entry.getKey()))).append("\": ");
            appendValue(sb, entry.getValue(), indent + 2);
            if (++i < map.size()) sb.append(',');
            sb.append('\n');
        }
        appendIndent(sb, indent);
        sb.append('}');
    }

    private void appendArray(StringBuilder sb, List<?> list, int indent) {
        if (list.isEmpty()) { sb.append("[]"); return; }
        sb.append("[\n");
        for (int i = 0; i < list.size(); i++) {
            appendIndent(sb, indent + 2);
            appendValue(sb, list.get(i), indent + 2);
            if (i < list.size() - 1) sb.append(',');
            sb.append('\n');
        }
        appendIndent(sb, indent);
        sb.append(']');
    }

    private void appendValue(StringBuilder sb, Object value, int indent) {
        switch (value) {
            case null         -> sb.append("null");
            case Boolean b    -> sb.append(b);
            case Number n     -> sb.append(n);
            case String s     -> sb.append('"').append(escapeString(s)).append('"');
            case Map<?,?> m   -> appendObject(sb, m, indent);
            case List<?> l    -> appendArray(sb, l, indent);
            default           -> sb.append('"').append(escapeString(value.toString())).append('"');
        }
    }

    private void appendIndent(StringBuilder sb, int spaces) {
        sb.append(" ".repeat(spaces));
    }

    private String escapeString(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
