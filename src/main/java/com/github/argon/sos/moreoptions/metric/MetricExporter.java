package com.github.argon.sos.moreoptions.metric;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import init.paths.PATHS;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

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
    @Nullable
    private Path exportFile;

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

        newExportFile();
    }

    public void newExportFile() {
        exportFile = EXPORT_FOLDER.resolve(Instant.now().getEpochSecond() + "_MetricExport.csv");
        log.debug("New metrics export file: %s", exportFile);
    }

    public boolean export() {
        if (exportFile == null) {
            log.warn("No export file name set yet. Can not export.");
            return false;
        }
        List<Metric> metrics;
        try {
            metrics = metricCollector.flush();
        } catch (Exception e) {
            log.error("Could not not flush buffered metrics", e);
            return false;
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
