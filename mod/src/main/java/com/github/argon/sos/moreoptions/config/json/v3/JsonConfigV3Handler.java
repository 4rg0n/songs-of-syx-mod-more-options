package com.github.argon.sos.moreoptions.config.json.v3;

import com.github.argon.sos.mod.sdk.config.json.AbstractJsonConfigVersionHandler;
import com.github.argon.sos.mod.sdk.config.json.JsonConfigStore;
import com.github.argon.sos.moreoptions.config.ConfigMapper;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV5Config;
import com.github.argon.sos.moreoptions.config.json.v4.JsonConfigV4Mapper;
import com.github.argon.sos.moreoptions.config.json.v5.JsonConfigV5Mapper;

import java.util.Optional;

public class JsonConfigV3Handler extends AbstractJsonConfigVersionHandler<MoreOptionsV5Config> {
    public JsonConfigV3Handler(JsonConfigStore jsonConfigStore) {
        super(jsonConfigStore);
    }

    @Override
    public Optional<MoreOptionsV5Config> handle(int version) {
        return jsonConfigStore.get(JsonMoreOptionsV3Config.class)
            .map(JsonConfigV4Mapper::map)
            .map(JsonConfigV5Mapper::map)
            .map(ConfigMapper::mapConfig)
            // add other configs
            .map(domainConfig -> {
                jsonConfigStore.get(JsonRacesV3Config.class)
                    .map(JsonConfigV4Mapper::map)
                    .map(JsonConfigV5Mapper::map)
                    .ifPresent(racesConfig -> ConfigMapper.mapInto(racesConfig, domainConfig));
                jsonConfigStore.get(JsonBoostersV3Config.class)
                    .map(JsonConfigV4Mapper::map)
                    .map(JsonConfigV5Mapper::map)
                    .ifPresent(boostersConfig -> ConfigMapper.mapInto(boostersConfig, domainConfig));
                return domainConfig;
            });
    }
}
