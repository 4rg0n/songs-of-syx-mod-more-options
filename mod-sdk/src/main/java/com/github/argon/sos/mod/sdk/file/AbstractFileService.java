package com.github.argon.sos.mod.sdk.file;

import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;

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
import java.util.stream.Stream;

/**
 * Provides basic methods to deal with reading and writing files and their content
 */
public abstract class AbstractFileService implements IOService {

    private final static Logger log = Loggers.getLogger(AbstractFileService.class);

    /**
     * Will read the content from an {@link InputStream}.
     * Each line will be separated by a "\n" linebreak.
     *
     * @param inputStream to read content from
     * @return the actual file content
     * @throws IOException when reading of the file fails
     */
    protected String readFromInputStream(InputStream inputStream) throws IOException {
        return String.join("\n", readLinesFromInputStream(inputStream));
    }

    /**
     * Will read the content from an {@link InputStream} as list.
     * Where each entry is a line in the file.
     *
     * @param inputStream to read lines from
     * @return read file content as list
     * @throws IOException when reading of the file fails
     */
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
     * Will read the metadata of a file by the given file path.
     *
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
            log.warn("Could not read file meta from %s", filePath, e);
        }

        return null;
    }

    /**
     * Will read the metadata of all files in a folder (not recursive).
     *
     * @return meta information of a all files in a folder or an empty list if the folder does not exist or can not be read
     */
    @Override
    public List<FileMeta> readMetas(Path folderPath) {
        try (Stream<Path> stream = Files.list(folderPath)) {
            List<FileMeta> metas = stream
                .map(this::readMeta)
                .filter(Objects::nonNull)
                .toList();

            log.debug("Loaded %s file metas from folder %s", metas.size(), folderPath);
            return metas;
        } catch (IOException e) {
            log.info("Could not load file metas from folder %s", folderPath, e);
            return List.of();
        }
    }
}
