package org.tpmbds.generator.domain.generator;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.Random;
@Component
public class EnumGenerator implements DataGenerator {
    private final Random random = new Random();
    @Override @SuppressWarnings("unchecked")
    public Object generate(Map<String, Object> config, int rowIndex) {
        List<String> values = (List<String>) config.get("values");
        if (values == null || values.isEmpty()) return "UNKNOWN";
        return values.get(random.nextInt(values.size()));
    }
}
