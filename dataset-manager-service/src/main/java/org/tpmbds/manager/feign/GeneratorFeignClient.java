package org.tpmbds.manager.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.tpmbds.manager.feign.dto.PreviewResponse;

/**
 * Client Feign permettant au dataset-manager de déléguer la génération
 * au generator-service.
 *
 * Le dataset-manager conserve les endpoints /api/projects/{id}/preview
 * et /api/projects/{id}/export pour la compatibilité avec les clients existants.
 * Il délègue simplement la logique au generator-service via ce client.
 */
@FeignClient(name = "generator-service")
public interface GeneratorFeignClient {

    @GetMapping("/api/generator/{projectId}/preview")
    PreviewResponse preview(@PathVariable Long projectId);

    @GetMapping("/api/generator/{projectId}/export")
    String export(@PathVariable Long projectId, @RequestParam String format);
}
