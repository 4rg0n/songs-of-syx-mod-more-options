package com.github.argon.sos.mod.sdk.file;

import lombok.*;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;

/**
 * Contains meta information about a file such as the creation date
 */
@Getter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FileMeta {
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
