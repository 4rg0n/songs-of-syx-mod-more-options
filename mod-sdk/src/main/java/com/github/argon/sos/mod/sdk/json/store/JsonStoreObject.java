package com.github.argon.sos.mod.sdk.json.store;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds a data class with multiple instances of it.
 * Each instance holding data read from a different file with the sam JSON structure.
 * This allows you to have multiple files with different data states in it.
 *
 * @param <T>
 */
@Builder
public class JsonStoreObject<T> {
    @NonNull
    private Class<T> clazz;

    @Getter
    @Accessors(fluent = true, chain = false)
    @Builder.Default
    private Map<Path, T> entries = new HashMap<>();

    @Nullable
    public T get(@Nullable Path filePath) {
        if (filePath == null) {
            return null;
        }

        return entries.get(filePath);
    }

    public T put(Path filePath, T object) {
        entries.put(filePath, object);
        return object;
    }
}
