package com.github.argon.sos.mod.sdk.file;

import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.util.Lists;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Properties;

/**
 * Simple service for handling file operations, which are not resources.
 * So everything outside the *.jar file.
 */
public class FileService extends AbstractFileService {
    private final static Logger log = Loggers.getLogger(FileService.class);
    public final static Charset CHARSET = StandardCharsets.UTF_8;

    @Override
    public Properties readProperties(Path filePath) throws IOException {
        log.debug("Reading from resource properties file %s", filePath);

        if (!Files.exists(filePath)) {
            // do not read what's not there
            log.info("%s is not a file, does not exists or is not readable", filePath);
            return null;
        }

        Properties properties = new Properties();
        try (InputStream inputStream = Files.newInputStream(filePath)) {
            properties.load(inputStream);
        } catch (Exception e) {
            log.warn("Could not read properties from file %s", filePath);
            throw e;
        }

        return properties;
    }

    @Override
    public List<String> readLines(Path path) throws IOException {
        log.debug("Reading from file %s", path);

        if (!Files.exists(path)) {
            // do not load what's not there
            log.info("%s is not a folder, does not exists or is not readable", path);
            return Lists.of();
        }

        try (InputStream inputStream = Files.newInputStream(path)) {
            return readLinesFromInputStream(inputStream);
        } catch (Exception e) {
            log.error("Could not read from file %s", path, e);
            throw e;
        }
    }

    /**
     * @return content of the file as string or null if the file does not exist
     * @throws IOException if something goes wrong when reading the file
     */
    @Nullable
    public String read(Path path) throws IOException {
        log.debug("Reading from file %s", path);

        if (!Files.exists(path)) {
            // do not load what's not there
            log.info("%s is not a file, does not exists or is not readable", path);
            return null;
        }

        try (InputStream inputStream = Files.newInputStream(path)) {
            return readFromInputStream(inputStream);
        } catch (Exception e) {
            log.error("Could not read from file %s", path, e);
            throw e;
        }
    }

    /**
     * Writes content into a file. Will create the file if it does not exist.
     *
     * @throws IOException if something goes wrong when writing the file
     */
    public void write(Path path, String content) throws IOException {
        log.debug("Writing into file %s", path);
        File parentDirectory = path.getParent().toFile();

        if (!parentDirectory.exists()) {
            try {
                Files.createDirectories(path.getParent());
            } catch (Exception e) {
                log.error("Could not create directories for %s", path);
                throw e;
            }
        }

        try {
            Files.write(path, content.getBytes(CHARSET), StandardOpenOption.CREATE);
        } catch (IOException e) {
            log.error("Could not write into %s", path);
            throw e;
        }
    }

    /**
     * @return whether the file is present anymore
     * @throws IOException if something goes wrong when deleting the file
     */
    public boolean delete(Path path) throws IOException {
        log.debug("Deleting file %s", path);
        try {
            Files.delete(path);
        } catch (NoSuchFileException e) {
            return true;
        } catch (Exception e) {
            log.error("Could not delete file %s", path, e);
            throw e;
        }

        return true;
    }
}
