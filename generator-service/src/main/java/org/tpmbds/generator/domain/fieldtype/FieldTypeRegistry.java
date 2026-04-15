package org.tpmbds.generator.domain.fieldtype;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
@Component
public class FieldTypeRegistry {
    private final Map<String, FieldType> types;
    public FieldTypeRegistry(List<FieldType> list) {
        types = list.stream().collect(Collectors.toMap(ft -> ft.code().toUpperCase(), Function.identity()));
    }
    public FieldType getByCode(String code) {
        FieldType ft = types.get(code == null ? null : code.toUpperCase());
        if (ft == null) throw new IllegalArgumentException("Unknown field type: " + code);
        return ft;
    }
}
