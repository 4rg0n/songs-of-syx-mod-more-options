package com.github.argon.sos.mod.sdk.file;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface IOService {
    @Nullable String read(Path path) throws IOException;
    List<String> readLines(Path path) throws IOException;
    void write(Path path, String content) throws IOException;
    boolean delete(Path path) throws IOException;
    @Nullable FileMeta readMeta(Path filePath);
    List<FileMeta> readMetas(Path folderPath);
}
