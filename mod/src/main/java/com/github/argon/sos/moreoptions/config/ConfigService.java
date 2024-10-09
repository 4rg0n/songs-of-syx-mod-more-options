package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.mod.sdk.config.ConfigVersionHandler;
import com.github.argon.sos.mod.sdk.config.ConfigVersionHandlers;
import com.github.argon.sos.mod.sdk.json.store.Binding;
import com.github.argon.sos.mod.sdk.config.json.JsonConfigStore;
import com.github.argon.sos.mod.sdk.file.FileMeta;
import com.github.argon.sos.mod.sdk.json.JsonException;
import com.github.argon.sos.mod.sdk.json.JsonGameService;
import com.github.argon.sos.mod.sdk.json.parser.JsonParseException;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.phase.Phases;
import com.github.argon.sos.moreoptions.config.domain.ConfigMeta;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV5Config;
import com.github.argon.sos.moreoptions.config.domain.RacesConfig;
import com.github.argon.sos.moreoptions.config.json.JsonMeta;
import com.github.argon.sos.moreoptions.config.json.v5.JsonBoostersV5Config;
import com.github.argon.sos.moreoptions.config.json.v5.JsonMoreOptionsV5Config;
import com.github.argon.sos.moreoptions.config.json.v5.JsonRacesV5Config;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class ConfigService implements Phases {
    private final static Logger log = Loggers.getLogger(ConfigService.class);

    private final JsonConfigStore jsonConfigStore;
    private final JsonGameService jsonGameService;

    private final ConfigVersionHandler<MoreOptionsV5Config> versionHandler;

    public ConfigService(
        JsonConfigStore jsonConfigStore,
        ConfigVersionHandlers<MoreOptionsV5Config> versionHandler,
        JsonGameService jsonGameService
    ) {
        this.jsonConfigStore = jsonConfigStore;
        this.versionHandler = versionHandler;
        this.jsonGameService = jsonGameService;
    }

    public Optional<ConfigMeta> getMeta() {
        // try reading legacy meta first and then normal meta
        return Stream.of(readMetaLegacy(), readMeta())
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst();
    }

    private Optional<ConfigMeta> readMeta() {
        return jsonConfigStore.get(JsonMeta.class)
            .map(ConfigMapper::mapMeta);
    }

    /**
     * This is there for compatibility to older config versions written with the custom JSON writer
     */
    private Optional<ConfigMeta> readMetaLegacy() {
        try {
            return jsonGameService.load(ConfigDefaults.CONFIG_FILE_PATH, JsonMeta.class)
                .map(ConfigMapper::mapMeta);
        } catch (JsonParseException | JsonException e) {
            // this is expected
            log.debug("Could not parse or read legacy meta json data: %s", e.getMessage());
            log.trace("", e);
            return Optional.empty();
        } catch (Exception e) {
            log.warn("Could not read legacy meta json", e);
            return Optional.empty();
        }
    }

    public Optional<MoreOptionsV5Config> getConfig() {
        return Stream.of(readMetaLegacy(), getMeta())
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst()
            .map(configMeta -> {
                log.info("Json Config: V%s", configMeta.getVersion());
                return configMeta;
            })
            .map(ConfigMeta::getVersion)
            .flatMap(versionHandler::handle);
    }

    public Optional<MoreOptionsV5Config> getBackup() {
        return jsonConfigStore.getBackup(JsonMoreOptionsV5Config.class)
            .map(ConfigMapper::mapConfig)
            // add other configs
            .map(domainConfig -> {
                jsonConfigStore.getBackup(JsonRacesV5Config.class)
                    .ifPresent(racesConfig -> ConfigMapper.mapInto(racesConfig, domainConfig));
                jsonConfigStore.getBackup(JsonBoostersV5Config.class)
                    .ifPresent(boostersConfig -> ConfigMapper.mapInto(boostersConfig, domainConfig));
                return domainConfig;
            });
    }

    public boolean saveBackups() {
        return jsonConfigStore.saveBackups(true);
    }

    public List<FileMeta> readRacesConfigMetas() {
        return jsonConfigStore.readMetas(JsonRacesV5Config.class);
    }

    public Optional<MoreOptionsV5Config> reloadAll() {
        jsonConfigStore.reloadAll();
        return getConfig();
    }

    public Optional<MoreOptionsV5Config> reloadBackups() {
        jsonConfigStore.reloadBackups();
        return getBackup();
    }

    public void reloadBoundToSave() {
        jsonConfigStore.reload(Binding.SAVE);
    }

    public Optional<RacesConfig> loadRacesConfig(Path path) {
        return jsonConfigStore.readFromPath(RacesConfig.class, path);
    }

    public Optional<Path> getRacesConfigPath() {
        return jsonConfigStore.getPath(JsonRacesV5Config.class);
    }

    public boolean save(MoreOptionsV5Config config) {
        return Stream.of(
            jsonConfigStore.save(ConfigMapper.mapConfig(config)),
            jsonConfigStore.save(ConfigMapper.mapRacesConfig(config)),
            jsonConfigStore.save(ConfigMapper.mapBoostersConfig(config))
        )
        // all succeeded?
        .noneMatch(Objects::isNull);
    }

    public void clear() {
        jsonConfigStore.clear();
    }

    public boolean deleteBackups(boolean removeFromStore) {
        return jsonConfigStore.deleteBackups(removeFromStore);
    }

    public boolean deleteBackupOriginals(boolean removeFromStore) {
        return jsonConfigStore.deleteBackupOriginals(removeFromStore);
    }
}
