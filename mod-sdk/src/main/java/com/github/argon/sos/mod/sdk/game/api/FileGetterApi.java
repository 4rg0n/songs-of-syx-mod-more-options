package com.github.argon.sos.mod.sdk.game.api;

import com.github.argon.sos.mod.sdk.json.Json;
import com.github.argon.sos.mod.sdk.json.JsonMapper;
import snake2d.util.file.FileGetter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class FileGetterApi implements IFileLoad {
    private HashMap<String, Json> loadedData;

    @Override
    public <T> T get(String key, Class<T> type) {
        Json json = loadedData.get(key);

        return JsonMapper.mapJson(json, type);
    }

    void onGameLoaded(FileGetter fileGetter) throws IOException {
        int length = fileGetter.i();
        byte[] jsonBytes = new byte[length];
        fileGetter.bs(jsonBytes);

        Json json = new Json(new String(jsonBytes, StandardCharsets.UTF_8));

        loadedData = JsonMapper.mapJson(json, loadedData.getClass());
    }
}
