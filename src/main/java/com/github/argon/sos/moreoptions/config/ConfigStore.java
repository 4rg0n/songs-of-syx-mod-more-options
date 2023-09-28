package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.game.api.GameApis;
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
    private final GameApis gameApis;

    @Getter(lazy = true)
    private final static ConfigStore instance = new ConfigStore(
        ConfigJsonService.getInstance(),
        GameApis.getInstance()
    );

    private MoreOptionsConfig currentConfig;

    /**
     * This thing is a little flaky!
     * Because it relies on reading game data to build the configs, it is only usable after a certain phase.
     * When called too early, some game classes might not be available yet
     * and the method could fail or deliver en empty result.
     */
    public MoreOptionsConfig getDefault() {
       return MoreOptionsConfig.builder()
            .eventsWorld(gameApis.eventsApi().getWorldEvents().keySet().stream()
                .collect(Collectors.toMap(key -> key, o -> true)))
            .eventsSettlement(gameApis.eventsApi().getSettlementEvents().keySet().stream()
                .collect(Collectors.toMap(key -> key, o -> true)))
           .eventsChance(gameApis.eventsApi().getEventsChance().keySet().stream()
               .collect(Collectors.toMap(key -> key, o -> 100)))
            .soundsAmbience(gameApis.soundsApi().getAmbienceSounds().keySet().stream()
                .collect(Collectors.toMap(key -> key, o -> 100)))
            .soundsSettlement(gameApis.soundsApi().getSettlementSounds().keySet().stream()
                .collect(Collectors.toMap(key -> key, o -> 100)))
            .soundsRoom(gameApis.soundsApi().getRoomSounds().keySet().stream()
               .collect(Collectors.toMap(key -> key, o -> 100)))
            .weather(gameApis.weatherApi().getWeatherThings().keySet().stream()
                .collect(Collectors.toMap(key -> key, o -> 100)))
           .boosters(gameApis.boosterApi().getBoosters().keySet().stream()
               .collect(Collectors.toMap(key -> key, o -> 100)))
            .build();
    }

    /**
     * Used by the mod as current configuration to apply and use
     */
    public void setCurrentConfig(MoreOptionsConfig currentConfig) {
        log.trace("Set %s.currentConfig to %s", ConfigStore.class.getSimpleName(), currentConfig);
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
     * @return configuration loaded from file with merged defaults
     */
    public Optional<MoreOptionsConfig> loadConfig(MoreOptionsConfig defaultConfig) {
        return configJsonService.loadConfig(defaultConfig);
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
