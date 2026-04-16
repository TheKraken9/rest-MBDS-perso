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
        var preview = generatorService.preview(projectId);
        if ("PARTIAL".equals(preview.getStatus())) {
            return buildFallbackExport(format, projectId, preview.getMessage());
        }
        return exporter.export(preview.getData());
    }

    /**
     * Retourne un message de fallback dans le format demandé quand dataset-manager-service
     * est indisponible (circuit breaker ouvert).
     */
    private String buildFallbackExport(String format, Long projectId, String message) {
        return switch (format) {
            case "csv" -> "status,projectId,message\nPARTIAL," + projectId + ",\"" + message + "\"\n";
            default    -> "{\n  \"status\": \"PARTIAL\",\n  \"projectId\": " + projectId
                          + ",\n  \"message\": \"" + message + "\"\n}";
        };
    }
}
