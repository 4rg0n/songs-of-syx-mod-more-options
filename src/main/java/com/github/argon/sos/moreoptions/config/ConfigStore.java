package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.Dictionary;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import init.paths.PATHS;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigStore {
    private final static Logger log = Loggers.getLogger(ConfigStore.class);

    private final ConfigService configService;
    private final GameApis gameApis;

    @Getter(lazy = true)
    private final static ConfigStore instance = new ConfigStore(
        ConfigService.getInstance(),
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
        return configService.loadConfig(PATHS.local().SETTINGS, MoreOptionsConfig.FILE_NAME);
    }

    /**
     * @return configuration loaded from file with merged defaults
     */
    public Optional<MoreOptionsConfig> loadConfig(MoreOptionsConfig defaultConfig) {
        return configService.loadConfig(PATHS.local().SETTINGS, MoreOptionsConfig.FILE_NAME, defaultConfig);
    }

    /**
     * Saves config into file
     *
     * @return whether saving was successful
     */
    public boolean saveConfig(MoreOptionsConfig config) {
       return configService.saveConfig(PATHS.local().SETTINGS, MoreOptionsConfig.FILE_NAME, config);
    }

    public Optional<Map<String, Dictionary.Entry>> loadDictionary() {
        return configService.loadDictionary(PATHS.INIT().getFolder("config"), Dictionary.FILE_NAME);
    }

    public boolean saveDictionary(Map<String, Dictionary.Entry> entries) {
        return configService.saveDictionary(entries, PATHS.INIT().getFolder("config"), Dictionary.FILE_NAME);
    }

    public MoreOptionsConfig mergeMissing(MoreOptionsConfig target, MoreOptionsConfig source) {
        return configService.mergeMissing(target, source);
    }
}
