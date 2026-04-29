package org.tpmbds.generator.domain.exporter;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Exporte les données au format XML.
 * Structure : &lt;dataset&gt; &gt; &lt;EntityName&gt; &gt; &lt;row&gt; &gt; &lt;fieldName&gt;value&lt;/fieldName&gt;
 */
@Component
public class XmlExporter extends Exporter {

    @Override
    public String format() {
        return "xml";
    }

    @Override
    protected void appendDocumentStart(StringBuilder sb, Map<String, List<Map<String, Object>>> data) {
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<dataset>\n");
    }

    @Override
    protected void appendEntityBlock(StringBuilder sb, String entityName, List<Map<String, Object>> rows) {
        String tag = sanitizeTag(entityName);
        sb.append("  <").append(tag).append("s>\n");
        for (var row : rows) {
            sb.append("    <").append(tag).append(">\n");
            for (var entry : row.entrySet()) {
                String field = sanitizeTag(entry.getKey());
                String value = entry.getValue() == null ? "" : escapeXml(entry.getValue().toString());
                sb.append("      <").append(field).append(">")
                  .append(value)
                  .append("</").append(field).append(">\n");
            }
            sb.append("    </").append(tag).append(">\n");
        }
        sb.append("  </").append(tag).append("s>\n");
    }

    @Override
    protected void appendDocumentEnd(StringBuilder sb) {
        sb.append("</dataset>");
    }

    private String sanitizeTag(String name) {
        return name.replaceAll("[^a-zA-Z0-9_\\-.]", "_");
    }

    private String escapeXml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
