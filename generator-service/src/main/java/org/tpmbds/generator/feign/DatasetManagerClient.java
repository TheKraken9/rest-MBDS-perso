package org.tpmbds.generator.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.tpmbds.generator.dto.ProjectDefinitionDto;
import org.tpmbds.generator.feign.fallback.DatasetManagerFallback;

/**
 * Client Feign pour récupérer la définition d'un projet depuis dataset-manager-service.
 * Protégé par un Circuit Breaker Resilience4J : si le manager est HS,
 * DatasetManagerFallback est appelé et renvoie une réponse de secours.
 */
@FeignClient(name = "dataset-manager-service", fallback = DatasetManagerFallback.class)
public interface DatasetManagerClient {

    @GetMapping("/api/projects/{id}")
    ProjectDefinitionDto getProject(@PathVariable Long id);
}
