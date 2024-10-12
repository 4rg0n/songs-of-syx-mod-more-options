package com.github.argon.sos.mod.sdk.game.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.argon.sos.mod.sdk.ModSdkModule;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import snake2d.util.file.FileGetter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class FileGetterApi implements IFileLoad {
    private final static Logger log = Loggers.getLogger(GameSaveApi.class);
    private HashMap<String, JsonNode> loadedData;

    @Override
    public <T> T get(String key, Class<T> type) {
        JsonNode json = loadedData.get(key);

        try {
            return ModSdkModule.jacksonJsonMapper().readValue(json.toString(), type);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize json with key: " + key + ", value: " + json.toString());
            return null;
        }
    }

    void onGameLoaded(FileGetter fileGetter) throws IOException {
        if (fileGetter == null) {
            return;
        }

        int length = fileGetter.i();
        byte[] jsonBytes = new byte[length];
        fileGetter.bs(jsonBytes);

        String json = new String(jsonBytes, StandardCharsets.UTF_8);

        loadedData = ModSdkModule.jacksonJsonMapper().readValue(json, new TypeReference<HashMap<String, JsonNode>>() {
        });
    }
}
