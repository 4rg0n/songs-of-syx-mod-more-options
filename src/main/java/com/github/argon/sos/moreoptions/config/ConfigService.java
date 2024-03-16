package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV3Config;
import com.github.argon.sos.moreoptions.config.domain.RacesConfig;
import com.github.argon.sos.moreoptions.log.Level;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import init.paths.PATH;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import snake2d.util.file.JsonE;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

/**
 * For saving and loading {@link MoreOptionsV3Config} as json
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigService {
    private final static Logger log = Loggers.getLogger(ConfigService.class);

    @Getter(lazy = true)
    private final static ConfigService instance = new ConfigService(
        JsonService.getInstance()
    );

    private final JsonService jsonService;

    public boolean delete(PATH path, String fileName) {
        if (!path.exists(fileName)) {
            return true;
        }

        try {
            if (log.isLevel(Level.DEBUG)) log.debug("Deleting file: %s", path.get(fileName));
            path.delete(fileName);
        } catch (Exception e) {
            if (log.isLevel(Level.WARN)) log.warn("Could not delete file %s", path.get(fileName));
            return false;
        }

        return true;
    }

    public Optional<MoreOptionsV3Config.Meta> loadMeta(PATH path, String fileName) {
        return jsonService.loadJson(path, fileName)
            .map(JsonConfigMapper::mapMeta);
    }

    public boolean saveConfig(PATH path, String fileName, MoreOptionsV3Config config) {
        log.debug("Saving more options config v%s", config.getVersion());
        log.trace("CONFIG: %s", config);

        JsonE configJson = JsonConfigMapper.mapConfig(config);

        return jsonService.saveJson(configJson, path, fileName);
    }

    public boolean saveConfig(Path path, RacesConfig config) {
        log.trace("CONFIG: %s", config);

        JsonE configJson = JsonConfigMapper.mapRacesConfig(config);

        return jsonService.saveJson(configJson, path);
    }

    public Optional<RacesConfig> loadRacesConfig(Path path) {
        try {
            return jsonService.loadJson(path)
                .map(JsonConfigMapper::mapRacesConfig);
        } catch (Exception e) {
            log.error("Could load config: %s", path, e);
            return Optional.empty();
        }
    }

    public Optional<MoreOptionsV3Config> loadConfig(PATH path, String fileName) {
        if (!path.exists(fileName)) {
            // do not load what's not there
            log.debug("File %s" + File.separator + "%s.txt not present", path.get(), fileName);
            return Optional.empty();
        }

        Path filePath = path.get(fileName);

        try {
            return jsonService.loadJson(filePath).map(json -> {
                MoreOptionsV3Config.Meta meta = JsonConfigMapper.mapMeta(json);
                int version = meta.getVersion();
                log.debug("Loaded config v%s", version);

                MoreOptionsV3Config config;
                switch (version) {
                    case 1:
                        config = JsonConfigMapper.mapV1(json);
                        break;
                    case 2:
                        config = JsonConfigMapper.mapV2(json);
                        break;
                    default:
                        log.warn("Unsupported config version v%s found in %s", version, filePath);
                        return null;
                }

                return config;
            });
        } catch (Exception e) {
            log.error("Could load config: %s", filePath, e);
            return Optional.empty();
        }
    }
}
