package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.config.domain.ConfigMeta;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV4Config;
import com.github.argon.sos.moreoptions.config.domain.RacesConfig;
import com.github.argon.sos.moreoptions.io.FileService;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.phase.Phase;
import com.github.argon.sos.moreoptions.phase.Phases;
import com.github.argon.sos.moreoptions.phase.UninitializedException;
import com.github.argon.sos.moreoptions.phase.state.StateManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Handles loading and saving of {@link MoreOptionsV4Config}
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
    private MoreOptionsV4Config currentConfig;
    @Nullable
    private MoreOptionsV4Config defaultConfig;
    @Nullable
    private ConfigMeta configMeta;

    @Override
    public void initBeforeGameCreated() {
        // load configs into store
        configService.getMeta().ifPresent(meta -> configMeta = meta);
    }

    @Override
    public void initSettlementUiPresent() {
        MoreOptionsV4Config defaultConfig = configDefaults.newConfig();
        setDefaultConfig(defaultConfig);
        MoreOptionsV4Config config = configService.getConfig()
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
        MoreOptionsV4Config currentConfig = getCurrentConfig();
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

    public boolean save(@Nullable MoreOptionsV4Config config) {
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

    public Optional<MoreOptionsV4Config> getBackup() {
        return configService.getBackup();
    }

    public void clear() {
        configService.clear();
        setCurrentConfig(getDefaultConfig());
    }

    /**
     * Used by the mod as current configuration to apply and use
     */
    public void setCurrentConfig(MoreOptionsV4Config currentConfig) {
        log.debug("Setting Current Config");
        log.trace("Config: %s", currentConfig);
        this.currentConfig = currentConfig;
    }

    @Nullable
    public MoreOptionsV4Config getCurrentConfig() {
        return currentConfig;
    }

    public void setDefaultConfig(MoreOptionsV4Config defaultConfig) {
        log.debug("Setting Default Config");
        log.trace("Config: %s", defaultConfig);
        this.defaultConfig = defaultConfig;
    }

    /**
     * Accessing defaults before the "INIT_GAME_UPDATING" phase can cause problems.
     */
    public MoreOptionsV4Config getDefaultConfig() {
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

    public Optional<MoreOptionsV4Config> reloadConfig() {
        return configService.reloadAll();
    }

    public Optional<RacesConfig> loadRacesConfig(Path path) {
        return configService.loadRacesConfig(path);
    }

    public boolean deleteBackups() {
        return configService.deleteBackups(true);
    }
}
