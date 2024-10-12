package com.github.argon.sos.mod.sdk.game.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.argon.sos.mod.sdk.ModSdkModule;
import com.github.argon.sos.mod.sdk.json.JsonMapper;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import snake2d.util.file.FilePutter;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class FilePutterApi implements IFileSave {
    private final static Logger log = Loggers.getLogger(GameSaveApi.class);
    private final HashMap<String, Object> dataToWrite;

    public FilePutterApi() {
        dataToWrite = new HashMap<>();
    }

    @Override
    public void put(String key, Object data) {
        dataToWrite.put(key, data);
    }

    public void onGameSaved(FilePutter filePutter) {
        if(filePutter == null){
            return;
        }

        try {
            byte[] json = ModSdkModule.jacksonJsonMapper().writeValueAsString(dataToWrite).getBytes(StandardCharsets.UTF_8);
            filePutter.bsE(json);
        }
        catch(JsonProcessingException e){
            log.error("Can't serialize save data: " + e.getMessage());
        }
    }
}

