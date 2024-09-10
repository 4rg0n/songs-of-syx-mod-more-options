package com.github.argon.sos.mod.sdk.file;

import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.util.Lists;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractFileService implements IOService {

    private final static Logger log = Loggers.getLogger(AbstractFileService.class);

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

    /**
     * @return meta information of a file or null if the file does not exist or can not be read
     */
    @Override
    public FileMeta readMeta(Path filePath) {
        log.trace("Loading meta from file %s", filePath);
        try {
            BasicFileAttributes fileAttributes = Files.getFileAttributeView(filePath, BasicFileAttributeView.class).readAttributes();
            return FileMeta.builder()
                .fromFileAttributes(filePath, fileAttributes)
                .build();
        } catch (Exception e) {
            log.info("Could not read file meta from %s", filePath, e);
        }

        return null;
    }

    /**
     * @return meta information of a folder or null if the file does not exist or can not be read
     */
    @Override
    public List<FileMeta> readMetas(Path folderPath) {
        try (Stream<Path> stream = Files.list(folderPath)) {
            List<FileMeta> metas = stream
                .map(this::readMeta)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

            log.debug("Loaded %s file metas from folder %s", metas.size(), folderPath);
            return metas;
        } catch (IOException e) {
            log.info("Could not load file metas from folder %s", folderPath, e);
            return Lists.of();
        }
    }
}
