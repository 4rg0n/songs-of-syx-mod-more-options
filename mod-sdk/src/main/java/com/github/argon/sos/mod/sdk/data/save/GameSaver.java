package com.github.argon.sos.mod.sdk.data.save;

import com.github.argon.sos.mod.sdk.json.store.JsonStoreManager;
import com.github.argon.sos.mod.sdk.json.store.JsonStoreDefinition;
import com.github.argon.sos.mod.sdk.phase.Phases;
import com.github.argon.sos.mod.sdk.util.ClassUtil;
import com.github.argon.sos.mod.sdk.util.ReflectionUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Saves data in relation to the game save data.
 * It will not write into the game save file itself, but instead create a new JSON files.
 */
@RequiredArgsConstructor
public class GameSaver implements Phases {

    @Getter
    private final Path saveFolder;

    private final JsonStoreManager store;

    /**
     * Will write a JSON file into {@link this#saveFolder}.
     * The name of the file will be the class name or the name set in a
     * {@link SaveData} annotation at the save data class.
     *
     * @param saveData to save
     * @return path of the written file; empty when no save was written
     */
    public Optional<Path> save(Object saveData) {
        Class<?> dataClass = saveData.getClass();
        if (!store.isBound(dataClass)) {
            bind(dataClass);
        }

        return Optional.ofNullable(store.save(saveData));
    }

    public <T> Optional<Path> backup(Class<T> dataClass) {
        if (!store.isBound(dataClass)) {
            bind(dataClass);
        }

        return Optional.ofNullable(store.backup(dataClass));
    }

    /**
     * Will read save data from a file into a given class structure.
     *
     * @param saveDataClass defining the save file structure
     * @return the read data object; empty when data couldn't be read
     * @param <T> of save data class
     */
    public <T> Optional<T> load(Class<T> saveDataClass) {
        if (!store.isBound(saveDataClass)) {
            bind(saveDataClass);
        }

        return store.get(saveDataClass);
    }

    public <T> Optional<T> loadBackup(Class<T> saveDataClass) {
        if (!store.isBound(saveDataClass)) {
            bind(saveDataClass);
        }

        return store.getBackup(saveDataClass);
    }

    private void bind(Class<?> dataClass) {
        String name = ReflectionUtil.getAnnotation(dataClass, SaveData.class)
            .map(SaveData::value)
            .orElse(ClassUtil.getInnerName(dataClass));

        JsonStoreDefinition definition = JsonStoreDefinition.builder()
            .clazz(dataClass)
            .name(name)
            .path(saveFolder)
            .build();

        store.bind(definition);
    }

    public void delete(Class<?> dataClass) {
        store.deleteFile(dataClass);
        store.deleteBackupFile(dataClass);
    }

    @Override
    public void onGameLoaded(Path saveFilePath) {
        store.reload();
    }


}
