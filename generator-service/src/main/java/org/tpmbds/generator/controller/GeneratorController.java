package org.tpmbds.generator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tpmbds.generator.dto.PreviewResponse;
import org.tpmbds.generator.service.ExportService;
import org.tpmbds.generator.service.GeneratorService;

@RestController
@RequestMapping("/api/generator")
public class GeneratorController {

    private final GeneratorService generatorService;
    private final ExportService exportService;

    public GeneratorController(GeneratorService generatorService, ExportService exportService) {
        this.generatorService = generatorService;
        this.exportService = exportService;
    }

    /**
     * Génère un aperçu JSON des données pour le projet donné.
     * Appelé par dataset-manager-service via Feign ou directement par l'API Gateway.
     */
    @GetMapping("/{projectId}/preview")
    public ResponseEntity<PreviewResponse> preview(@PathVariable Long projectId) {
        return ResponseEntity.ok(generatorService.preview(projectId));
    }

    /**
     * Exporte les données générées dans le format demandé (csv, json, xml).
     */
    @GetMapping("/{projectId}/export")
    public ResponseEntity<String> export(@PathVariable Long projectId,
                                         @RequestParam(defaultValue = "json") String format) {
        return ResponseEntity.ok(exportService.export(projectId, format));
    }
}
