package org.tpmbds.restmbds.dataset.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tpmbds.restmbds.dataset.dto.response.PreviewResponse;
import org.tpmbds.restmbds.dataset.service.DatasetService;
import org.tpmbds.restmbds.dataset.service.ExportService;

@RestController
@RequestMapping("/api/projects")
public class DatasetController {

    private final DatasetService datasetService;
    private final ExportService exportService;

    public DatasetController(DatasetService datasetService, ExportService exportService) {
        this.datasetService = datasetService;
        this.exportService = exportService;
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<PreviewResponse> preview(@PathVariable Long id) {
        return ResponseEntity.ok(datasetService.preview(id));
    }

    @GetMapping("/{id}/export")
    public ResponseEntity<String> export(@PathVariable Long id,
                                         @RequestParam(defaultValue = "json") String format) {
        String content = exportService.exportProject(id, format);
        String filename = "dataset." + format.toLowerCase();

        MediaType mediaType = switch (format.toLowerCase()) {
            case "json" -> MediaType.APPLICATION_JSON;
            case "xml"  -> MediaType.APPLICATION_XML;
            default     -> MediaType.TEXT_PLAIN;
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(mediaType)
                .body(content);
    }
}
