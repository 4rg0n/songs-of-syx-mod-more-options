package com.github.argon.sos.moreoptions.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractIOService {
    protected String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        readLinesFromInputStream(inputStream).forEach(line -> resultStringBuilder.append(line).append("\n"));
        return resultStringBuilder.toString();
    }

    protected List<String> readLinesFromInputStream(InputStream inputStream) throws IOException {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }
}
