package com.github.argon.sos.moreoptions.config.json;

import com.github.argon.sos.moreoptions.config.ConfigFactory;
import com.github.argon.sos.moreoptions.config.ConfigMapper;
import com.github.argon.sos.moreoptions.config.domain.ConfigMeta;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV3Config;
import com.github.argon.sos.moreoptions.config.json.v2.JsonMoreOptionsV2Config;
import com.github.argon.sos.moreoptions.config.json.v2.JsonRacesV2Config;
import com.github.argon.sos.moreoptions.config.json.v3.JsonBoostersV3Config;
import com.github.argon.sos.moreoptions.config.json.v3.JsonMoreOptionsV3Config;
import com.github.argon.sos.moreoptions.config.json.v3.JsonRacesV3Config;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import org.jetbrains.annotations.Nullable;


public class JsonConfigVersionHandler {
    private final static Logger log = Loggers.getLogger(JsonConfigVersionHandler.class);

    private final ConfigFactory configFactory;

    private final JsonConfigStore jsonConfigStore;

    public JsonConfigVersionHandler(ConfigFactory configFactory, JsonConfigStore jsonConfigStore) {
        this.configFactory = configFactory;
        this.jsonConfigStore = jsonConfigStore;
    }

    @Nullable
    public MoreOptionsV3Config handleMapping(ConfigMeta configMeta) {
        return handleMapping(configMeta.getVersion());
    }

    @Nullable
    public MoreOptionsV3Config handleMapping(int version) {
        log.debug("Handling version mapping for V%s", version);
        MoreOptionsV3Config moreOptionsConfig = null;

        switch (version) {
            case MoreOptionsV3Config.VERSION:
                if (jsonConfigStore.getVersion() != version) {
                    log.error("Can not handle mapping to current version %s, because given json config store has different version %s.",
                        MoreOptionsV3Config.VERSION, jsonConfigStore.getVersion());
                    break;
                }
                moreOptionsConfig = jsonConfigStore.get(JsonMoreOptionsV3Config.class)
                    .map(ConfigMapper::mapConfig)
                    // add other configs
                    .map(domainConfig -> {
                        jsonConfigStore.get(JsonRacesV3Config.class)
                            .ifPresent(racesConfig -> ConfigMapper.mapInto(racesConfig, domainConfig));
                        jsonConfigStore.get(JsonBoostersV3Config.class)
                            .ifPresent(boostersConfig -> ConfigMapper.mapInto(boostersConfig, domainConfig));
                        return domainConfig;
                    }).orElse(null);
                break;
            case 2:
                JsonConfigStore jsonConfigStoreV2 = configFactory.newJsonConfigStoreV2();
                moreOptionsConfig = jsonConfigStoreV2.get(JsonMoreOptionsV2Config.class)
                    .map(JsonConfigV3Mapper::map)
                    .map(ConfigMapper::mapConfig)
                    .map(config -> {
                        jsonConfigStoreV2.get(JsonRacesV2Config.class).ifPresent(racesConfig -> {
                            JsonRacesV3Config racesV3Config = JsonConfigV3Mapper.map(racesConfig);
                            ConfigMapper.mapInto(racesV3Config, config);
                        });

                        return config;
                    }).orElse(null);
                break;
            default:
                log.info("Can not handle config version V%s", version);
                break;
        }

        return moreOptionsConfig;
    }
}
