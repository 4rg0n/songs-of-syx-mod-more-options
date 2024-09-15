package com.github.argon.sos.moreoptions.config.json;

import com.github.argon.sos.mod.sdk.config.json.JsonConfigStore;
import com.github.argon.sos.mod.sdk.file.IOService;
import com.github.argon.sos.mod.sdk.game.api.GameSaveApi;
import com.github.argon.sos.mod.sdk.json.JacksonService;
import com.github.argon.sos.mod.sdk.json.JsonGameService;
import com.github.argon.sos.mod.sdk.phase.Phases;
import com.github.argon.sos.moreoptions.config.json.v2.JsonMoreOptionsV2Config;
import com.github.argon.sos.moreoptions.config.json.v2.JsonRacesV2Config;
import com.github.argon.sos.moreoptions.config.json.v3.JsonBoostersV3Config;
import com.github.argon.sos.moreoptions.config.json.v3.JsonMoreOptionsV3Config;
import com.github.argon.sos.moreoptions.config.json.v3.JsonRacesV3Config;
import com.github.argon.sos.moreoptions.config.json.v4.JsonBoostersV4Config;
import com.github.argon.sos.moreoptions.config.json.v4.JsonMoreOptionsV4Config;
import com.github.argon.sos.moreoptions.config.json.v4.JsonRacesV4Config;
import com.github.argon.sos.moreoptions.config.json.v5.JsonBoostersV5Config;
import com.github.argon.sos.moreoptions.config.json.v5.JsonMoreOptionsV5Config;
import com.github.argon.sos.moreoptions.config.json.v5.JsonRacesV5Config;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

@RequiredArgsConstructor
public class JsonConfigStoreFactory implements Phases {

    private final GameSaveApi gameSaveApi;
    private final JacksonService jacksonService;
    private final JsonGameService jsonGameService;
    private final IOService ioService;
    private final PathsConfig pathsConfig;

    public JsonConfigStore newJsonConfigStoreV2() {
        // bind older v2 config classes for backwards compatibility
        JsonConfigStore jsonConfigStore = new JsonConfigStore(gameSaveApi, jsonGameService, ioService);

        jsonConfigStore.bind(JsonMeta.class, pathsConfig.getConfigFile(), false);
        jsonConfigStore.bind(JsonMoreOptionsV2Config.class, pathsConfig.getConfigFile(), true);
        jsonConfigStore.bindToSave(JsonRacesV2Config.class, "RacesConfig", pathsConfig.getRaceConfigFolder(), true);

        return jsonConfigStore;
    }

    public JsonConfigStore newJsonConfigStoreV3() {
        JsonConfigStore jsonConfigStore = new JsonConfigStore(gameSaveApi, jsonGameService, ioService);

        // bind current config classes with file paths to store
        jsonConfigStore.bind(JsonMeta.class, pathsConfig.getConfigFile(), false);
        jsonConfigStore.bind(JsonMoreOptionsV3Config.class, pathsConfig.getConfigFile(), true);
        jsonConfigStore.bindToSave(JsonRacesV3Config.class, "RacesConfig", pathsConfig.getRaceConfigFolder(), true);
        jsonConfigStore.bindToSave(JsonBoostersV3Config.class, "BoostersConfig", pathsConfig.getBoosterConfigFolder(), true);

        return jsonConfigStore;
    }

    public JsonConfigStore newJsonConfigStoreV4() {
        JsonConfigStore jsonConfigStore = new JsonConfigStore(gameSaveApi, jsonGameService, ioService);

        // bind current config classes with file paths to store
        jsonConfigStore.bind(JsonMeta.class, pathsConfig.getConfigFile(), false);
        jsonConfigStore.bind(JsonMoreOptionsV4Config.class, pathsConfig.getConfigFile(), true);
        jsonConfigStore.bindToSave(JsonRacesV4Config.class, "RacesConfig", pathsConfig.getRaceConfigFolder(), true);
        jsonConfigStore.bindToSave(JsonBoostersV4Config.class, "BoostersConfig", pathsConfig.getBoosterConfigFolder(), true);

        return jsonConfigStore;
    }

    public JsonConfigStore newJsonConfigStoreV5() {
        JsonConfigStore jsonConfigStore = new JsonConfigStore(gameSaveApi, jacksonService, ioService);

        // bind current config classes with file paths to store
        jsonConfigStore.bind(JsonMeta.class, pathsConfig.getConfigFile(), false);
        jsonConfigStore.bind(JsonMoreOptionsV5Config.class, pathsConfig.getConfigFile(), true);
        jsonConfigStore.bindToSave(JsonRacesV5Config.class, "RacesConfig", pathsConfig.getRaceConfigFolder(), true);
        jsonConfigStore.bindToSave(JsonBoostersV5Config.class, "BoostersConfig", pathsConfig.getBoosterConfigFolder(), true);

        return jsonConfigStore;
    }

    @Data
    @Builder
    public static class PathsConfig {
        private Path configFile;
        private Path raceConfigFolder;
        private Path boosterConfigFolder;
    }
}
