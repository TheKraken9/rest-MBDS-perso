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
        } catch (FeignException.FeignClientException e) {
            // 4xx → erreur métier (ex: 404 projet inconnu) → propager
            return ResponseEntity.status(e.status()).build();
        } catch (FeignException e) {
            // 5xx → panne generator → fallback PARTIAL
            return ResponseEntity.ok(new PreviewResponse(id, Collections.emptyMap(), "PARTIAL",
                    "generator-service momentanément indisponible. Réessayez plus tard."));
        } catch (Exception e) {
            // Pas d'instance dans Eureka (load balancer) → fallback PARTIAL
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
        } catch (FeignException.FeignClientException e) {
            // 400 = format invalide, 404 = projet inconnu → propager
            return ResponseEntity.status(e.status()).build();
        } catch (FeignException e) {
            // 5xx → panne generator → fallback PARTIAL
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"status\":\"PARTIAL\",\"projectId\":" + id
                          + ",\"message\":\"generator-service momentanément indisponible.\"}");
        } catch (Exception e) {
            // Pas d'instance dans Eureka (load balancer) → fallback PARTIAL
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"status\":\"PARTIAL\",\"projectId\":" + id
                          + ",\"message\":\"generator-service momentanément indisponible.\"}");
        }
    }
}

