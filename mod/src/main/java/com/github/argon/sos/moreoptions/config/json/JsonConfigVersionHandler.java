package com.github.argon.sos.moreoptions.config.json;

import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.moreoptions.config.ConfigFactory;
import com.github.argon.sos.moreoptions.config.ConfigMapper;
import com.github.argon.sos.moreoptions.config.domain.ConfigMeta;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV5Config;
import com.github.argon.sos.moreoptions.config.json.v2.JsonMoreOptionsV2Config;
import com.github.argon.sos.moreoptions.config.json.v2.JsonRacesV2Config;
import com.github.argon.sos.moreoptions.config.json.v3.JsonBoostersV3Config;
import com.github.argon.sos.moreoptions.config.json.v3.JsonMoreOptionsV3Config;
import com.github.argon.sos.moreoptions.config.json.v3.JsonRacesV3Config;
import com.github.argon.sos.moreoptions.config.json.v4.JsonBoostersV4Config;
import com.github.argon.sos.moreoptions.config.json.v4.JsonMoreOptionsV4Config;
import com.github.argon.sos.moreoptions.config.json.v4.JsonRacesV4Config;
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
    public MoreOptionsV5Config handleMapping(ConfigMeta configMeta) {
        return handleMapping(configMeta.getVersion());
    }

    @Nullable
    public MoreOptionsV5Config handleMapping(int version) {
        log.debug("Handling version mapping for V%s", version);
        MoreOptionsV5Config moreOptionsConfig = null;

        switch (version) {
            case MoreOptionsV5Config.VERSION:
                if (jsonConfigStore.getVersion() != version) {
                    log.error("Can not handle mapping to current version %s, because given json config store has different version %s.",
                        MoreOptionsV5Config.VERSION, jsonConfigStore.getVersion());
                    break;
                }
                moreOptionsConfig = jsonConfigStore.get(JsonMoreOptionsV4Config.class)
                    .map(ConfigMapper::mapConfig)
                    // add other configs
                    .map(domainConfig -> {
                        jsonConfigStore.get(JsonRacesV4Config.class)
                            .ifPresent(racesConfig -> ConfigMapper.mapInto(racesConfig, domainConfig));
                        jsonConfigStore.get(JsonBoostersV4Config.class)
                            .ifPresent(boostersConfig -> ConfigMapper.mapInto(boostersConfig, domainConfig));
                        return domainConfig;
                    }).orElse(null);
                break;
            case 4:
                JsonConfigStore jsonConfigStoreV4 = configFactory.newJsonConfigStoreV4();
                moreOptionsConfig = jsonConfigStoreV4.get(JsonMoreOptionsV4Config.class)
                    .map(ConfigMapper::mapConfig)
                    // add other configs
                    .map(domainConfig -> {
                        jsonConfigStore.get(JsonRacesV4Config.class)
                            .ifPresent(racesConfig -> ConfigMapper.mapInto(racesConfig, domainConfig));
                        jsonConfigStore.get(JsonBoostersV4Config.class)
                            .ifPresent(boostersConfig -> ConfigMapper.mapInto(boostersConfig, domainConfig));
                        return domainConfig;
                    }).orElse(null);
                break;

            case 3:
                JsonConfigStore jsonConfigStoreV3 = configFactory.newJsonConfigStoreV3();
                moreOptionsConfig = jsonConfigStoreV3.get(JsonMoreOptionsV3Config.class)
                    .map(JsonConfigV4Mapper::map)
                    .map(ConfigMapper::mapConfig)
                    // add other configs
                    .map(domainConfig -> {
                        jsonConfigStoreV3.get(JsonRacesV3Config.class)
                            .map(JsonConfigV4Mapper::map)
                            .ifPresent(racesConfig -> ConfigMapper.mapInto(racesConfig, domainConfig));
                        jsonConfigStoreV3.get(JsonBoostersV3Config.class)
                            .map(JsonConfigV4Mapper::map)
                            .ifPresent(boostersConfig -> ConfigMapper.mapInto(boostersConfig, domainConfig));
                        return domainConfig;
                    }).orElse(null);
                break;
            case 2:
                JsonConfigStore jsonConfigStoreV2 = configFactory.newJsonConfigStoreV2();
                moreOptionsConfig = jsonConfigStoreV2.get(JsonMoreOptionsV2Config.class)
                    .map(JsonConfigV3Mapper::map)
                    .map(JsonConfigV4Mapper::map)
                    .map(ConfigMapper::mapConfig)
                    .map(config -> {
                        jsonConfigStoreV2.get(JsonRacesV2Config.class).ifPresent(racesConfig -> {
                            JsonRacesV3Config racesV3Config = JsonConfigV3Mapper.map(racesConfig);
                            JsonRacesV4Config racesV4Config = JsonConfigV4Mapper.map(racesV3Config);
                            ConfigMapper.mapInto(racesV4Config, config);
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
