package org.tpmbds.restmbds.domain.exporter;

import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class ExporterRegistry {

    private final Map<String, Exporter> exporters;

    public ExporterRegistry(List<Exporter> list) {
        exporters = new HashMap<>();
        for (var e : list) {
            exporters.put(e.format(), e);
        }
    }

    public Exporter get(String format) {
        return exporters.get(format.toLowerCase());
    }
}