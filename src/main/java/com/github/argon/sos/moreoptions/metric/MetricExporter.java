package com.github.argon.sos.moreoptions.metric;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import init.paths.PATHS;
import lombok.Getter;

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

    public final static Path EXPORT_PATH = PATHS.local().PROFILE.get();

    private Path exportFilePath;

    private final MetricCollector metricCollector;

    private final CSVWriter csvWriter;

    private MetricExporter(MetricCollector metricCollector, CSVWriter csvWriter) {
        this.metricCollector = metricCollector;
        this.csvWriter = csvWriter;
        newExportFile();
    }

    public void newExportFile() {
        exportFilePath = EXPORT_PATH.resolve(Instant.now().getEpochSecond() + "_MetricExport.csv");
        log.debug("New metric export file path: %s", exportFilePath);
    }

    public boolean export() {
        try {
            List<Metric> metrics = metricCollector.flush();
            log.debug("Exporting %s metrics to %s", metrics.size(), exportFilePath);
            csvWriter.write(exportFilePath, metrics);
            return true;
        } catch (Exception e) {
            log.warn("Could not export metrics to %s", exportFilePath, e);
        }

        return false;
    }
}
