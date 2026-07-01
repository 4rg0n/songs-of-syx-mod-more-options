package com.github.argon.sos.mod.sdk.json;

import java.nio.file.Path;
import java.util.Optional;

/**
 * For services dealing with json content.
 */
public interface JsonService {
    /**
     * Reads the json content of the given path and parses it into the given class pojo.
     *
     * @param filePath to read json from
     * @param clazz to parse json into
     * @return parsed object with read json values
     * @param <T> type of object to parse json values into
     */
    <T> Optional<T> read(Path filePath, Class<T> clazz);

    /**
     * Writes given object as json into given file path.
     *
     * @param filePath to write into
     * @param object with values for printing as json
     */
    void write(Path filePath, Object object);

    /**
     * Deletes a file with given path.
     *
     * @param filePath to delete
     * @return whether deletion was successful
     */
    boolean delete(Path filePath);
}
