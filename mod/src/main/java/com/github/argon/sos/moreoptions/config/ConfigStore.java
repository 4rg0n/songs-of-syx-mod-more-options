package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.mod.sdk.file.FileMeta;
import com.github.argon.sos.mod.sdk.game.action.Initializable;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.phase.Phase;
import com.github.argon.sos.mod.sdk.phase.Phases;
import com.github.argon.sos.mod.sdk.phase.UninitializedException;
import com.github.argon.sos.moreoptions.ModModule;
import com.github.argon.sos.moreoptions.config.domain.ConfigMeta;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV5Config;
import com.github.argon.sos.moreoptions.config.domain.RacesConfig;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Handles loading and saving of {@link MoreOptionsV5Config}
 */
@RequiredArgsConstructor
public class ConfigStore implements Phases, Initializable<Void> {
    private final static Logger log = Loggers.getLogger(ConfigStore.class);

    private final ConfigService configService;
    private final ConfigDefaults configDefaults;

    @Nullable
    private MoreOptionsV5Config currentConfig;
    @Nullable
    private MoreOptionsV5Config defaultConfig;
    private ConfigMeta configMeta;

    /**
     * @return cached config meta information, loading it from disk on first access
     */
    public ConfigMeta getConfigMeta() {
        if (configMeta == null) {
            ConfigMeta configMetaDefault = configDefaults.newConfigMeta();
            try {
                // load config meta information into store
                configMeta = configService.getMeta()
                    .orElse(configMetaDefault);
            } catch (Exception e) {
                configMeta = configMetaDefault;
                log.warn("Could not read config meta information. Using defaults.", e);
                log.trace("Defaults: %s", configMetaDefault);
            }
        }

        return configMeta;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initSettlementUiPresent() {
        try {
            init();
        } catch (ConfigException e) {
            ModModule.messages().errorDialog(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onGameLoaded(Path saveFilePath, FileGetter fileGetter) {
        configService.reloadBoundToSave();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onGameSaved(Path saveFilePath, FilePutter filePutter) {
        MoreOptionsV5Config currentConfig = getCurrentConfig();
        if (currentConfig != null) {
            save(currentConfig);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCrash(Throwable e) {
        if (!configService.saveBackups()) {
            log.warn("Could not create backup config for game crash");
        }
    }

    /**
     * @return whether the backup originals were deleted successfully
     */
    public boolean deleteBackupOriginals() {
        return configService.deleteBackupOriginals(true);
    }

    /**
     * Loads configs from files and caches it
     * Used once when a game starts
     */
    @Override
    public void init() {
        MoreOptionsV5Config defaultConfig = configDefaults.newConfig();
        setDefaultConfig(defaultConfig);
        MoreOptionsV5Config config;
        try {
            config = configService.getConfig()
                .map(loadedConfig -> {
                    ConfigMerger.merge(loadedConfig, defaultConfig);
                    return loadedConfig;
                })
                .orElse(defaultConfig);
            setCurrentConfig(config);
        } catch (Exception e) {
            setCurrentConfig(defaultConfig);
            throw new ConfigException("Could not initialize config loaded from file system. Using default settings instead.", e);
        }
    }

    /**
     * Persists the given config to disk and sets it as the current config on success.
     *
     * @param config config to save; a no-op returning {@code false} when {@code null}
     * @return whether the config was saved successfully
     */
    public boolean save(@Nullable MoreOptionsV5Config config) {
        if (config == null) {
            return false;
        }

        log.debug("Saving %s", config.getClass().getSimpleName());
        if (configService.save(config)) {
            setCurrentConfig(config);
            return true;
        }

        return false;
    }

    /**
     * @return the backed up config, if one exists
     */
    public Optional<MoreOptionsV5Config> getBackup() {
        return configService.getBackup();
    }

    /**
     * Clears stored config files and resets the current config to defaults.
     */
    public void clear() {
        configService.clear();
        setCurrentConfig(getDefaultConfig());
    }

    /**
     * Used by the mod as current configuration to apply and use
     */
    public void setCurrentConfig(MoreOptionsV5Config currentConfig) {
        log.debug("Setting Current Config");
        log.trace("Config: %s", currentConfig);
        this.currentConfig = currentConfig;
    }

    /**
     * @return the currently active config, or {@code null} if none has been set yet
     */
    @Nullable
    public MoreOptionsV5Config getCurrentConfig() {
        return currentConfig;
    }

    /**
     * Used as fallback for options which are not present in the current config.
     */
    public void setDefaultConfig(MoreOptionsV5Config defaultConfig) {
        log.debug("Setting Default Config");
        log.trace("Config: %s", defaultConfig);
        this.defaultConfig = defaultConfig;
    }

    /**
     * Accessing defaults before the "INIT_GAME_UPDATING" phase can cause problems.
     */
    public MoreOptionsV5Config getDefaultConfig() {
        return Optional.ofNullable(defaultConfig)
            .orElseThrow(() -> new UninitializedException(Phase.INIT_SETTLEMENT_UI_PRESENT));
    }

    /**
     * @return file meta information about all available races config files
     */
    public List<FileMeta> readRacesConfigMetas() {
        return configService.readRacesConfigMetas();
    }

    /**
     * @return path to the races config file, if present
     */
    public Optional<Path> getRacesConfigPath() {
        return configService.getRacesConfigPath();
    }

    /**
     * Reloads all config files from disk.
     *
     * @return the reloaded config, if reloading succeeded
     */
    public Optional<MoreOptionsV5Config> reloadConfig() {
        return configService.reloadAll();
    }

    /**
     * Loads a races config from the given file path.
     *
     * @param path path to the races config file
     * @return the loaded races config, if present and readable
     */
    public Optional<RacesConfig> loadRacesConfig(Path path) {
        return configService.loadRacesConfig(path);
    }

    /**
     * @return whether the backups were deleted successfully
     */
    public boolean deleteBackups() {
        return configService.deleteBackups(true);
    }
}
