package org.tpmbds.generator.domain.generator;
import org.springframework.stereotype.Component;
import java.util.Map;
@Component
public class AutoIncrementGenerator implements DataGenerator {
    @Override public Object generate(Map<String, Object> config, int rowIndex) {
        int start = ((Number) config.getOrDefault("start", 1)).intValue();
        int step  = ((Number) config.getOrDefault("step",  1)).intValue();
        return start + (long) rowIndex * step;
    }
}
