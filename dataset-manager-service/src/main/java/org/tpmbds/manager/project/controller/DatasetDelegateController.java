package org.tpmbds.manager.project.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tpmbds.manager.feign.GeneratorFeignClient;
import org.tpmbds.manager.feign.dto.PreviewResponse;

/**
 * Façade de rétrocompatibilité : les anciens endpoints /api/projects/{id}/preview
 * et /api/projects/{id}/export sont conservés mais délèguent au generator-service
 * via Feign.
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
        return ResponseEntity.ok(generatorClient.preview(id));
    }

    @GetMapping("/{id}/export")
    public ResponseEntity<String> export(@PathVariable Long id,
                                          @RequestParam(defaultValue = "json") String format) {
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
    }
}
