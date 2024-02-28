package com.github.argon.sos.moreoptions.metric;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import init.paths.PATHS;
import lombok.Getter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

public class MetricExporter {
    @Getter(lazy = true)
    private final static MetricExporter instance = new MetricExporter(
        MetricCollector.getInstance(),
        CSVWriter.getInstance()
    );

    private final static Logger log = Loggers.getLogger(MetricExporter.class);

    public static final Path EXPORT_FOLDER = PATHS.local().PROFILE.get().resolve("exports");

    @Getter
    private Path exportFile = generateExportFile();

    private final MetricCollector metricCollector;

    private final CSVWriter csvWriter;

    private MetricExporter(MetricCollector metricCollector, CSVWriter csvWriter) {
        this.metricCollector = metricCollector;
        this.csvWriter = csvWriter;

        if (!Files.isDirectory(EXPORT_FOLDER)) {
            try {
                Files.createDirectory(EXPORT_FOLDER);
            } catch (Exception e) {
                log.error("Could not create metrics export folder %s", EXPORT_FOLDER, e);
            }
        }
    }

    public void newExportFile() {
        exportFile = generateExportFile();
        log.debug("New metrics export file: %s", exportFile);
    }

    private Path generateExportFile() {
        return EXPORT_FOLDER.resolve(Instant.now().getEpochSecond() + "_MetricExport.csv");
    }

    public boolean export(List<String> statList) {
        List<Metric> metrics;

        try {
            metrics = metricCollector.flush();
        } catch (Exception e) {
            log.error("Could not not flush buffered metrics", e);
            return false;
        }

        if (metrics.isEmpty()) {
            log.debug("No metrics to export");
            return true;
        }

        try {
            log.debug("Exporting %s metrics to %s", metrics.size(), exportFile);
            csvWriter.write(exportFile, metrics);
        } catch (Exception e) {
            log.error("Could not export metrics to: %s. %s metrics were lost :(", exportFile, metrics.size(), e);
            return false;
        }

        return true;
    }
}
