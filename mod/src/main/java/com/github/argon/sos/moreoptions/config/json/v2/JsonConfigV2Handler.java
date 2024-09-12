package com.github.argon.sos.moreoptions.config.json.v2;

import com.github.argon.sos.mod.sdk.config.json.AbstractJsonConfigVersionHandler;
import com.github.argon.sos.mod.sdk.config.json.JsonConfigStore;
import com.github.argon.sos.moreoptions.config.ConfigMapper;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV5Config;
import com.github.argon.sos.moreoptions.config.json.v3.JsonConfigV3Mapper;
import com.github.argon.sos.moreoptions.config.json.v3.JsonRacesV3Config;
import com.github.argon.sos.moreoptions.config.json.v4.JsonConfigV4Mapper;
import com.github.argon.sos.moreoptions.config.json.v4.JsonRacesV4Config;
import com.github.argon.sos.moreoptions.config.json.v5.JsonConfigV5Mapper;
import com.github.argon.sos.moreoptions.config.json.v5.JsonRacesV5Config;

import java.util.Optional;

public class JsonConfigV2Handler extends AbstractJsonConfigVersionHandler<MoreOptionsV5Config> {
    public JsonConfigV2Handler(JsonConfigStore jsonConfigStore) {
        super(jsonConfigStore);
    }

    @Override
    public Optional<MoreOptionsV5Config> handle(int version) {
        return jsonConfigStore.get(JsonMoreOptionsV2Config.class)
            .map(JsonConfigV3Mapper::map)
            .map(JsonConfigV4Mapper::map)
            .map(JsonConfigV5Mapper::map)
            .map(ConfigMapper::mapConfig)
            .map(config -> {
                jsonConfigStore.get(JsonRacesV2Config.class).ifPresent(racesConfig -> {
                    JsonRacesV3Config racesV3Config = JsonConfigV3Mapper.map(racesConfig);
                    JsonRacesV4Config racesV4Config = JsonConfigV4Mapper.map(racesV3Config);
                    JsonRacesV5Config racesV5Config = JsonConfigV5Mapper.map(racesV4Config);
                    ConfigMapper.mapInto(racesV5Config, config);
                });

                return config;
            });
    }
}
