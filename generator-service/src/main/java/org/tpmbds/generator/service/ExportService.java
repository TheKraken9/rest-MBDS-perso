package org.tpmbds.generator.service;

import org.springframework.stereotype.Service;
import org.tpmbds.generator.domain.exporter.ExporterRegistry;
import org.tpmbds.generator.exception.BadRequestException;

@Service
public class ExportService {

    private final GeneratorService generatorService;
    private final ExporterRegistry registry;

    public ExportService(GeneratorService generatorService, ExporterRegistry registry) {
        this.generatorService = generatorService;
        this.registry = registry;
    }

    public String export(Long projectId, String format) {
        var exporter = registry.get(format);
        if (exporter == null) {
            throw new BadRequestException("Unsupported export format: '" + format + "'");
        }
        var data = generatorService.preview(projectId).getData();
        return exporter.export(data);
    }
}
