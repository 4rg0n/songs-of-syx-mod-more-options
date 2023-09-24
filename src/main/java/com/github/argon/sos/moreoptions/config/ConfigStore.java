package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.game.api.GameEventsApi;
import com.github.argon.sos.moreoptions.game.api.GameSoundsApi;
import com.github.argon.sos.moreoptions.game.api.GameWeatherApi;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigStore {
    private final static Logger log = Loggers.getLogger(ConfigStore.class);

    private final ConfigJsonService configJsonService;
    private final GameWeatherApi gameWeatherApi;
    private final GameEventsApi gameEventsApi;
    private final GameSoundsApi gameSoundsApi;


    @Getter(lazy = true)
    private final static ConfigStore instance = new ConfigStore(
        ConfigJsonService.getInstance(),
        GameWeatherApi.getInstance(),
        GameEventsApi.getInstance(),
        GameSoundsApi.getInstance()
    );

    private MoreOptionsConfig currentConfig;

    public MoreOptionsConfig getDefault() {
       return MoreOptionsConfig.builder()
            .eventsWorld(gameEventsApi.getWorldEvents().keySet().stream()
                .collect(Collectors.toMap(key -> key, o -> true)))
            .eventsSettlement(gameEventsApi.getSettlementEvents().keySet().stream()
                .collect(Collectors.toMap(key -> key, o -> true)))
            .soundsAmbience(gameSoundsApi.getAmbienceSounds().keySet().stream()
                .collect(Collectors.toMap(key -> key, o -> 100)))
            .soundsSettlement(gameSoundsApi.getSettlementSounds().keySet().stream()
                .collect(Collectors.toMap(key -> key, o -> 100)))
            .weather(gameWeatherApi.getWeatherThings().keySet().stream()
                .collect(Collectors.toMap(key -> key, o -> 100)))
            .build();
    }

    /**
     * Used by the mod as current configuration to apply and use
     */
    public void setCurrentConfig(MoreOptionsConfig currentConfig) {
        log.trace("Set current config to: %s", currentConfig);
        this.currentConfig = currentConfig;
    }

    public Optional<MoreOptionsConfig> getCurrentConfig() {
        return Optional.ofNullable(currentConfig);
    }

    /**
     * @return configuration loaded from file
     */
    public Optional<MoreOptionsConfig> loadConfig() {
        return configJsonService.loadConfig();
    }

    /**
     * Saves config into file
     *
     * @return whether saving was successful
     */
    public boolean saveConfig(MoreOptionsConfig config) {
       return configJsonService.saveConfig(config);
    }
}
