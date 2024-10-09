package com.github.argon.sos.mod.sdk.json.store.filepath;

import com.github.argon.sos.mod.sdk.json.store.JsonStoreDefinition;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public class SimpleFilePathGenerator implements FilePathGenerator {
    @Override
    @Nullable
    public Path filePath(JsonStoreDefinition definition) {
        if (definition == null) {
            return null;
        }

        String fileName = definition.getName() + "." + FILE_ENDING;
        return definition.getPath().resolve(fileName);
    }

    @Override
    public @Nullable Path backupPath(JsonStoreDefinition definition) {
        if (definition == null) {
            return null;
        }

        String fileName = definition.getName() + "." + BACKUP_FILE_PREFIX + "." + FILE_ENDING;
        return definition.getPath().resolve(fileName);
    }
}