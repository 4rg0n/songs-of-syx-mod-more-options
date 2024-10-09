package com.github.argon.sos.mod.sdk.json.store.filepath;

import com.github.argon.sos.mod.sdk.game.api.GameSaveApi;
import com.github.argon.sos.mod.sdk.json.store.JsonStoreDefinition;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

@RequiredArgsConstructor
public class SaveFilePathGenerator implements FilePathGenerator {

    private final GameSaveApi gameSaveApi;

    @Nullable
    @Override
    public Path filePath(JsonStoreDefinition definition) {
        if (definition == null) {
            return null;
        }

        String saveStamp = gameSaveApi.getSaveStamp();

        if (saveStamp == null) {
            return null;
        }

        String fileName = saveStamp + "." + definition.getName() + "." + FILE_ENDING;
        return definition.getPath().resolve(fileName);
    }


    @Nullable
    @Override
    public Path backupPath(JsonStoreDefinition definition) {
        if (definition == null) {
            return null;
        }

        String saveStamp = gameSaveApi.getSaveStamp();
        if (saveStamp == null) {
            return null;
        }

        String fileName = saveStamp + "." + definition.getName() + "." + BACKUP_FILE_PREFIX + "." + FILE_ENDING;
        return definition.getPath().resolve(fileName);
    }
}
