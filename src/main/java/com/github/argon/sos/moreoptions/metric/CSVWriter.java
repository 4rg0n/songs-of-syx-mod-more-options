package com.github.argon.sos.moreoptions.metric;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.StringUtil;
import lombok.AccessLevel;
import lombok.Getter;
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
 * Writes data in CSV format into a file
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CSVWriter {

    @Getter(lazy = true)
    private final static CSVWriter instance = new CSVWriter();

    private final static Logger log = Loggers.getLogger(CSVWriter.class);

    @Setter
    private String delimiter = ";";

    public void write(Path path, List<Metric> metrics) throws IOException {
        if (metrics.isEmpty()) {
            return;
        }
        List<String> headersMetric = headersMetric();
        List<String> headersStats = headersStats(metrics.iterator().next());

        List<String> lines = metrics.stream()
            .map(metric ->
                // merge metric values and stats together
                toLineMetric(headersMetric, metric) +
                delimiter +
                toLineStats(headersStats, metric.getValues()))
            .collect(Collectors.toList());

        File file = path.toFile();

        // build CSV header if needed
        if (needsHeader(file)) {
            List<String> headers = new ArrayList<>();
            headers.addAll(headersMetric);
            headers.addAll(headersStats);

            log.debug("Create new CSV file %s", file.toPath());
            String headerLine = toHeaderLine(headers);
            log.trace("Header: %s", headerLine);

            ArrayList<String> newLines = new ArrayList<>();
            newLines.add(headerLine);
            newLines.addAll(lines);

            lines = newLines;
        }

        write(file, lines);
    }

    public void write(File file, List<String> lines) throws IOException {
        Files.write(
            file.toPath(),
            (lines.stream().collect(Collectors.joining(System.lineSeparator())) + System.lineSeparator()).getBytes(),
            StandardOpenOption.APPEND);
    }

    public boolean needsHeader(File file) throws IOException {
        return file.createNewFile();
    }

    public String toHeaderLine(List<String> keys) {
        return keys.stream()
            .collect(Collectors.joining(delimiter));
    }

    public String toLineStats(List<String> header, Map<String, Object> stats) {
        List<Object> values = header.stream()
            .map(stats::get)
            .collect(Collectors.toList());

        return values.stream()
            .map(this::escapeSpecialCharacters)
            .collect(Collectors.joining(delimiter));
    }

    public String toLineMetric(List<String> headers, Metric metric) {
        List<Object> values = headers.stream()
            .map(metric::get)
            .collect(Collectors.toList());

        return values.stream()
            .map(this::escapeSpecialCharacters)
            .collect(Collectors.joining(delimiter));
    }

    public String escapeSpecialCharacters(Object data) {
        if (data == null) {
           return "null";
        }

        if (data instanceof String) {
            String dataString = (String) data;
            String escapedData = dataString.replaceAll("\\R", " ");
            if (dataString.contains(",") || dataString.contains("\"") || dataString.contains("'")) {
                dataString = dataString.replace("\"", "\"\"");
                escapedData = "\"" + dataString + "\"";
            }

            return escapedData;
        }

        return StringUtil.stringifyValue(data);
    }

    public List<String> headersStats(Metric metric) {
        return metric.getValues().keySet().stream()
            .sorted()
            .collect(Collectors.toList());
    }

    public List<String> headersMetric() {
        return Metric.getHeaders();
    }
}
