package com.github.argon.sos.mod.sdk.json.store.filepath;

import com.github.argon.sos.mod.sdk.json.store.JsonStoreDefinition;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public interface FilePathGenerator {
    String FILE_ENDING = "txt";
    String BACKUP_FILE_PREFIX = "backup";

    @Nullable
    Path filePath(@Nullable JsonStoreDefinition definition);

    @Nullable
    Path backupPath(@Nullable JsonStoreDefinition definition);
}
