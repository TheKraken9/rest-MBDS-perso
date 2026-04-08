package org.tpmbds.restmbds.domain.fieldtype;

import org.springframework.stereotype.Component;
import org.tpmbds.restmbds.common.exception.BadRequestException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class FieldTypeRegistry {

    private final Map<String, FieldType> types;

    public FieldTypeRegistry(List<FieldType> list) {
        types = list.stream()
                .collect(Collectors.toMap(ft -> ft.code().toUpperCase(), Function.identity()));
    }

    public FieldType getByCode(String code) {
        if (code == null) throw new BadRequestException("Field type code must not be null");
        FieldType ft = types.get(code.toUpperCase());
        if (ft == null) {
            throw new BadRequestException(
                    "Unknown field type: '" + code + "'. Supported types: " + types.keySet()
            );
        }
        return ft;
    }
}
