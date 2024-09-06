package com.github.argon.sos.mod.sdk.file;

import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.util.Lists;
import lombok.*;
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
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Simple service for handling file operations, which are not resources.
 * So everything outside the *.jar file.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FileService extends AbstractFileService {
    @Getter(lazy = true)
    private final static FileService instance = new FileService();
    private final static Logger log = Loggers.getLogger(FileService.class);
    public final static Charset CHARSET = StandardCharsets.UTF_8;

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

    /**
     * @return meta information of a file or null if the file does not exist or can not be read
     */
    @Nullable
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

    @Getter
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FileMeta {
        private Path path;
        private boolean isDirectory;
        private boolean isFile;
        private boolean isOther;
        private boolean isLink;
        private Instant creationTime;
        private Instant updateTime;
        private Instant accessTime;

        public static class FileMetaBuilder {
            public FileMetaBuilder fromFileAttributes(Path path, BasicFileAttributes fileAttributes) {
                return FileMeta.builder()
                    .path(path)
                    .isDirectory(fileAttributes.isDirectory())
                    .isFile(fileAttributes.isRegularFile())
                    .isOther(fileAttributes.isOther())
                    .isLink(fileAttributes.isSymbolicLink())
                    .accessTime(fileAttributes.lastAccessTime().toInstant())
                    .updateTime(fileAttributes.lastModifiedTime().toInstant())
                    .creationTime(fileAttributes.creationTime().toInstant());
            }
        }
    }
}
