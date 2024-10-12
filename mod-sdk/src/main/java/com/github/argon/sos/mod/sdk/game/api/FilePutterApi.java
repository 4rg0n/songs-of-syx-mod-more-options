package com.github.argon.sos.mod.sdk.game.api;

import com.github.argon.sos.mod.sdk.json.JsonMapper;
import snake2d.util.file.FilePutter;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class FilePutterApi implements IFileSave {
    private HashMap<String, Object> dataToWrite;

    public FilePutterApi() {
        dataToWrite = new HashMap<>();
    }

    @Override
    public void put(String key, Object data) {
        dataToWrite.put(key, data);
    }

    void onGameSaved(FilePutter filePutter) {
        byte[] json = JsonMapper.mapObject(dataToWrite).toString().getBytes(StandardCharsets.UTF_8);
        filePutter.i(json.length);
        filePutter.bs(json);
    }
}

