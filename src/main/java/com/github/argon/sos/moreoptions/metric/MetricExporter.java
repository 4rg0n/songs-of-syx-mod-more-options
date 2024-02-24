package com.github.argon.sos.moreoptions.metric;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import init.paths.PATHS;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MetricExporter {
    @Getter(lazy = true)
    private final static MetricExporter instance = new MetricExporter(
        MetricCollector.getInstance(),
        CSVWriter.getInstance()
    );

    private final static Logger log = Loggers.getLogger(MetricExporter.class);

    public final static Path EXPORT_FILE = PATHS.local().PROFILE.get().resolve("MetricsExport.csv");

    private final MetricCollector metricCollector;

    private final CSVWriter csvWriter;

    public boolean export() {
        try {
            List<Metric> metrics = metricCollector.flush();
            log.debug("Exporting %s metrics to %s", metrics.size(), EXPORT_FILE);
            csvWriter.write(EXPORT_FILE, metrics);
            return true;
        } catch (Exception e) {
            log.warn("Could not export to %s", EXPORT_FILE, e);
        }

        return false;
    }
}
