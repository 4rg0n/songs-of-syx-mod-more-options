package com.github.argon.sos.mod.sdk.json;

import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import init.paths.PATH;
import snake2d.Errors;
import snake2d.util.file.Json;
import snake2d.util.file.JsonE;

import java.nio.file.Path;
import java.util.Optional;

/**
 * For saving the data in the games JsonE format
 */
public class JsonEService {

    private final static Logger log = Loggers.getLogger(JsonEService.class);

    /** Creates a new {@link JsonEService}. */
    public JsonEService() {
    }

    /**
     * Reads and parses game json from given folder path and file name.
     *
     * @param folderPath of the file to read
     * @param fileName of the file to read
     * @return read json if present
     */
    public Optional<Json> read(PATH folderPath, String fileName) {
        Path loadPath = folderPath.get(fileName);
        return read(loadPath);
    }

    /**
     * Reads and parses game json from given file path.
     *
     * @param filePath of the file to read
     * @return read json if present
     */
    public Optional<Json> read(Path filePath) {
        log.debug("Loading json file %s", filePath);
        if (!filePath.toFile().exists()) {
            // do not read what's not there
            log.debug("File %s does not exist", filePath);
            return Optional.empty();
        }

        try {
            return Optional.of(new Json(filePath));
        }  catch (Exception e) {
            log.info("Could not load json file from %s", filePath.toString(), e);
            return Optional.empty();
        }
    }

    /**
     * Writes the {@link JsonE} content into the given folder path with file name.
     *
     * @param folderPath to write the file into
     * @param fileName of the file
     * @param json to write into file
     * @return whether the file and content was successfully written
     */
    public boolean write(PATH folderPath, String fileName, JsonE json) {
        // file exists?
        Path path;
        if (!folderPath.exists(fileName)) {
            path = folderPath.create(fileName);
            log.debug("Created new json file %s", path);
        } else {
            path = folderPath.get(fileName);
        }

        return write(path, json);
    }

    /**
     * Writes the {@link JsonE} content into the given file path.
     *
     * @param filePath to write the file into
     * @param json to write into file
     * @return whether the file and content was successfully written
     */
    public boolean write(Path filePath, JsonE json) {
        try {
            boolean success = json.save(filePath);
            log.debug("Saving to %s was successful? %s", filePath, success);

            return success;
        } catch (Errors.DataError e) {
            log.warn("Could not save json file %s into %s", filePath, e);
        } catch (Exception e) {
            log.error("Could not save json file %s into %s", filePath, e);
        }

        return false;
    }
}
