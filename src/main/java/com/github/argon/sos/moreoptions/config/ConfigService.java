package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.Dictionary;
import com.github.argon.sos.moreoptions.log.Level;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import init.paths.PATH;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.mapstruct.factory.Mappers;
import snake2d.util.file.Json;
import snake2d.util.file.JsonE;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * For saving and loading {@link MoreOptionsConfig} as json
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigService {
    private final static Logger log = Loggers.getLogger(ConfigService.class);

    @Getter(lazy = true)
    private final static ConfigService instance = new ConfigService(
        JsonService.getInstance(),
        Mappers.getMapper(ConfigMapper.class)
    );

    private final JsonService jsonService;

    private final ConfigMapper configMapper;

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

    public Optional<MoreOptionsConfig.Meta> loadMeta(PATH path, String fileName) {
        return jsonService.loadJson(path, fileName)
            .map(configMapper::mapMeta);
    }

    public Optional<MoreOptionsConfig> loadConfig(PATH path, String fileName) {
        return loadConfig(path, fileName, null);
    }

    public Optional<MoreOptionsConfig> loadConfig(String fileName, PATH path, MoreOptionsConfig defaultConfig) {
        return loadConfig(path, fileName, defaultConfig);
    }

    public boolean saveConfig(PATH path, String fileName, MoreOptionsConfig config) {
        log.debug("Saving configuration v%s into %s", config.getVersion(), path.get().toString());
        log.trace("CONFIG: %s", config);

        JsonE configJson = configMapper.mapConfig(config);

        return jsonService.saveJson(configJson, path, fileName);
    }

    public Optional<MoreOptionsConfig> loadConfig(PATH path, String fileName, @Nullable MoreOptionsConfig defaultConfig) {
        if (!path.exists(fileName)) {
            // do not load what's not there
            log.debug("File %s" + File.separator + "%s.txt not present", path.get(), fileName);
            return Optional.empty();
        }

        Path filePath = path.get(fileName);

        try {
            return jsonService.loadJson(filePath).map(json -> {
                MoreOptionsConfig.Meta meta = configMapper.mapMeta(json);
                int version = meta.getVersion();
                log.debug("Loaded config v%s", version);

                switch (version) {
                    case 1:
                        return configMapper.mapV1(filePath, json, defaultConfig);
                    case 2:
                        return configMapper.mapV2(filePath, json, defaultConfig);
                    default:
                        log.warn("Unsupported config version v%s found in %s", version, filePath);
                        return null;
                }
            });
        } catch (Exception e) {
            log.error("Could load config.", e);
            return Optional.empty();
        }
    }

    public Optional<Map<String, Dictionary.Entry>> loadDictionary(PATH path, String fileName) {
        return jsonService.loadJson(path, fileName).map(json -> {
            HashMap<String, Dictionary.Entry> dictEntries = new HashMap<>();

            json.keys().forEach(key -> {
                Json jsonDictEntry = json.json(key);

                String title = jsonDictEntry.text("TITLE", key);
                String description = jsonDictEntry.text("DESC", null);

                dictEntries.put(key, Dictionary.Entry.builder()
                    .key(key)
                    .title(title)
                    .description(description)
                    .build());
            });

            return dictEntries;
        });
    }

    public boolean saveDictionary(Map<String, Dictionary.Entry> entries, PATH path, String fileName) {
        JsonE jsonEntries = new JsonE();

        entries.forEach((key, entry) -> {
            JsonE jsonDictEntry = new JsonE();
            jsonDictEntry.addString("TITLE", entry.getTitle());

            if (entry.getDescription() != null) {
                jsonDictEntry.addString("DESC", entry.getDescription());
            }

            jsonEntries.add(key, jsonDictEntry);
        });

        return jsonService.saveJson(jsonEntries, path, fileName);
    }
}
