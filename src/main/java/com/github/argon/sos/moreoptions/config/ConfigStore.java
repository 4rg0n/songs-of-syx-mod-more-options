package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.Dictionary;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.game.api.GameEventsApi;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import init.paths.PATH;
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

    private final static PATH SAVE_PATH = PATHS.local().SETTINGS;

    private final ConfigService configService;

    private Defaults defaultConfig;

    @Getter(lazy = true)
    private final static ConfigStore instance = new ConfigStore(
        ConfigService.getInstance()
    );

    private MoreOptionsConfig currentConfig;

    private MoreOptionsConfig.Meta metaInfo;
    private MoreOptionsConfig backupConfig;

    public MoreOptionsConfig initConfig() {
        MoreOptionsConfig defaultConfig = getDefaultConfig();
        MoreOptionsConfig config = getCurrentConfig()
            .orElseGet(() -> loadConfig(defaultConfig)
                .orElseGet(() -> {
                    log.info("No config file loaded. Using default.");
                    log.trace("Default: %s", defaultConfig);
                    return defaultConfig;
                }));
        setCurrentConfig(config);

        // detect version change in config
        getMetaInfo().ifPresent(meta -> {
            log.trace("Detecting config version with meta: %s", meta);

            int configVersion = config.getVersion();
            int metaVersion = meta.getVersion();
            // do we have a version increase?
            if (configVersion > metaVersion) {
                log.info("Detected config version increase from %s to %s." +
                    "Saving version %s", metaVersion, configVersion, configVersion);
                saveConfig(config);
            } else if (configVersion < metaVersion) {
                log.warn("Detected config version decrease from %s to %s. This shouldn't happen...", metaVersion, configVersion);
                // FIXME persist newest? discard old?
            }
        });

        return config;
    }

    public MoreOptionsConfig.Meta initMetaInfo() {
        MoreOptionsConfig.Meta meta = loadMeta()
            .orElse(MoreOptionsConfig.Meta.builder().build());
        setMetaInfo(meta);

        return meta;
    }

    /**
     * Used by the mod as current configuration to apply and use
     */
    public void setCurrentConfig(MoreOptionsConfig currentConfig) {
        log.trace("Set %s.currentConfig to %s", ConfigStore.class.getSimpleName(), currentConfig);
        this.currentConfig = currentConfig;
    }

    public void setMetaInfo(MoreOptionsConfig.Meta metaInfo) {
        log.trace("Set %s.metaInfo to %s", ConfigStore.class.getSimpleName(), metaInfo);
        this.metaInfo = metaInfo;
    }

    public void setBackupConfig(MoreOptionsConfig backupConfig) {
        log.trace("Set %s.backupConfig to %s", ConfigStore.class.getSimpleName(), backupConfig);
        this.backupConfig = backupConfig;
    }

    public Optional<MoreOptionsConfig> getCurrentConfig() {
        return Optional.ofNullable(currentConfig);
    }

    public Optional<MoreOptionsConfig.Meta> getMetaInfo() {
        return Optional.ofNullable(metaInfo);
    }

    public Optional<MoreOptionsConfig> getBackupConfig() {
        return Optional.ofNullable(backupConfig);
    }

    /**
     * @return configuration loaded from file
     */
    public Optional<MoreOptionsConfig> loadConfig() {
        return configService.loadConfig(SAVE_PATH, MoreOptionsConfig.FILE_NAME, null);
    }

    public Optional<MoreOptionsConfig.Meta> loadMeta() {
        return configService.loadMeta(SAVE_PATH, MoreOptionsConfig.FILE_NAME);
    }

    public boolean deleteConfig() {
        return configService.delete(SAVE_PATH, MoreOptionsConfig.FILE_NAME);
    }

    public Optional<MoreOptionsConfig> loadBackupConfig() {
        return configService.loadConfig(SAVE_PATH, MoreOptionsConfig.FILE_NAME_BACKUP);
    }

    public boolean deleteBackupConfig() {
        return configService.delete(SAVE_PATH, MoreOptionsConfig.FILE_NAME_BACKUP);
    }

    public boolean createBackupConfig(MoreOptionsConfig config) {
        log.debug("Creating backup config file: %s", backupConfigPath());
        return configService.saveConfig(SAVE_PATH, MoreOptionsConfig.FILE_NAME_BACKUP, config);
    }

    public Defaults getDefaults() {
        if (defaultConfig == null) {
            defaultConfig = new Defaults(GameApis.getInstance());
        }

        return defaultConfig;
    }

    public MoreOptionsConfig getDefaultConfig() {
        return getDefaults().get();
    }

    /**
     * @return configuration loaded from file with merged defaults
     */
    public Optional<MoreOptionsConfig> loadConfig(MoreOptionsConfig defaultConfig) {
        return configService.loadConfig(SAVE_PATH, MoreOptionsConfig.FILE_NAME, defaultConfig);
    }

    /**
     * Saves config into file
     *
     * @return whether saving was successful
     */
    public boolean saveConfig(MoreOptionsConfig config) {
       return configService.saveConfig(SAVE_PATH, MoreOptionsConfig.FILE_NAME, config);
    }

    public static Path configPath() {
        return SAVE_PATH.get()
            .resolve(MoreOptionsConfig.FILE_NAME + ".txt");
    }

    public static Path backupConfigPath() {
        return SAVE_PATH.get()
            .resolve(MoreOptionsConfig.FILE_NAME_BACKUP + ".txt");
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
        private final Map<String, MoreOptionsConfig.Range> boosters = boosters();
        @Getter(lazy=true)
        private final Map<String, MoreOptionsConfig.Range> weather = weather();
        @Getter(lazy=true)
        private final Map<String, MoreOptionsConfig.Range> soundsRoom = soundsRoom();
        @Getter(lazy=true)
        private final Map<String, MoreOptionsConfig.Range> soundsAmbience = soundsAmbience();
        @Getter(lazy=true)
        private final Map<String, MoreOptionsConfig.Range> soundsSettlement = soundsSettlement();
        @Getter(lazy=true)
        private final Map<String, MoreOptionsConfig.Range> eventsChance = eventsChance();
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
                .filePath(ConfigStore.configPath())
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

        private Map<String, MoreOptionsConfig.Range> boosters() {
            //noinspection DataFlowIssue
            return gameApis.boosterApi().getAllBoosters().keySet().stream()
                .collect(Collectors.toMap(key -> key, o -> MoreOptionsConfig.Range.builder()
                    .value(100)
                    .min(0)
                    .max(10000)
                    .displayMode(MoreOptionsConfig.Range.DisplayMode.PERCENTAGE)
                    .build()));
        }

        private Map<String, MoreOptionsConfig.Range> weather() {
            //noinspection DataFlowIssue
            return gameApis.weatherApi().getWeatherThings().keySet().stream()
                .collect(Collectors.toMap(key -> key, o -> MoreOptionsConfig.Range.builder()
                    .value(100)
                    .min(0)
                    .max(100)
                    .displayMode(MoreOptionsConfig.Range.DisplayMode.PERCENTAGE)
                    .build()));
        }

        private Map<String, MoreOptionsConfig.Range> soundsRoom() {
            //noinspection DataFlowIssue
            return gameApis.soundsApi().getRoomSounds().keySet().stream()
                .collect(Collectors.toMap(key -> key, o -> MoreOptionsConfig.Range.builder()
                    .value(100)
                    .min(0)
                    .max(100)
                    .displayMode(MoreOptionsConfig.Range.DisplayMode.PERCENTAGE)
                    .build()));
        }

        private Map<String, MoreOptionsConfig.Range> soundsSettlement() {
            //noinspection DataFlowIssue
            return gameApis.soundsApi().getSettlementSounds().keySet().stream()
                .collect(Collectors.toMap(key -> key, o -> MoreOptionsConfig.Range.builder()
                    .value(100)
                    .min(0)
                    .max(100)
                    .displayMode(MoreOptionsConfig.Range.DisplayMode.PERCENTAGE)
                    .build()));
        }

        private Map<String, MoreOptionsConfig.Range> soundsAmbience() {
            //noinspection DataFlowIssue
            return gameApis.soundsApi().getAmbienceSounds().keySet().stream()
                .collect(Collectors.toMap(key -> key, o -> MoreOptionsConfig.Range.builder()
                    .value(100)
                    .min(0)
                    .max(100)
                    .displayMode(MoreOptionsConfig.Range.DisplayMode.PERCENTAGE)
                    .build()));
        }

        private Map<String, MoreOptionsConfig.Range> eventsChance() {
            //noinspection DataFlowIssue
            return gameApis.eventsApi().getEventsChance().keySet().stream()
                .collect(Collectors.toMap(key -> key, key -> {
                    MoreOptionsConfig.Range range = MoreOptionsConfig.Range.builder()
                        .value(100)
                        .min(0)
                        .max(100)
                        .displayMode(MoreOptionsConfig.Range.DisplayMode.PERCENTAGE)
                        .build();

                    // fixme ugly...
                    if (GameEventsApi.FACTION_OPINION_ADD.equals(key)) {
                        range.setDisplayMode(MoreOptionsConfig.Range.DisplayMode.ABSOLUTE);
                        range.setMin(-100);
                    }

                    return range;
                }));
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
