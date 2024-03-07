package com.github.argon.sos.moreoptions.metric;

import com.github.argon.sos.moreoptions.MoreOptionsScript;
import com.github.argon.sos.moreoptions.init.InitPhases;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import init.paths.PATHS;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

/**
 * For exporting game stats as {@link Metric} into a CSV file
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MetricExporter implements InitPhases {
    @Getter(lazy = true)
    private final static MetricExporter instance = new MetricExporter(
        MetricCollector.getInstance(),
        CSVWriter.getInstance()
    );

    private final static Logger log = Loggers.getLogger(MetricExporter.class);

    public static final Path EXPORT_FOLDER = PATHS.local().PROFILE.get()
        .resolve(MoreOptionsScript.MOD_INFO.name + "/exports");

    @Getter
    private Path exportFile = generateExportFile();

    private final MetricCollector metricCollector;
    private final CSVWriter csvWriter;

    public void newExportFile() {
        exportFile = generateExportFile();
        log.debug("New metrics export file: %s", exportFile);
    }

    private Path generateExportFile() {
        return EXPORT_FOLDER.resolve(Instant.now().getEpochSecond() + "_MetricExport.csv");
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
            csvWriter.write(exportFile, metrics);
        } catch (Exception e) {
            log.error("Could not export metrics to: %s. %s metrics were lost :(", exportFile, metrics.size(), e);
            return false;
        }

        return true;
    }

    @Override
    public void initBeforeGameCreated() {
        if (!Files.isDirectory(EXPORT_FOLDER)) {
            try {
                log.debug("Create metrics export folder at %s", EXPORT_FOLDER);
                Files.createDirectories(EXPORT_FOLDER);
            } catch (Exception e) {
                log.error("Could not create metrics export folder at %s", EXPORT_FOLDER, e);
            }
        }
    }

    @Override
    public void initGameSaveReloaded() {
        // start a new export file on load
        newExportFile();
    }
}
