package org.tpmbds.gateway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/dataset-manager")
    public ResponseEntity<Map<String, Object>> datasetManagerFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "status", "PARTIAL",
                "message", "dataset-manager-service momentanément indisponible. Réessayez plus tard.",
                "timestamp", Instant.now().toString()
        ));
    }

    @RequestMapping("/generator")
    public ResponseEntity<Map<String, Object>> generatorFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "status", "PARTIAL",
                "message", "generator-service momentanément indisponible. Réessayez plus tard.",
                "timestamp", Instant.now().toString()
        ));
    }
}
