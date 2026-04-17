package org.tpmbds.gateway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Endpoints de fallback appelés par les filtres CircuitBreaker du Gateway
 * quand un service amont est indisponible (CB ouvert ou timeout).
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/dataset-manager")
    public ResponseEntity<Map<String, Object>> datasetManagerFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "status", "PARTIAL",
                "message", "dataset-manager-service momentanément indisponible. Réessayez plus tard.",
                "timestamp", Instant.now().toString()
        ));
    }

    @GetMapping("/generator")
    public ResponseEntity<Map<String, Object>> generatorFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "status", "PARTIAL",
                "message", "generator-service momentanément indisponible. Réessayez plus tard.",
                "timestamp", Instant.now().toString()
        ));
    }
}
