package com.github.argon.sos.mod.sdk.json.store;

import com.github.argon.sos.mod.sdk.json.JsonService;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.util.Maps;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

/**
 * Holds and stores data read from JSON files.
 * Can be used to cache, read and write JSON data into files.
 */
@RequiredArgsConstructor
public class JsonStore {
    private final static Logger log = Loggers.getLogger(JsonStore.class);

    private final Map<Class<?>, JsonStoreObject<?>> store = new HashMap<>();
    private final JsonService jsonService;

    /**
     * Deletes all files currently held by the store associated by the given class.
     * Will also remove the {@link JsonStoreObject} entry for the class from the store.
     *
     * @param dataClass associated with the files to delete
     */
    public void delete(Class<?> dataClass) {
        JsonStoreObject<?> storeObject = store.get(dataClass);

        if (storeObject == null) {
            return;
        }

        HashSet<Path> paths = new HashSet<>(storeObject.entries().keySet());
        paths.forEach(path -> delete(dataClass, path));
        remove(dataClass);
    }

    /**
     * Deletes a file of a given class held in the store.
     *
     * @param dataClass associated with the file to delete
     * @param filePath to the file
     */
    public void delete(Class<?> dataClass, Path filePath) {
        JsonStoreObject<?> storeObject = store.get(dataClass);

        if (storeObject == null) {
            return;
        }

        log.debug("Deleting file %s for %s", filePath, dataClass.getSimpleName());
        jsonService.delete(filePath);
        storeObject.entries().remove(filePath);
    }

    /**
     * Removes the {@link JsonStoreObject} entry associated with the given class.
     * Will NOT delete any files.
     *
     * @param dataClass associated with the entry to remove
     */
    public void remove(Class<?> dataClass) {
        log.debug("Removing %s", dataClass.getSimpleName());
        store.remove(dataClass);
    }

    /**
     * Will return the {@link JsonStoreObject} entry associated with the given class.
     *
     * @param clazz of the entry
     * @return entry, if present
     * @param <T> of the data in the entry
     */
    public <T> Optional<JsonStoreObject<T>> get(Class<T> clazz) {
        JsonStoreObject<?> storeObject = store.get(clazz);
        if (storeObject == null) {
            return Optional.empty();
        }

        //noinspection unchecked
        return Optional.of((JsonStoreObject<T>) storeObject);
    }

    /**
     * Will read data from the given file path into the given class structure and store it.
     *
     * @param dataClass with structure of data in file
     * @param filePath to the file to read the JSON from
     * @return loaded store entry with read data in it or empty when there's no file to read from
     * @param <T> of the data in the entry
     */
    public <T> Optional<JsonStoreObject<T>> load(Class<T> dataClass, Path filePath) {
        try {
            return jsonService.load(filePath, dataClass)
                .map(data -> store(data, filePath));
        } catch (Exception e) {
            log.error("Could not load data from %s", filePath);
            throw e;
        }
    }

    /**
     * Writes and stores given data as JSON into the given file. Will replace the content or create a new file if not present.
     *
     * @param data to write and store
     * @param filePath of the file to write the JSON into
     * @return saved store entry
     * @param <T> of the data in the entry
     */
    public <T> JsonStoreObject<T> save(T data, Path filePath) {
        jsonService.save(filePath, data);
        return store(data, filePath);
    }

    /**
     * Removes all stored entries.
     * Does NOT delete any files.
     */
    public void clear() {
        store.clear();
    }

    private <T> JsonStoreObject<T> store(T data, Path filePath) {
        //noinspection unchecked
        Class<T> dataClass = (Class<T>) data.getClass();
        log.debug("Storing %s from %s", dataClass.getSimpleName(), filePath);

        JsonStoreObject<?> rawStoreObject = store.get(dataClass);
        JsonStoreObject<T> storeObject;

        if (rawStoreObject == null) {
            storeObject = JsonStoreObject.<T>builder()
                .clazz(dataClass)
                .entries(Maps.Modifiable.of(filePath, data))
                .build();
        } else {
            //noinspection unchecked
            storeObject = (JsonStoreObject<T>) rawStoreObject;
            storeObject.put(filePath, data);
        }

        store.put(dataClass, storeObject);
        return storeObject;
    }
}
