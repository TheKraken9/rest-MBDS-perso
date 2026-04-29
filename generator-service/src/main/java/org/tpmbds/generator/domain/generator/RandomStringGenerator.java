package org.tpmbds.generator.domain.generator;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.UUID;
@Component
public class RandomStringGenerator implements DataGenerator {
    @Override public Object generate(Map<String, Object> config, int rowIndex) {
        int length = ((Number) config.getOrDefault("length", 8)).intValue();
        String raw = UUID.randomUUID().toString().replace("-", "");
        return raw.substring(0, Math.min(length, raw.length()));
    }
}
