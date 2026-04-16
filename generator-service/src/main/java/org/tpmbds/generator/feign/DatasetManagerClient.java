package org.tpmbds.generator.feign;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.tpmbds.generator.dto.ProjectDefinitionDto;

/**
 * Client Feign pour récupérer la définition d'un projet depuis dataset-manager-service.
 *
 * Circuit Breaker Resilience4J (instance "dataset-manager") :
 * - après 50% d'échecs sur 5 appels, le circuit s'ouvre pendant 10s
 * - getProjectFallback() est appelé → GeneratorService retourne une 503 propre à l'utilisateur
 */
@FeignClient(name = "dataset-manager-service")
public interface DatasetManagerClient {

    @CircuitBreaker(name = "dataset-manager", fallbackMethod = "getProjectFallback")
    @GetMapping("/api/projects/{id}")
    ProjectDefinitionDto getProject(@PathVariable Long id);

    default ProjectDefinitionDto getProjectFallback(Long id, Exception e) {
        return null;
    }
}
