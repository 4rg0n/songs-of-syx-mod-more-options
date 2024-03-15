package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.MoreOptionsScript;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.phase.Phase;
import com.github.argon.sos.moreoptions.phase.Phases;
import com.github.argon.sos.moreoptions.phase.UninitializedException;
import com.github.argon.sos.moreoptions.util.Lists;
import init.paths.PATH;
import init.paths.PATHS;
import lombok.*;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Handles loading and saving of {@link MoreOptionsV3Config}
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigStore implements Phases {
    private final static Logger log = Loggers.getLogger(ConfigStore.class);

    @Getter(lazy = true)
    private final static ConfigStore instance = new ConfigStore(
        ConfigService.getInstance(),
        ConfigDefaults.getInstance(),
        GameApis.getInstance()
    );

    /**
     * Name of the config file
     */
    public final static String RACES_CONFIG_FILE_PREFIX = ".RacesConfig";
    public final static String MORE_OPTIONS_FILE_NAME = "MoreOptions";
    public final static String MORE_OPTIONS_FILE_NAME_BACKUP = MORE_OPTIONS_FILE_NAME + ".backup";
    public final static PATH MORE_OPTIONS_CONFIG_PATH = PATHS.local().SETTINGS;
    public final static Path RACES_CONFIG_PATH = PATHS.local().PROFILE.get()
        .resolve(MoreOptionsScript.MOD_INFO.name + "/races");

    private final ConfigService configService;
    private final ConfigDefaults configDefaults;
    private final GameApis gameApis;

    @Nullable
    private MoreOptionsV3Config.Meta metaInfo;
    @Nullable
    private MoreOptionsV3Config currentConfig;
    @Nullable
    private MoreOptionsV3Config backupConfig;
    @Nullable
    private MoreOptionsV3Config defaultConfig;

    @Nullable
    private MoreOptionsV3Config loadedConfig;

    @Nullable
    private MoreOptionsV3Config.RacesConfig loadedRacesConfig;

    @Override
    public void initBeforeGameCreated() {
        if (!Files.isDirectory(RACES_CONFIG_PATH)) {
            try {
                log.debug("Create race configs folder at %s", RACES_CONFIG_PATH);
                Files.createDirectories(RACES_CONFIG_PATH);
            } catch (Exception e) {
                log.error("Could not create races config folder at %s", RACES_CONFIG_PATH, e);
            }
        }

        MoreOptionsV3Config.Meta meta = loadMeta()
            .orElse(MoreOptionsV3Config.Meta.builder().build());
        setMetaInfo(meta);

        // load config from file if present
        MoreOptionsV3Config config = loadConfig().orElse(null);
        if (currentConfig == null && config != null) {
            setCurrentConfig(config);
            loadedConfig = config;
        }

        // load backup config from file if present
        loadBackupConfig().ifPresent(this::setBackupConfig);
    }

    @Override
    public void initGameUpdating() {
        // initialize defaults
        MoreOptionsV3Config defaultConfig = configDefaults.newConfig();
        setDefaultConfig(defaultConfig);

        // merge or set defaults?
        if (currentConfig != null) {
            ConfigMerger.merge(currentConfig, defaultConfig);
        } else {
            setCurrentConfig(defaultConfig);
        }

        if (loadedRacesConfig != null) {
            currentConfig.setRaces(loadedRacesConfig);
        }

        // detect version change in config
        getMetaInfo().ifPresent(meta -> {
            log.trace("Detecting config version with meta: %s", meta);

            int configVersion = currentConfig.getVersion();
            int metaVersion = meta.getVersion();
            // do we have a version increase?
            if (configVersion > metaVersion) {
                log.info("Detected config version increase from %s to %s." +
                    "Saving version %s", metaVersion, configVersion, configVersion);
                saveConfig(currentConfig);
            } else if (configVersion < metaVersion) {
                log.warn("Detected config version decrease from %s to %s. This shouldn't happen...", metaVersion, configVersion);
            }
        });
    }

    @Override
    public void onGameSaved(Path saveFilePath) {
        // each save gets it race likings config associated
        MoreOptionsV3Config currentConfig = getCurrentConfig();

        if (currentConfig != null) {
            configService.saveConfig(racesConfigPath(), currentConfig.getRaces());
            saveConfig(currentConfig);
        }
    }

    @Override
    public void onGameSaveLoaded(Path saveFilePath) {
        Path path = racesConfigPath();

        if (path == null) {
            return;
        }

        // load races config additionally
        configService.loadRacesConfig(path)
            .ifPresent(racesConfig -> loadedRacesConfig = racesConfig);
    }

    @Override
    public void onCrash(Throwable e) {
        if (!createBackupConfig()) {
            log.warn("Could not create backup config for game crash");
        }
    }

    /**
     * Used by the mod as current configuration to apply and use
     */
    public void setCurrentConfig(MoreOptionsV3Config currentConfig) {
        log.debug("Setting Current Config");
        log.trace("Config: %s", currentConfig);
        this.currentConfig = currentConfig;
    }

    public void setMetaInfo(MoreOptionsV3Config.Meta metaInfo) {
        log.debug("Setting Meta Info");
        log.trace("Info: %s", metaInfo);
        this.metaInfo = metaInfo;
    }

    public void setBackupConfig(MoreOptionsV3Config backupConfig) {
        log.debug("Setting Backup Config");
        log.trace("Config: %s", backupConfig);
        this.backupConfig = backupConfig;
    }

    @Nullable
    public MoreOptionsV3Config getCurrentConfig() {
        return currentConfig;
    }

    public void setDefaultConfig(MoreOptionsV3Config defaultConfig) {
        log.debug("Setting Default Config");
        log.trace("Config: %s", defaultConfig);
        this.defaultConfig = defaultConfig;
    }

    /**
     * Accessing defaults before the "INIT_GAME_UPDATING" phase can cause problems.
     */
    public MoreOptionsV3Config getDefaultConfig() {
        return Optional.ofNullable(defaultConfig)
            .orElseThrow(() -> new UninitializedException(Phase.INIT_GAME_UPDATING));
    }

    public Optional<MoreOptionsV3Config.Meta> getMetaInfo() {
        return Optional.ofNullable(metaInfo);
    }

    public Optional<MoreOptionsV3Config> getBackupConfig() {
        return Optional.ofNullable(backupConfig);
    }

    public Optional<MoreOptionsV3Config.Meta> loadMeta() {
        return configService.loadMeta(MORE_OPTIONS_CONFIG_PATH, MORE_OPTIONS_FILE_NAME);
    }

    public boolean deleteConfig() {
        return configService.delete(MORE_OPTIONS_CONFIG_PATH, MORE_OPTIONS_FILE_NAME);
    }

    public Optional<MoreOptionsV3Config> loadBackupConfig() {
        return loadConfig(MORE_OPTIONS_FILE_NAME_BACKUP);
    }

    /**
     * @return configuration loaded from file with merged defaults
     */
    public Optional<MoreOptionsV3Config> loadConfig() {
        return loadConfig(MORE_OPTIONS_FILE_NAME);
    }

    public Optional<MoreOptionsV3Config.RacesConfig> loadRaceConfig(Path path) {
        return configService.loadRacesConfig(path);
    }

    private Optional<MoreOptionsV3Config> loadConfig(String fileName) {
        return configService.loadConfig(MORE_OPTIONS_CONFIG_PATH, fileName)
            .map(config -> {
                ConfigMerger.merge(config, defaultConfig);
                return config;
            });
    }

    /**
     * For getting some basic information from the race config files without parsing them
     */
    public List<RaceConfigMeta> loadRacesConfigMetas() {

        try (Stream<Path> stream = Files.list(RACES_CONFIG_PATH)) {
            List<RaceConfigMeta> metas = stream
                .filter(path -> path.getFileName().toString().endsWith(".txt"))
                .map(this::loadRaceConfigMeta)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

            log.debug("Loaded race configs meta from %s files in folder %s", metas.size(), RACES_CONFIG_PATH);
            return metas;
        } catch (IOException e) {
            log.error("Could not load race configs from files in folder %s", RACES_CONFIG_PATH, e);
            return Lists.of();
        }
    }

    @Nullable
    public RaceConfigMeta loadRaceConfigMeta(Path path) {
        log.trace("Loading race configs meta from file %s", path);
        try {
            BasicFileAttributes fileAttributes = Files.getFileAttributeView(path, BasicFileAttributeView.class).readAttributes();
            return RaceConfigMeta.builder()
                .fromFileAttributes(path, fileAttributes)
                .build();
        } catch (Exception e) {
            log.error("Could not load race config meta from file %s", path, e);
        }

        return null;
    }

    public boolean deleteBackupConfig() {
        return configService.delete(MORE_OPTIONS_CONFIG_PATH, MORE_OPTIONS_FILE_NAME_BACKUP);
    }

    public boolean createBackupConfig() {
        return createBackupConfig(getCurrentConfig());
    }

    public boolean createBackupConfig(@Nullable MoreOptionsV3Config config) {
        if (config == null) {
            return true;
        }

        log.debug("Creating backup config file: %s", backupConfigPath());
        if (configService.saveConfig(MORE_OPTIONS_CONFIG_PATH, MORE_OPTIONS_FILE_NAME_BACKUP, config)) {
            log.info("Backup config file successfully created at: %s",
                ConfigStore.backupConfigPath());

            // only delete original when backup config was created successfully
            if (deleteConfig()) {
                log.info("Deleted possible faulty config file at: %s",
                    ConfigStore.configPath());

                return true;
            }
        }

        return false;
    }



    /**
     * Saves config into file
     *
     * @return whether saving was successful
     */
    public boolean saveConfig(MoreOptionsV3Config config) {
       return (configService.saveConfig(MORE_OPTIONS_CONFIG_PATH, MORE_OPTIONS_FILE_NAME, config)
           && configService.saveConfig(racesConfigPath(), config.getRaces()));
    }

    public static Path configPath() {
        return MORE_OPTIONS_CONFIG_PATH.get()
            .resolve(MORE_OPTIONS_FILE_NAME + ".txt");
    }

    public static Path backupConfigPath() {
        return MORE_OPTIONS_CONFIG_PATH.get()
            .resolve(MORE_OPTIONS_FILE_NAME_BACKUP + ".txt");
    }

    public Path racesConfigPath() {
        String saveStamp = gameApis.save().getSaveStamp();
        return RACES_CONFIG_PATH.resolve(saveStamp + RACES_CONFIG_FILE_PREFIX + ".txt");
    }

    @Data
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RaceConfigMeta {
        private Path configPath;
        private Instant creationTime;
        private Instant updateTime;

        public static class RaceConfigMetaBuilder {
            public RaceConfigMetaBuilder fromFileAttributes(Path path, BasicFileAttributes fileAttributes) {
                return RaceConfigMeta.builder()
                    .configPath(path)
                    .updateTime(fileAttributes.lastModifiedTime().toInstant())
                    .creationTime(fileAttributes.creationTime().toInstant());
            }
        }
    }
}
