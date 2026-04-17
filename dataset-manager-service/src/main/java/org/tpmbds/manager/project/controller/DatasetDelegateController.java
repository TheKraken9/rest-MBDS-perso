package org.tpmbds.manager.project.controller;

import feign.FeignException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tpmbds.manager.feign.GeneratorFeignClient;
import org.tpmbds.manager.feign.dto.PreviewResponse;

import java.util.Collections;

/**
 * Façade : délègue preview/export au generator-service.
 *
 * Règle de gestion des erreurs Feign :
 *   4xx → erreur métier du client → propager telle quelle
 *   5xx ou connexion → generator-service down → fallback PARTIAL (200)
 */
@RestController
@RequestMapping("/api/projects")
public class DatasetDelegateController {

    private final GeneratorFeignClient generatorClient;

    public DatasetDelegateController(GeneratorFeignClient generatorClient) {
        this.generatorClient = generatorClient;
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<PreviewResponse> preview(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(generatorClient.preview(id));
        } catch (FeignException e) {
            if (e.status() >= 400 && e.status() < 500) {
                return ResponseEntity.status(e.status()).build();
            }
            // 5xx ou connexion refusée → generator-service down → fallback PARTIAL
            return ResponseEntity.ok(new PreviewResponse(id, Collections.emptyMap(), "PARTIAL",
                    "generator-service momentanément indisponible. Réessayez plus tard."));
        }
    }

    @GetMapping("/{id}/export")
    public ResponseEntity<String> export(@PathVariable Long id,
                                          @RequestParam(defaultValue = "json") String format) {
        try {
            String content = generatorClient.export(id, format);
            MediaType mediaType = switch (format.toLowerCase()) {
                case "json" -> MediaType.APPLICATION_JSON;
                case "xml"  -> MediaType.APPLICATION_XML;
                default     -> MediaType.TEXT_PLAIN;
            };
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=dataset." + format)
                    .contentType(mediaType)
                    .body(content);
        } catch (FeignException e) {
            if (e.status() >= 400 && e.status() < 500) {
                // 400 = format invalide, 404 = projet introuvable → propager
                return ResponseEntity.status(e.status()).build();
            }
            // 5xx ou connexion → fallback PARTIAL
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"status\":\"PARTIAL\",\"projectId\":" + id
                          + ",\"message\":\"generator-service momentanément indisponible.\"}");
        }
    }
}

