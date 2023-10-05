package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.Dictionary;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import init.paths.PATHS;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigStore {
    private final static Logger log = Loggers.getLogger(ConfigStore.class);

    private final ConfigService configService;

    private Defaults defaultConfig;

    @Getter(lazy = true)
    private final static ConfigStore instance = new ConfigStore(
        ConfigService.getInstance()
    );

    private MoreOptionsConfig currentConfig;

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

    public Defaults getDefaults() {
        if (defaultConfig == null) {
            defaultConfig = new Defaults(GameApis.getInstance());
        }

        return defaultConfig;
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

    public Path getConfigPath() {
        return PATHS.local().SETTINGS.get()
            .resolve(MoreOptionsConfig.FILE_NAME + ".txt");
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

    @RequiredArgsConstructor
    public static class Defaults {

        private final GameApis gameApis;

        @Getter(lazy=true)
        private final Map<String, Integer> boosters = boosters();
        @Getter(lazy=true)
        private final Map<String, Integer> weather = weather();
        @Getter(lazy=true)
        private final Map<String, Integer> soundsRoom = soundsRoom();
        @Getter(lazy=true)
        private final Map<String, Integer> soundsAmbience = soundsAmbience();
        @Getter(lazy=true)
        private final Map<String, Integer> soundsSettlement = soundsSettlement();
        @Getter(lazy=true)
        private final Map<String, Integer> eventsChance = eventsChance();
        @Getter(lazy=true)
        private final Map<String, Boolean> eventsWorld = eventsWorld();
        @Getter(lazy=true)
        private final Map<String, Boolean> eventsSettlement = eventsSettlement();

        /**
         * This thing is a little flaky!
         * Because it relies on reading game data to build the configs, it is only usable after a certain phase.
         * When called too early, some game classes might not be available yet
         * and the method could fail or deliver en empty result.
         */
        public MoreOptionsConfig get() {
            return MoreOptionsConfig.builder()
                .eventsWorld(getEventsWorld())
                .eventsSettlement(getEventsSettlement())
                .eventsChance(getEventsChance())
                .soundsAmbience(getSoundsAmbience())
                .soundsSettlement(getSoundsSettlement())
                .soundsRoom(getSoundsRoom())
                .weather(getWeather())
                .boosters(getBoosters())
                .build();
        }

        private Map<String, Integer> boosters() {
            //noinspection DataFlowIssue
            return gameApis.boosterApi().getAllBoosters().keySet().stream()
                .collect(Collectors.toMap(key -> key, o -> 100));
        }

        private Map<String, Integer> weather() {
            //noinspection DataFlowIssue
            return gameApis.weatherApi().getWeatherThings().keySet().stream()
                .collect(Collectors.toMap(key -> key, o -> 100));
        }

        private Map<String, Integer> soundsRoom() {
            //noinspection DataFlowIssue
            return gameApis.soundsApi().getRoomSounds().keySet().stream()
                .collect(Collectors.toMap(key -> key, o -> 100));
        }

        private Map<String, Integer> soundsSettlement() {
            //noinspection DataFlowIssue
            return gameApis.soundsApi().getSettlementSounds().keySet().stream()
                .collect(Collectors.toMap(key -> key, o -> 100));
        }

        private Map<String, Integer> soundsAmbience() {
            //noinspection DataFlowIssue
            return gameApis.soundsApi().getAmbienceSounds().keySet().stream()
                .collect(Collectors.toMap(key -> key, o -> 100));
        }

        private Map<String, Integer> eventsChance() {
            //noinspection DataFlowIssue
            return gameApis.eventsApi().getEventsChance().keySet().stream()
                .collect(Collectors.toMap(key -> key, o -> 100));
        }

        private Map<String, Boolean> eventsSettlement() {
            //noinspection DataFlowIssue
            return gameApis.eventsApi().getSettlementEvents().keySet().stream()
                .collect(Collectors.toMap(key -> key, o -> true));
        }

        private Map<String, Boolean> eventsWorld() {
            //noinspection DataFlowIssue
            return gameApis.eventsApi().getWorldEvents().keySet().stream()
                .collect(Collectors.toMap(key -> key, o -> true));
        }
    }
}
