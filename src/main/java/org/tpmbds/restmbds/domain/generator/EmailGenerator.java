package org.tpmbds.restmbds.domain.generator;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class EmailGenerator implements DataGenerator {

    @Override
    public Object generate(Map<String, Object> config, int rowIndex) {
        String domain = (String) config.getOrDefault("domain", "example.com");
        String localPart = "user" + UUID.randomUUID().toString().substring(0, 8);
        return localPart + "@" + domain;
    }
}
