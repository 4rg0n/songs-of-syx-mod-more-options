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
import java.util.stream.Collectors;

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

        List<String> headers = headers(metrics.iterator().next());
        List<String> lines = metrics.stream()
            .map(metric -> toLine(headers, metric))
            .collect(Collectors.toList());

        File file = path.toFile();

        if (needsHeader(file)) {
            log.debug("Create new header for CSV file");
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

    public String toLine(Metric metric) {
        List<String> header = headers(metric);
        return toLine(header, metric);
    }

    public String toLine(List<String> header, Metric metric) {
        List<Object> values = header.stream()
            .map(key -> metric.getValues().get(key))
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

    public List<String> headers(Metric metric) {
        return metric.getValues().keySet().stream()
            .sorted()
            .collect(Collectors.toList());
    }
}
