package com.github.argon.sos.moreoptions.config.json.v5;

import com.github.argon.sos.mod.sdk.config.AbstractJsonConfigVersionHandler;
import com.github.argon.sos.mod.sdk.config.json.JsonConfigStore;
import com.github.argon.sos.moreoptions.config.ConfigMapper;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV5Config;

import java.util.Optional;

public class JsonConfigV5Handler extends AbstractJsonConfigVersionHandler<MoreOptionsV5Config> {
    public JsonConfigV5Handler(JsonConfigStore jsonConfigStore) {
        super(jsonConfigStore);
    }

    @Override
    public Optional<MoreOptionsV5Config> handle(int version) {
        return jsonConfigStore.get(JsonMoreOptionsV5Config.class)
            .map(ConfigMapper::mapConfig)
            // add other configs
            .map(domainConfig -> {
                jsonConfigStore.get(JsonRacesV5Config.class)
                    .ifPresent(racesConfig -> ConfigMapper.mapInto(racesConfig, domainConfig));
                jsonConfigStore.get(JsonBoostersV5Config.class)
                    .ifPresent(boostersConfig -> ConfigMapper.mapInto(boostersConfig, domainConfig));
                return domainConfig;
            });
    }
}
