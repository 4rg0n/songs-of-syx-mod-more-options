package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.MoreOptionsScript;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.phase.Phases;
import com.github.argon.sos.moreoptions.phase.UninitializedException;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
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
 * Handles loading and saving of {@link MoreOptionsV2Config}
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
    private MoreOptionsV2Config.Meta metaInfo;
    @Nullable
    private MoreOptionsV2Config currentConfig;
    @Nullable
    private MoreOptionsV2Config backupConfig;
    @Nullable
    private MoreOptionsV2Config defaultConfig;

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

        MoreOptionsV2Config.Meta meta = loadMeta()
            .orElse(MoreOptionsV2Config.Meta.builder().build());
        setMetaInfo(meta);

        // load backup config from file if present
        loadBackupConfig().ifPresent(this::setBackupConfig);
    }

    @Override
    public void initModCreateInstance() {
        setDefaultConfig(configDefaults.newDefaultConfig());
        // load config from file if present; merge missing fields with defaults
        MoreOptionsV2Config config = loadConfig().orElse(defaultConfig);
        if (currentConfig == null) {
            setCurrentConfig(config);
        }

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
            }
        });
    }

    @Override
    public void onGameSaved(Path saveFilePath) {
        // each save gets it race likings config associated
        configService.saveConfig(racesConfigPath(), getCurrentConfig().getRaces());
    }

    @Override
    public void onGameSaveLoaded(Path saveFilePath) {
        Path path = racesConfigPath();

        if (path == null) {
            return;
        }

        // todo need to apply to ui?
        // load races config additionally
        configService.loadConfig(path)
            .ifPresent(getCurrentConfig()::setRaces);
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
    public void setCurrentConfig(MoreOptionsV2Config currentConfig) {
        log.debug("Setting Current Config");
        log.trace("Config: %s", currentConfig);
        this.currentConfig = currentConfig;
    }

    public void setMetaInfo(MoreOptionsV2Config.Meta metaInfo) {
        log.debug("Setting Meta Info");
        log.trace("Info: %s", metaInfo);
        this.metaInfo = metaInfo;
    }

    public void setBackupConfig(MoreOptionsV2Config backupConfig) {
        log.debug("Setting Backup Config");
        log.trace("Config: %s", backupConfig);
        this.backupConfig = backupConfig;
    }

    public MoreOptionsV2Config getCurrentConfig() {
        return Optional.ofNullable(currentConfig)
            .orElseThrow(() -> new UninitializedException("ConfigStore wasn't correctly initialized"));
    }

    public void setDefaultConfig(MoreOptionsV2Config defaultConfig) {
        log.debug("Setting Default Config");
        log.trace("Config: %s", defaultConfig);
        this.defaultConfig = defaultConfig;
    }

    /**
     * Accessing defaults before the "game instance created" phase can cause problems.
     */
    public MoreOptionsV2Config getDefaultConfig() {
        return Optional.ofNullable(defaultConfig)
            .orElseThrow(() -> new UninitializedException("ConfigStore wasn't correctly initialized"));
    }

    public Optional<MoreOptionsV2Config.Meta> getMetaInfo() {
        return Optional.ofNullable(metaInfo);
    }

    public Optional<MoreOptionsV2Config> getBackupConfig() {
        return Optional.ofNullable(backupConfig);
    }

    public Optional<MoreOptionsV2Config.Meta> loadMeta() {
        return configService.loadMeta(MORE_OPTIONS_CONFIG_PATH, MORE_OPTIONS_FILE_NAME);
    }

    public boolean deleteConfig() {
        return configService.delete(MORE_OPTIONS_CONFIG_PATH, MORE_OPTIONS_FILE_NAME);
    }

    public Optional<MoreOptionsV2Config> loadBackupConfig() {
        return loadConfig(MORE_OPTIONS_FILE_NAME_BACKUP);
    }

    /**
     * @return configuration loaded from file with merged defaults
     */
    public Optional<MoreOptionsV2Config> loadConfig() {
        return loadConfig(MORE_OPTIONS_FILE_NAME);
    }

    public Optional<MoreOptionsV2Config.RacesConfig> loadRaceConfig(Path path) {
        return configService.loadConfig(path);
    }

    private Optional<MoreOptionsV2Config> loadConfig(String fileName) {
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

    public boolean createBackupConfig(MoreOptionsV2Config config) {
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
    public boolean saveConfig(MoreOptionsV2Config config) {
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
