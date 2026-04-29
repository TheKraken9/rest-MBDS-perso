package org.tpmbds.manager.model.mapper;

import org.springframework.stereotype.Component;
import org.tpmbds.manager.model.dto.request.AttributeRequest;
import org.tpmbds.manager.model.dto.response.AttributeResponse;
import org.tpmbds.manager.model.entity.AttributeEntity;
import org.tpmbds.manager.model.entity.EntityModelEntity;

import java.util.*;

@Component
public class AttributeMapper {

    public AttributeEntity toEntity(AttributeRequest request, EntityModelEntity entityModel) {
        AttributeEntity attr = new AttributeEntity();
        attr.setName(request.getName());
        attr.setFieldTypeCode(request.getType().toUpperCase());
        attr.setConstraintJson(serializeConstraints(request.getConstraints()));
        attr.setEntityModel(entityModel);
        return attr;
    }

    public AttributeResponse toResponse(AttributeEntity entity) {
        return new AttributeResponse(
                entity.getId(),
                entity.getName(),
                entity.getFieldTypeCode(),
                deserializeConstraints(entity.getConstraintJson())
        );
    }

    public String serializeConstraints(Map<String, Object> map) {
        if (map == null || map.isEmpty()) return "{}";
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) sb.append(',');
            first = false;
            sb.append('"').append(escapeString(entry.getKey())).append('"').append(':');
            appendValue(sb, entry.getValue());
        }
        return sb.append('}').toString();
    }

    private void appendValue(StringBuilder sb, Object value) {
        switch (value) {
            case null         -> sb.append("null");
            case Boolean b    -> sb.append(b);
            case Number n     -> sb.append(n);
            case String s     -> sb.append('"').append(escapeString(s)).append('"');
            case List<?> list -> {
                sb.append('[');
                for (int i = 0; i < list.size(); i++) {
                    if (i > 0) sb.append(',');
                    appendValue(sb, list.get(i));
                }
                sb.append(']');
            }
            default           -> sb.append('"').append(escapeString(value.toString())).append('"');
        }
    }

    private String escapeString(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    public Map<String, Object> deserializeConstraints(String json) {
        if (json == null || json.isBlank()) return Collections.emptyMap();
        String s = json.trim();
        if ("{}".equals(s) || "null".equals(s)) return Collections.emptyMap();
        int[] pos = {0};
        return parseObject(s, pos);
    }

    private Map<String, Object> parseObject(String s, int[] pos) {
        Map<String, Object> result = new LinkedHashMap<>();
        skipWs(s, pos); expect(s, pos, '{'); skipWs(s, pos);
        while (pos[0] < s.length() && s.charAt(pos[0]) != '}') {
            String key = parseString(s, pos);
            skipWs(s, pos); expect(s, pos, ':'); skipWs(s, pos);
            Object value = parseValue(s, pos);
            result.put(key, value);
            skipWs(s, pos);
            if (pos[0] < s.length() && s.charAt(pos[0]) == ',') { pos[0]++; skipWs(s, pos); }
        }
        expect(s, pos, '}');
        return result;
    }

    private Object parseValue(String s, int[] pos) {
        skipWs(s, pos);
        if (pos[0] >= s.length()) return null;
        return switch (s.charAt(pos[0])) {
            case '"' -> parseString(s, pos);
            case '[' -> parseArray(s, pos);
            case '{' -> parseObject(s, pos);
            case 't' -> { pos[0] += 4; yield Boolean.TRUE; }
            case 'f' -> { pos[0] += 5; yield Boolean.FALSE; }
            case 'n' -> { pos[0] += 4; yield null; }
            default  -> parseNumber(s, pos);
        };
    }

    private String parseString(String s, int[] pos) {
        expect(s, pos, '"');
        StringBuilder sb = new StringBuilder();
        while (pos[0] < s.length() && s.charAt(pos[0]) != '"') {
            char c = s.charAt(pos[0]++);
            if (c == '\\' && pos[0] < s.length()) {
                char esc = s.charAt(pos[0]++);
                sb.append(switch (esc) { case '"' -> '"'; case '\\' -> '\\';
                    case 'n' -> '\n'; case 'r' -> '\r'; case 't' -> '\t'; default -> esc; });
            } else sb.append(c);
        }
        expect(s, pos, '"');
        return sb.toString();
    }

    private List<Object> parseArray(String s, int[] pos) {
        expect(s, pos, '['); skipWs(s, pos);
        List<Object> list = new ArrayList<>();
        while (pos[0] < s.length() && s.charAt(pos[0]) != ']') {
            list.add(parseValue(s, pos)); skipWs(s, pos);
            if (pos[0] < s.length() && s.charAt(pos[0]) == ',') { pos[0]++; skipWs(s, pos); }
        }
        expect(s, pos, ']');
        return list;
    }

    private Number parseNumber(String s, int[] pos) {
        int start = pos[0];
        while (pos[0] < s.length() && "0123456789.-+eE".indexOf(s.charAt(pos[0])) >= 0) pos[0]++;
        String num = s.substring(start, pos[0]);
        return (num.contains(".") || num.contains("e") || num.contains("E"))
                ? Double.parseDouble(num) : Long.parseLong(num);
    }

    private void skipWs(String s, int[] pos) {
        while (pos[0] < s.length() && Character.isWhitespace(s.charAt(pos[0]))) pos[0]++;
    }

    private void expect(String s, int[] pos, char expected) {
        if (pos[0] >= s.length() || s.charAt(pos[0]) != expected)
            throw new IllegalArgumentException("Expected '" + expected + "' at " + pos[0]);
        pos[0]++;
    }
}
