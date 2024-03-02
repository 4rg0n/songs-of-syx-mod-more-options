package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.Dictionary;
import com.github.argon.sos.moreoptions.init.UninitializedException;
import com.github.argon.sos.moreoptions.init.InitPhases;
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

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigStore implements InitPhases {
    /**
     * Name of the config file
     */
    public final static String MORE_OPTIONS_FILE_NAME = "MoreOptions";
    public final static String MORE_OPTIONS_FILE_NAME_BACKUP = MORE_OPTIONS_FILE_NAME + ".backup";
    private final static Logger log = Loggers.getLogger(ConfigStore.class);

    @Getter(lazy = true)
    private final static ConfigStore instance = new ConfigStore(
        ConfigService.getInstance(),
        Dictionary.getInstance(),
        ConfigDefaults.getInstance()
    );

    private final static PATH SAVE_PATH = PATHS.local().SETTINGS;

    private final ConfigService configService;

    @Getter
    private final Dictionary dictionary;

    private final ConfigDefaults configDefaults;

    private MoreOptionsV2Config.Meta metaInfo;
    private MoreOptionsV2Config currentConfig;
    private MoreOptionsV2Config backupConfig;
    private MoreOptionsV2Config defaultConfig;

    @Override
    public void initBeforeGameCreated() {
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
        defaultConfig = configDefaults.newDefaultConfig();
        // load config from file if present; merge missing fields with defaults
        MoreOptionsV2Config config = loadConfig(defaultConfig).orElse(defaultConfig);
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

    /**
     * @return configuration loaded from file
     */
    public Optional<MoreOptionsV2Config> loadConfig() {
        return configService.loadConfig(SAVE_PATH, MORE_OPTIONS_FILE_NAME, null);
    }

    public Optional<MoreOptionsV2Config.Meta> loadMeta() {
        return configService.loadMeta(SAVE_PATH, MORE_OPTIONS_FILE_NAME);
    }

    public boolean deleteConfig() {
        return configService.delete(SAVE_PATH, MORE_OPTIONS_FILE_NAME);
    }

    public Optional<MoreOptionsV2Config> loadBackupConfig() {
        return configService.loadConfig(SAVE_PATH, MORE_OPTIONS_FILE_NAME_BACKUP);
    }

    public boolean deleteBackupConfig() {
        return configService.delete(SAVE_PATH, MORE_OPTIONS_FILE_NAME_BACKUP);
    }

    public boolean createBackupConfig() {
        return createBackupConfig(getCurrentConfig());
    }

    public boolean createBackupConfig(MoreOptionsV2Config config) {
        log.debug("Creating backup config file: %s", backupConfigPath());
        return configService.saveConfig(SAVE_PATH, MORE_OPTIONS_FILE_NAME_BACKUP, config);
    }

    /**
     * @return configuration loaded from file with merged defaults
     */
    public Optional<MoreOptionsV2Config> loadConfig(MoreOptionsV2Config defaultConfig) {
        return configService.loadConfig(SAVE_PATH, MORE_OPTIONS_FILE_NAME, defaultConfig);
    }

    /**
     * Saves config into file
     *
     * @return whether saving was successful
     */
    public boolean saveConfig(MoreOptionsV2Config config) {
       return configService.saveConfig(SAVE_PATH, MORE_OPTIONS_FILE_NAME, config);
    }

    public static Path configPath() {
        return SAVE_PATH.get()
            .resolve(MORE_OPTIONS_FILE_NAME + ".txt");
    }

    public static Path backupConfigPath() {
        return SAVE_PATH.get()
            .resolve(MORE_OPTIONS_FILE_NAME_BACKUP + ".txt");
    }

    public Optional<Map<String, Dictionary.Entry>> loadDictionary() {
        return configService.loadDictionary(PATHS.INIT().getFolder("config"), Dictionary.FILE_NAME);
    }

    public boolean saveDictionary(Map<String, Dictionary.Entry> entries) {
        return configService.saveDictionary(entries, PATHS.INIT().getFolder("config"), Dictionary.FILE_NAME);
    }
}
