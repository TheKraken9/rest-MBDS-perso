package org.tpmbds.restmbds.dataset.service;

import org.springframework.stereotype.Service;
import org.tpmbds.restmbds.domain.exporter.ExporterRegistry;

@Service
public class ExportService {

    private final DatasetService datasetService;
    private final ExporterRegistry registry;

    public ExportService(DatasetService datasetService,
                         ExporterRegistry registry) {
        this.datasetService = datasetService;
        this.registry = registry;
    }

    public String exportProject(Long id, String format) {

        var data = datasetService.preview(id);

        var exporter = registry.get(format);

        return exporter.export(data);
    }
}