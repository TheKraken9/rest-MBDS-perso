package org.tpmbds.generator.domain.generator;
import java.util.Map;
public interface DataGenerator {
    Object generate(Map<String, Object> config, int rowIndex);
}
