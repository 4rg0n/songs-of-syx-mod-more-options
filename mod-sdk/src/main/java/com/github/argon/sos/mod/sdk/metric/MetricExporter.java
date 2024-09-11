package com.github.argon.sos.mod.sdk.metric;

import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.phase.Phases;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

/**
 * For exporting game stats as {@link Metric} into a CSV file
 */
@RequiredArgsConstructor
public class MetricExporter implements Phases {

    private final static Logger log = Loggers.getLogger(MetricExporter.class);

    @Getter
    public final Path exportFolder;

    @Getter
    private Path exportFile = generateExportFile();

    private final MetricCollector metricCollector;
    private final MetricCsvWriter metricCsvWriter;

    public void newExportFile() {
        exportFile = generateExportFile();
        log.debug("New metrics export file: %s", exportFile);
    }

    private Path generateExportFile() {
        return exportFolder.resolve(Instant.now().getEpochSecond() + "_MetricExport.csv");
    }

    public boolean export() {
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
            metricCsvWriter.write(exportFile, metrics);
        } catch (Exception e) {
            log.error("Could not export metrics to: %s. %s metrics were lost :(", exportFile, metrics.size(), e);
            return false;
        }

        return true;
    }

    @Override
    public void initBeforeGameCreated() {
        if (!Files.isDirectory(exportFolder)) {
            try {
                log.debug("Create metrics export folder at %s", exportFolder);
                Files.createDirectories(exportFolder);
            } catch (Exception e) {
                log.error("Could not create metrics export folder at %s", exportFolder, e);
            }
        }
    }

    @Override
    public void onGameSaveReloaded() {
        // start a new export file on load
        newExportFile();
    }
}
