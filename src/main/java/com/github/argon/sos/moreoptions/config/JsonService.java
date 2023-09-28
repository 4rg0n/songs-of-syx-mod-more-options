package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import init.paths.PATH;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import snake2d.Errors;
import snake2d.util.file.Json;
import snake2d.util.file.JsonE;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;


@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonService {

    private final static Logger log = Loggers.getLogger(JsonService.class);
    @Getter(lazy = true)
    private final static JsonService instance = new JsonService();

    public Optional<Json> loadJson(PATH path, String fileName) {
        log.debug("Loading json file %s from %s", fileName, path.get());
        if (!path.exists(fileName)) {
            // do not load what's not there
            log.debug("File %s" + File.separator + "%s.txt not present", path.get(), fileName);
            return Optional.empty();
        }

        Path loadPath = path.get(fileName);
        return loadJson(loadPath);
    }

    public Optional<Json> loadJson(Path path) {
        try {
            return Optional.of(new Json(path));
        }  catch (Exception e) {
            log.info("Could not load json file from %s", path.toString(), e);
            return Optional.empty();
        }
    }

    public boolean saveJson(JsonE json, PATH savePath, String fileName) {
        try {
            // file exists?
            Path path;
            if (!savePath.exists(fileName)) {
                path = savePath.create(fileName);
                log.debug("Created new json file %s", path);
            } else {
                path = savePath.get(fileName);
            }

            boolean success = json.save(path);
            log.debug("Saving to %s was successful? %s", path, success);

            return success;
        } catch (Errors.DataError e) {
            log.warn("Could not save json file %s into %s", fileName, savePath.get(), e);
        } catch (Exception e) {
            log.error("Could not save json file %s into %s", fileName, savePath.get(), e);
        }

        return false;
    }

}
