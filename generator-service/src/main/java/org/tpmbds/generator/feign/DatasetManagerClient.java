package org.tpmbds.generator.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.tpmbds.generator.dto.ProjectDefinitionDto;

/**
 * Client Feign brut vers dataset-manager-service.
 * Pas de @CircuitBreaker ici — le circuit breaker est géré au niveau
 * de GeneratorService (Spring AOP sur une classe concrète = proxy CGLIB fiable).
 */
@FeignClient(name = "dataset-manager-service")
public interface DatasetManagerClient {

    @GetMapping("/api/projects/{id}")
    ProjectDefinitionDto getProject(@PathVariable Long id);
}
