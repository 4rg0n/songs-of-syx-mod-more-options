package com.github.argon.sos.mod.sdk.metric;

import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.phase.Phases;
import lombok.Getter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

/**
 * For exporting game stats as {@link Metric} into a CSV file
 */
public class MetricExporter implements Phases {

    private final static Logger log = Loggers.getLogger(MetricExporter.class);

    public final static String EXPORT_FILE_NAME_SUFFIX = "MetricExport.csv";

    /**
     * Folder to write export CSV files into
     */
    @Getter
    private final Path exportFolder;

    /**
     * The current CSV file path to write metrics into.
     */
    @Getter
    private Path exportFile;

    private final MetricCollector metricCollector;
    private final MetricCsvWriter metricCsvWriter;

    /**
     * Creates a new {@link MetricExporter} with the given folder to write CSV files into.
     *
     * @param exportFolder to write CSV files into
     * @param metricCollector to collect the stats
     * @param metricCsvWriter to write the stats as CSV file
     */
    public MetricExporter(Path exportFolder, MetricCollector metricCollector, MetricCsvWriter metricCsvWriter) {
        this.exportFolder = exportFolder;
        this.metricCollector = metricCollector;
        this.metricCsvWriter = metricCsvWriter;

        exportFile = generateExportFile();
    }

    /**
     * Will create a new export CSV file in the configured folder.
     */
    public void newExportFile() {
        exportFile = generateExportFile();
        log.debug("New metrics export file: %s", exportFile);
    }

    /**
     * Will generate the export CSV file path with the current timestamp in seconds and {@link MetricExporter#EXPORT_FILE_NAME_SUFFIX}.
     *
     * @return export file path
     */
    private Path generateExportFile() {
        return exportFolder.resolve(Instant.now().getEpochSecond() + "_" + EXPORT_FILE_NAME_SUFFIX);
    }

    /**
     * Will flush the currently collected metrics and write them into a CSV file.
     *
     * @return whether export was successful
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void onGameSaveReloaded() {
        // start a new export file on load
        newExportFile();
    }
}
