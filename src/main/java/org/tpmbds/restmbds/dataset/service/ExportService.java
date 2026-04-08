package org.tpmbds.restmbds.dataset.service;

import org.springframework.stereotype.Service;
import org.tpmbds.restmbds.common.exception.BadRequestException;
import org.tpmbds.restmbds.domain.exporter.ExporterRegistry;

@Service
public class ExportService {

    private final DatasetService datasetService;
    private final ExporterRegistry registry;

    public ExportService(DatasetService datasetService, ExporterRegistry registry) {
        this.datasetService = datasetService;
        this.registry = registry;
    }

    public String exportProject(Long id, String format) {
        var exporter = registry.get(format);
        if (exporter == null) {
            throw new BadRequestException("Unsupported export format: '" + format + "'");
        }
        // preview() retourne PreviewResponse — on extrait la Map de données brutes
        var data = datasetService.preview(id).getData();
        return exporter.export(data);
    }
}
