package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.Dictionary;
import com.github.argon.sos.moreoptions.MoreOptionsScript;
import com.github.argon.sos.moreoptions.game.api.GameSaveApi;
import com.github.argon.sos.moreoptions.init.InitPhases;
import com.github.argon.sos.moreoptions.init.UninitializedException;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import init.paths.PATH;
import init.paths.PATHS;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigStore implements InitPhases {
    private final static Logger log = Loggers.getLogger(ConfigStore.class);

    @Getter(lazy = true)
    private final static ConfigStore instance = new ConfigStore(
        ConfigService.getInstance(),
        Dictionary.getInstance(),
        ConfigDefaults.getInstance(),
        GameSaveApi.getInstance()
    );

    /**
     * Name of the config file
     */
    public final static String RACES_CONFIG_FILE_PREFIX = ".RacesConfig";
    public final static String MORE_OPTIONS_FILE_NAME = "MoreOptions";
    public final static String MORE_OPTIONS_FILE_NAME_BACKUP = MORE_OPTIONS_FILE_NAME + ".backup";
    private final static PATH MORE_OPTIONS_CONFIG_PATH = PATHS.local().SETTINGS;
    private final static Path RACES_CONFIG_PATH = PATHS.local().PROFILE.get()
        .resolve(MoreOptionsScript.MOD_INFO.name + "/races");

    private final ConfigService configService;
    @Getter
    private final Dictionary dictionary;
    private final ConfigDefaults configDefaults;
    private final GameSaveApi saveApi;

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
                Files.createDirectories(RACES_CONFIG_PATH);
            } catch (Exception e) {
                log.error("Could not create races config folder %s", RACES_CONFIG_PATH, e);
            }
        }

        MoreOptionsV2Config.Meta meta = loadMeta()
            .orElse(MoreOptionsV2Config.Meta.builder().build());
        setMetaInfo(meta);

        // load backup config from file if present
        loadBackupConfig().ifPresent(this::setBackupConfig);
        // load dictionary entries from file if present
        loadDictionary().ifPresent(dictionary::addAll);
    }

    @Override
    public void initCreateInstance() {
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

    private Optional<MoreOptionsV2Config> loadConfig(String fileName) {
        return configService.loadConfig(MORE_OPTIONS_CONFIG_PATH, fileName)
            .map(config -> {
                // load races config additionally
                configService.loadConfig(racesConfigPath())
                    .ifPresent(config::setRaces);

                return config;
            }).map(config -> {
                ConfigMerger.merge(config, defaultConfig);
                return config;
            });
    }

    public boolean deleteBackupConfig() {
        return configService.delete(MORE_OPTIONS_CONFIG_PATH, MORE_OPTIONS_FILE_NAME_BACKUP);
    }

    public boolean createBackupConfig() {
        return createBackupConfig(getCurrentConfig());
    }

    public boolean createBackupConfig(MoreOptionsV2Config config) {
        log.debug("Creating backup config file: %s", backupConfigPath());
        return configService.saveConfig(MORE_OPTIONS_CONFIG_PATH, MORE_OPTIONS_FILE_NAME_BACKUP, config);
    }



    /**
     * Saves config into file
     *
     * @return whether saving was successful
     */
    public boolean saveConfig(MoreOptionsV2Config config) {
       return configService.saveConfig(MORE_OPTIONS_CONFIG_PATH, MORE_OPTIONS_FILE_NAME, config)
           && configService.saveConfig(racesConfigPath(), config.getRaces());
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
        String saveStamp = saveApi.getSaveStamp();
        return RACES_CONFIG_PATH.resolve(saveStamp + RACES_CONFIG_FILE_PREFIX + ".txt");
    }

    public Optional<Map<String, Dictionary.Entry>> loadDictionary() {
        return configService.loadDictionary(PATHS.INIT().getFolder("config"), Dictionary.FILE_NAME);
    }

    public boolean saveDictionary(Map<String, Dictionary.Entry> entries) {
        return configService.saveDictionary(entries, PATHS.INIT().getFolder("config"), Dictionary.FILE_NAME);
    }
}
