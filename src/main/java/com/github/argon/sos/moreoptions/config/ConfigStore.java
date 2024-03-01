package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.Dictionary;
import com.github.argon.sos.moreoptions.game.api.UninitializedException;
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
    private final static Logger log = Loggers.getLogger(ConfigStore.class);

    @Getter(lazy = true)
    private final static ConfigStore instance = new ConfigStore(
        ConfigService.getInstance(),
        MoreOptionsDefaults.getInstance(),
        Dictionary.getInstance()
    );

    private final static PATH SAVE_PATH = PATHS.local().SETTINGS;

    private final ConfigService configService;

    @Getter
    private final MoreOptionsDefaults defaults;

    @Getter
    private final Dictionary dictionary;

    private MoreOptionsConfig currentConfig;

    private MoreOptionsConfig.Meta metaInfo;
    private MoreOptionsConfig backupConfig;

    @Override
    public void initCreateInstance() {
        MoreOptionsConfig defaultConfig = getDefaultConfig();
        // load config from file if present; merge missing fields with defaults
        MoreOptionsConfig config = loadConfig(defaultConfig).orElse(defaultConfig);
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
    public void initBeforeGameCreated() {
        MoreOptionsConfig.Meta meta = loadMeta()
            .orElse(MoreOptionsConfig.Meta.builder().build());
        setMetaInfo(meta);

        // load backup config from file if present
        loadBackupConfig().ifPresent(this::setBackupConfig);
        // load dictionary entries from file if present
        loadDictionary().ifPresent(dictionary::addAll);
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

    public MoreOptionsConfig getCurrentConfig() {
        return Optional.ofNullable(currentConfig)
            .orElseThrow(() -> new UninitializedException("ConfigStore wasn't correctly initialized"));
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

    public boolean createBackupConfig() {
        return createBackupConfig(getCurrentConfig());
    }

    public boolean createBackupConfig(MoreOptionsConfig config) {
        log.debug("Creating backup config file: %s", backupConfigPath());
        return configService.saveConfig(SAVE_PATH, MoreOptionsConfig.FILE_NAME_BACKUP, config);
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

    /**
     * Accessing defaults before the "game instance created" phase can cause problems.
     */
    public MoreOptionsConfig getDefaultConfig() {
        return defaults.getDefaults();
    }
}
