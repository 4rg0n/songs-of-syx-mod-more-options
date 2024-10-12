package com.github.argon.sos.mod.sdk.json.store;

import com.github.argon.sos.mod.sdk.json.store.filepath.FilePathGenerator;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Holds
 */
@RequiredArgsConstructor
public class JsonStoreManager {
    private final static Logger log = Loggers.getLogger(JsonStoreManager.class);

    private final FilePathGenerator filePathGenerator;
    private final JsonStore store;

    /**
     * Contains definitions on how to handle the data
     */
    private final Map<Class<?>, JsonStoreDefinition> definitions = new HashMap<>();

     public void bind(JsonStoreDefinition definition) {
         log.debug("Binding %s", definition.getClazz());
         definitions.put(definition.getClazz(), definition);
     }

    public boolean isBound(Class<?> configClass) {
        return definitions.containsKey(configClass);
    }

    public void unbind(Class<?> configClass) {
        log.debug("Unbinding %s", configClass);
        definitions.remove(configClass);
        store.remove(configClass);
    }

    public void clear() {
        log.debug("Clearing");
        definitions.clear();
        store.clear();
    }

    public <T> Optional<T> get(Class<T> clazz) {
        Path path = filePathGenerator.filePath(definitions.get(clazz));
        if (path == null) {
            return Optional.empty();
        }

        JsonStoreObject<T> storeObject = store.get(clazz).orElse(null);
        if (storeObject == null) {
            return Optional.empty();
        }

        T data = storeObject.get(path);
        if (data != null) {
            return Optional.of(data);
        }

        return store.load(clazz, path)
            .map(storeObject2 -> storeObject2.get(path));
    }

    public <T> Optional<T> getBackup(Class<T> clazz) {
        Path backupPath = filePathGenerator.backupPath(definitions.get(clazz));

        return store.get(clazz)
            .map(storeObject -> storeObject.get(backupPath));
    }

    public <T> void deleteAll(Class<T> clazz) {
        store.delete(clazz);
    }


    public <T> void deleteFile(Class<T> clazz) {
        JsonStoreObject<T> storeObject = store.get(clazz).orElse(null);
        if (storeObject == null) {
            return;
        }

        Path path = filePathGenerator.filePath(definitions.get(clazz));
        if (path == null) {
            return;
        }

        store.delete(clazz, path);
    }

    public <T> void deleteBackupFile(Class<T> clazz) {
        JsonStoreObject<T> storeObject = store.get(clazz).orElse(null);
        if (storeObject == null) {
            return;
        }

        Path path = filePathGenerator.backupPath(definitions.get(clazz));
        if (path == null) {
            return;
        }

        store.delete(clazz, path);
    }

    @Nullable
    public Path save(Object data) {
        log.debug("Saving %s", data.getClass().getSimpleName());
        Class<?> dataClass = data.getClass();
        JsonStoreDefinition definition = definitions.get(dataClass);

        if (definition == null) {
            log.debug("Can not save %s. No definition.", dataClass.getSimpleName());
            return null;
        }

        Path savePath = filePathGenerator.filePath(definition);
        log.debug("Can not save %s. No save path.", dataClass.getSimpleName());
        if (savePath == null) {
            return null;
        }

        // store newly saved config
        store.save(data, savePath);
        return savePath;
    }

    @Nullable
    public Path backup(Class<?> clazz) {
        log.debug("Backing up %s", clazz.getSimpleName());
        JsonStoreDefinition definition = definitions.get(clazz);

        if (definition == null) {
            log.debug("Can not backup %s. No definition.", clazz.getSimpleName());
            return null;
        }

        Path backupPath = filePathGenerator.backupPath(definition);
        if (backupPath == null) {
            log.debug("Can not backup %s. No backup path.", clazz.getSimpleName());
            return null;
        }

        JsonStoreObject<?> storeObject = store.get(clazz).orElse(null);
        if (storeObject == null) {
            log.debug("Can not backup %s. No entry with data to backup.", clazz.getSimpleName());
            return null;
        }

        Object data = storeObject.get(backupPath);
        if (data == null) {
            log.debug("Can not backup %s. No data to backup.", clazz.getSimpleName());
            return null;
        }

        store.save(data, backupPath);
        return backupPath;
    }

    public <T> Optional<T> loadBackup(Class<T> clazz) {
        JsonStoreDefinition definition = definitions.get(clazz);

        if (definition == null) {
            log.debug("Can not load backup for %s. No definition.", clazz.getName());
            return Optional.empty();
        }

        Path filePath = filePathGenerator.backupPath(definition);

        if (filePath == null) {
            log.debug("Can not load backup for %s. No backup path.", clazz.getName());
            return Optional.empty();
        }

        return store.load(clazz, filePath)
            .map(storeObject -> storeObject.get(filePath));
    }

    public <T> Optional<T> load(Class<T> clazz) {
        JsonStoreDefinition definition = definitions.get(clazz);

        if (definition == null) {
            log.debug("Can not load data for %s. No definition.", clazz.getName());
            return Optional.empty();
        }

        Path filePath = filePathGenerator.filePath(definition);

        if (filePath == null) {
            log.debug("Can not load data for %s. No file path.", clazz.getName());
            return Optional.empty();
        }

        return store.load(clazz, filePath)
            .map(storeObject -> storeObject.get(filePath));
    }

    public void reload() {
        definitions.keySet().forEach(this::load);
    }

    public void backup() {
        definitions.keySet().forEach(this::backup);
    }
}
