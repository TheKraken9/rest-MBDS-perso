package org.tpmbds.restmbds.domain.fieldtype;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.List;

@Component
public class FieldTypeRegistry {

    private final Map<String, FieldType> types;

    public FieldTypeRegistry(List<FieldType> list) {
        types = list.stream()
                .collect(Collectors.toMap(FieldType::code, Function.identity()));
    }

    public FieldType getByCode(String code) {
        return types.get(code.toUpperCase());
    }
}