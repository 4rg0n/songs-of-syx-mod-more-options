package com.github.argon.sos.moreoptions.json;

import java.nio.file.Path;
import java.util.Optional;

public interface JsonService {
    <T> Optional<T> load(Path path, Class<T> clazz);
    boolean save(Path path, Object object);
    boolean delete(Path path);
}
