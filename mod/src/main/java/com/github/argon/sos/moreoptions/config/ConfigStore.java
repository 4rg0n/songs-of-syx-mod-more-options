package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.moreoptions.config.domain.ConfigMeta;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV5Config;
import com.github.argon.sos.moreoptions.config.domain.RacesConfig;
import com.github.argon.sos.mod.sdk.file.FileService;
import com.github.argon.sos.mod.sdk.phase.Phase;
import com.github.argon.sos.mod.sdk.phase.Phases;
import com.github.argon.sos.mod.sdk.phase.UninitializedException;
import com.github.argon.sos.mod.sdk.phase.state.StateManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Handles loading and saving of {@link MoreOptionsV5Config}
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigStore implements Phases {
    private final static Logger log = Loggers.getLogger(ConfigStore.class);

    @Getter(lazy = true)
    private final static ConfigStore instance = new ConfigStore(
        ConfigService.getInstance(),
        ConfigDefaults.getInstance(),
        StateManager.getInstance()
    );

    private final ConfigService configService;
    private final ConfigDefaults configDefaults;
    private final StateManager stateManager;

    @Nullable
    private MoreOptionsV5Config currentConfig;
    @Nullable
    private MoreOptionsV5Config defaultConfig;
    @Nullable
    private ConfigMeta configMeta;

    @Override
    public void initBeforeGameCreated() {
        // load configs into store
        configService.getMeta().ifPresent(meta -> configMeta = meta);
    }

    @Override
    public void initSettlementUiPresent() {
        MoreOptionsV5Config defaultConfig = configDefaults.newConfig();
        setDefaultConfig(defaultConfig);
        MoreOptionsV5Config config = configService.getConfig()
            .map(loadedConfig -> {
                ConfigMerger.merge(loadedConfig, defaultConfig);
                return loadedConfig;
            })
            .orElse(defaultConfig);

        if (currentConfig == null) {
            setCurrentConfig(config);
        }
    }

    @Override
    public void onGameSaveLoaded(Path saveFilePath) {
        configService.reloadBoundToSave();
    }

    @Override
    public void onGameSaved(Path saveFilePath) {
        MoreOptionsV5Config currentConfig = getCurrentConfig();
        if (currentConfig != null && !stateManager.getState().isNewGame()) {
            save(currentConfig);
        }
    }
    @Override
    public void onCrash(Throwable e) {
        if (!configService.saveBackups()) {
            log.warn("Could not create backup config for game crash");
        }
    }

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

    public Optional<MoreOptionsV5Config> getBackup() {
        return configService.getBackup();
    }

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

    @Nullable
    public MoreOptionsV5Config getCurrentConfig() {
        return currentConfig;
    }

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
            .orElseThrow(() -> new UninitializedException(Phase.INIT_GAME_UPDATING));
    }

    public Optional<ConfigMeta> getConfigMeta() {
        return Optional.ofNullable(configMeta);
    }

    public List<FileService.FileMeta> readRacesConfigMetas() {
        return configService.readRacesConfigMetas();
    }

    public Optional<Path> getRacesConfigPath() {
        return configService.getRacesConfigPath();
    }

    public Optional<MoreOptionsV5Config> reloadConfig() {
        return configService.reloadAll();
    }

    public Optional<RacesConfig> loadRacesConfig(Path path) {
        return configService.loadRacesConfig(path);
    }

    public boolean deleteBackups() {
        return configService.deleteBackups(true);
    }
}
