package org.tpmbds.manager.model.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tpmbds.manager.model.dto.request.CreateEntityRequest;
import org.tpmbds.manager.model.dto.request.UpdateEntityRequest;
import org.tpmbds.manager.model.dto.response.EntityResponse;
import org.tpmbds.manager.model.service.EntityModelService;

import java.util.List;

@RestController
public class EntityController {

    private final EntityModelService service;

    public EntityController(EntityModelService service) {
        this.service = service;
    }

    @PostMapping("/api/projects/{projectId}/entities")
    public ResponseEntity<EntityResponse> create(@PathVariable Long projectId,
                                                  @Valid @RequestBody CreateEntityRequest request) {
        return ResponseEntity.ok(service.create(projectId, request));
    }

    @GetMapping("/api/projects/{projectId}/entities")
    public ResponseEntity<List<EntityResponse>> list(@PathVariable Long projectId) {
        return ResponseEntity.ok(service.list(projectId));
    }

    @GetMapping("/api/entities/{id}")
    public ResponseEntity<EntityResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/api/entities/{id}")
    public ResponseEntity<EntityResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody UpdateEntityRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/api/entities/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
