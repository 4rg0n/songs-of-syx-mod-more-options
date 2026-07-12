package com.github.argon.sos.mod.sdk.metric;

import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Writes {@link Metric} data in CSV format into a file
 */
@RequiredArgsConstructor
public class MetricCsvWriter {

    private final static Logger log = Loggers.getLogger(MetricCsvWriter.class);

    @Setter
    private String delimiter = ";";

    /**
     * Writes collected {@link Metric}s into the given file path as CSV.
     *
     * @param path for the CSV file
     * @param metrics to write
     * @throws IOException when writing the file fails
     */
    public void write(Path path, List<Metric> metrics) throws IOException {
        if (metrics.isEmpty()) {
            return;
        }

        Metric metricsFirst = metrics.getFirst();
        List<String> headersMetric = Metric.getHeaders();
        List<String> headersStats = metricsFirst.getHeaderStats();

        List<String> lines = metrics.stream()
            .map(metric ->
                // merge metric values and stats together
                toLineMetric(headersMetric, metric) +
                delimiter +
                toLineStats(headersStats, metric.getValues()))
            .toList();

        File csvFile = path.toFile();

        // build CSV header if needed
        if (needsHeader(csvFile)) {
            List<String> headers = new ArrayList<>();
            headers.addAll(headersMetric);
            headers.addAll(headersStats);

            log.debug("Create new CSV file %s", csvFile.toPath());
            String headerLine = toHeaderLine(headers);
            log.trace("Header: %s", headerLine);

            ArrayList<String> newLines = new ArrayList<>();
            newLines.add(headerLine);
            newLines.addAll(lines);

            lines = newLines;
        }

        write(csvFile, lines);
    }

    private void write(File file, List<String> lines) throws IOException {
        Files.write(
            file.toPath(),
            (lines.stream().collect(Collectors.joining(System.lineSeparator())) + System.lineSeparator()).getBytes(),
            StandardOpenOption.APPEND);
    }

    /**
     * Checks whether a file is not written yet and therefore needs a header.
     *
     * @param file to check
     * @return whether file needs headers
     * @throws IOException when creating the file fails.
     */
    public boolean needsHeader(File file) throws IOException {
        return file.createNewFile();
    }

    /**
     * Creates the CSV headers.
     *
     * @param headerNames to create the header with
     * @return created header line
     */
    public String toHeaderLine(List<String> headerNames) {
        return headerNames.stream()
            .collect(Collectors.joining(delimiter));
    }

    /**
     * Creates the content to write into a CSV file from given header names and stats.
     *
     * @param headerNames to use as CSV headers
     * @param stats to use for each line
     * @return build CSV content
     */
    public String toLineStats(List<String> headerNames, Map<String, Object> stats) {
        List<Object> values = headerNames.stream()
            .map(stats::get)
            .toList();

        return values.stream()
            .map(this::escapeSpecialCharacters)
            .collect(Collectors.joining(delimiter));
    }

    /**
     * Creates the content to write into a CSV file from given header names and {@link Metric}.
     *
     * @param headerNames to use as CSV headers
     * @param metric to use for each line
     * @return build CSV content
     */
    public String toLineMetric(List<String> headerNames, Metric metric) {
        List<Object> values = headerNames.stream()
            .map(metric::get)
            .toList();

        return values.stream()
            .map(this::escapeSpecialCharacters)
            .collect(Collectors.joining(delimiter));
    }

    private String escapeSpecialCharacters(Object data) {
        if (data == null) {
           return "null";
        }

        if (data instanceof String dataString) {
            return StringUtil.escapeSpecialCharacters(dataString);
        }

        return StringUtil.stringify(data);
    }
}
