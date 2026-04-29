package org.tpmbds.generator.domain.exporter;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Exporte chaque entité dans un bloc CSV séparé par un en-tête de section.
 * Format : une section par entité, colonnes en première ligne, données ensuite.
 */
@Component
public class CsvExporter extends Exporter {

    @Override
    public String format() {
        return "csv";
    }

    @Override
    protected void appendEntityBlock(StringBuilder sb, String entityName, List<Map<String, Object>> rows) {
        if (rows.isEmpty()) return;

        sb.append("# ").append(entityName).append("\n");

        // En-tête de colonnes
        var columns = rows.getFirst().keySet();
        sb.append(String.join(",", columns)).append("\n");

        // Lignes de données
        for (var row : rows) {
            StringJoiner joiner = new StringJoiner(",");
            for (var col : columns) {
                Object val = row.get(col);
                String cell = val == null ? "" : val.toString();
                // Échappement CSV minimal : guillemets si virgule ou guillemet présent
                if (cell.contains(",") || cell.contains("\"") || cell.contains("\n")) {
                    cell = "\"" + cell.replace("\"", "\"\"") + "\"";
                }
                joiner.add(cell);
            }
            sb.append(joiner).append("\n");
        }

        sb.append("\n");
    }
}
