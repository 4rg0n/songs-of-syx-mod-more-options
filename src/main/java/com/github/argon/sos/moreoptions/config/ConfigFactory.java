package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.config.json.JsonConfigStore;
import com.github.argon.sos.moreoptions.config.json.JsonMeta;
import com.github.argon.sos.moreoptions.json.JacksonService;
import com.github.argon.sos.moreoptions.config.json.v2.JsonMoreOptionsV2Config;
import com.github.argon.sos.moreoptions.config.json.v2.JsonRacesV2Config;
import com.github.argon.sos.moreoptions.config.json.v3.JsonBoostersV3Config;
import com.github.argon.sos.moreoptions.config.json.v3.JsonMoreOptionsV3Config;
import com.github.argon.sos.moreoptions.config.json.v3.JsonRacesV3Config;
import com.github.argon.sos.moreoptions.config.json.v4.JsonBoostersV4Config;
import com.github.argon.sos.moreoptions.config.json.v4.JsonMoreOptionsV4Config;
import com.github.argon.sos.moreoptions.config.json.v4.JsonRacesV4Config;
import com.github.argon.sos.moreoptions.game.api.GameSaveApi;
import com.github.argon.sos.moreoptions.io.FileService;
import com.github.argon.sos.moreoptions.phase.Phases;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.github.argon.sos.moreoptions.config.ConfigDefaults.*;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigFactory implements Phases {
    @Getter(lazy = true)
    private final static ConfigFactory instance = new ConfigFactory(
        GameSaveApi.getInstance(),
        JacksonService.getInstance(),
        FileService.getInstance()
    );

    private final GameSaveApi gameSaveApi;
    private final JacksonService jsonService;
    private final FileService fileService;

    public JsonConfigStore newJsonConfigStoreV2() {
        // bind older v2 config classes for backwards compatibility
        JsonConfigStore jsonConfigStore = new JsonConfigStore(gameSaveApi, jsonService, fileService,2);

        jsonConfigStore.bind(JsonMeta.class, CONFIG_FILE_PATH, false);
        jsonConfigStore.bind(JsonMoreOptionsV2Config.class, CONFIG_FILE_PATH, true);
        jsonConfigStore.bindToSave(JsonRacesV2Config.class, "RacesConfig", RACES_CONFIG_FOLDER_PATH, true);

        return jsonConfigStore;
    }

    public JsonConfigStore newJsonConfigStoreV3() {
        JsonConfigStore jsonConfigStore = new JsonConfigStore(gameSaveApi, jsonService, fileService, 3);

        // bind current config classes with file paths to store
        jsonConfigStore.bind(JsonMeta.class, CONFIG_FILE_PATH, false);
        jsonConfigStore.bind(JsonMoreOptionsV3Config.class, CONFIG_FILE_PATH, true);
        jsonConfigStore.bindToSave(JsonRacesV3Config.class, "RacesConfig", RACES_CONFIG_FOLDER_PATH, true);
        jsonConfigStore.bindToSave(JsonBoostersV3Config.class, "BoostersConfig", BOOSTERS_CONFIG_FOLDER_PATH, true);

        return jsonConfigStore;
    }

    public JsonConfigStore newJsonConfigStoreV4() {
        JsonConfigStore jsonConfigStore = new JsonConfigStore(gameSaveApi, jsonService, fileService, 4);

        // bind current config classes with file paths to store
        jsonConfigStore.bind(JsonMeta.class, CONFIG_FILE_PATH, false);
        jsonConfigStore.bind(JsonMoreOptionsV4Config.class, CONFIG_FILE_PATH, true);
        jsonConfigStore.bindToSave(JsonRacesV4Config.class, "RacesConfig", RACES_CONFIG_FOLDER_PATH, true);
        jsonConfigStore.bindToSave(JsonBoostersV4Config.class, "BoostersConfig", BOOSTERS_CONFIG_FOLDER_PATH, true);

        return jsonConfigStore;
    }

}
