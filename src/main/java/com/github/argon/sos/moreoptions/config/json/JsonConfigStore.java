package com.github.argon.sos.moreoptions.config.json;

import com.github.argon.sos.moreoptions.game.api.GameSaveApi;
import com.github.argon.sos.moreoptions.io.FileService;
import com.github.argon.sos.moreoptions.json.JsonService;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.Lists;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.*;

/**
 * Manages and contains config objects loaded from json files.
 */
@RequiredArgsConstructor
public class JsonConfigStore {

    private final static Logger log = Loggers.getLogger(JsonConfigStore.class);
    /**
     * Used in filenames when backed up
     */
    public final static String BACKUP_FILE_PREFIX = "backup";
    /**
     * Used as file ending for config json files
     */
    public final static String FILE_ENDING = ".txt";

    private final GameSaveApi gameSaveApi;
    private final JsonService jsonService;
    private final FileService fileService;
    @Getter
    private final int version;

    /**
     * Contains definitions on how to handle the config
     */
    private final Map<Class<?>, ConfigDefinition> configDefinitions = new HashMap<>();

    /**
     * Contains config objects loaded from json files
     */
    private final Map<Class<?>, ConfigObject<?>> configStore = new HashMap<>();

    /**
     * Contains possibly loaded backup configs
     */
    private final Map<Class<?>, ConfigObject<?>> backupConfigStore = new HashMap<>();

    /**
     * Registers a config class with its path to save in the store.
     *
     * @param configClass of the config to bing
     * @param file path for saving and loading
     * @param doBackup whether the registered file can be backed up
     */
    public void bind(Class<?> configClass, Path file, boolean doBackup) {
        configDefinitions.put(configClass, ConfigDefinition.builder()
            .path(file)
            .doBackup(doBackup)
            .configClass(configClass)
            .build());
    }

    /**
     * Registers a game save bound config class with its save folder in the store.
     * This config will get an identifier (save stamp) into its filename.
     * So it can be associated with the game save.
     *
     * Configs bound to saves will also be automatically loaded when the game loaded a save.
     *
     * @param configClass of the config to bing
     * @param namePrefix used for the filename in combination with the game stamp
     * @param folder path to store the bound configs
     * @param doBackup whether the registered file can be backed up
     */
    public void bindToSave(Class<?> configClass, String namePrefix, Path folder, boolean doBackup) {
        configDefinitions.put(configClass, ConfigDefinition.builder()
            .path(folder)
            .configClass(configClass)
            .namePrefix(namePrefix)
            .boundToSave(true)
            .doBackup(doBackup)
            .build());
    }

    /**
     * Remove a config class from the store
     *
     * @param configClass to remove
     */
    public void unbind(Class<?> configClass) {
        configStore.remove(configClass);
        backupConfigStore.remove(configClass);
        configDefinitions.remove(configClass);
    }

    /**
     * Clears all stored configs and definitions
     */
    public void clear() {
        log.debug("Clearing");
        configDefinitions.clear();
        configStore.clear();
        backupConfigStore.clear();
    }

    /**
     * @return whether there are any backup configs loaded
     */
    public boolean hasBackups() {
        return !backupConfigStore.isEmpty();
    }

    public List<FileService.FileMeta> readMetas(Class<?> configClass) {
        ConfigDefinition configDefinition = this.configDefinitions.get(configClass);

        if (configDefinition == null) {
            log.debug("Can not read file metas for class %s. No config entry present.", configClass.getSimpleName());
            return Lists.of();
        }

        return readMetas(configDefinition);
    }

    /**
     * @return file path of the currently loaded config class
     */
    public Optional<Path> getPath(Class<?> configClass) {
        return Optional.ofNullable(configStore.get(configClass))
            .map(ConfigObject::getPath);
    }

    /**
     * @return backup file path of the currently loaded config class
     */
    public Optional<Path> getBackupPath(Class<?> configClass) {
        return Optional.ofNullable(backupConfigStore.get(configClass))
            .map(ConfigObject::getPath);
    }

    /**
     * Will look into the store for a config object with given class.
     * If not present, will try to load the config from file with usage of the {@link ConfigDefinition}.
     *
     * @param configClass of the config object to get
     * @return config object if present
     * @param <T> type of the config object
     */
    public <T> Optional<T> get(Class<T> configClass) {
        T config = null;
        try {
            ConfigObject<?> configObject = backupConfigStore.get(configClass);

            if (configObject != null) {
                //noinspection unchecked
                config = (T) configObject.getConfig();
            }
        } catch (Exception e) {
            log.info("Could not get config for class %s", configClass.getName());
            return Optional.empty();
        }

        if (config != null) {
            return Optional.of(config);
        }

        return reload(configClass);
    }

    /**
     * Will look into the backup store for a config object with given class.
     * If not present, will try to load the backup config from file with usage of the {@link ConfigDefinition}.
     *
     * @param configClass of the config object to get
     * @return config object if present
     * @param <T> type of the config object
     */
    public <T> Optional<T> getBackup(Class<T> configClass) {
        T config = null;
        try {
            ConfigObject<?> configObject = backupConfigStore.get(configClass);

            if (configObject != null) {
                //noinspection unchecked
                config = (T) configObject.getConfig();
            }
        } catch (Exception e) {
            log.debug("Could not get backup config for class %s", configClass.getName());
            return Optional.empty();
        }

        if (config != null) {
            return Optional.of(config);
        }

        return reloadBackup(configClass);
    }

    /**
     * Reloads all configs from files into store
     */
    public void reloadAll() {
        Boolean success = configDefinitions.keySet().stream()
            .map(configDefinition -> reload(configDefinition).isPresent())
            .filter(aBoolean -> !aBoolean)
            .findFirst()
            .orElse(true);

        log.debug("Reloaded all configs successful? %s", success);
    }

    /**
     * Reloads configs bound not to saves from files
     */
    public void reloadNotBoundToSave() {
        Boolean success = configDefinitions.entrySet().stream()
            .filter(entry -> !entry.getValue().isBoundToSave())
            .map(entry -> reload(entry.getKey()).isPresent())
            .filter(aBoolean -> !aBoolean)
            .findFirst()
            .orElse(true);

        log.debug("Reloaded not save bound configs successful? %s", success);
    }

    /**
     * Reloads only configs bound to saves from files
     */
    public void reloadBoundToSave() {
        Boolean success = configDefinitions.entrySet().stream()
            .filter(entry -> entry.getValue().isBoundToSave())
            .map(entry -> reload(entry.getKey()).isPresent())
            .filter(aBoolean -> !aBoolean)
            .findFirst()
            .orElse(true);

        log.debug("Reloaded save bound configs successful? %s", success);
    }

    /**
     * Loads backups from registered configs into the store if present
     */
    public void reloadBackups() {
        Boolean success = configDefinitions.entrySet().stream()
            .filter(entry -> entry.getValue().isDoBackup())
            .map(entry -> reloadBackup(entry.getKey()).isPresent())
            .filter(aBoolean -> !aBoolean)
            .findFirst()
            .orElse(true);

        log.debug("Reloaded backup configs successful? %s", success);
    }

    /**
     * Will save all stored configs as backups.
     *
     * @param deleteOriginals whether original config files shall be deleted
     * @return whether all configs were successfully backed up
     */
    public boolean saveBackups(boolean deleteOriginals) {
        Boolean success = configDefinitions.entrySet().stream()
            .filter(entry -> entry.getValue().isDoBackup())
            .map(entry -> saveBackup(entry.getKey(), deleteOriginals))
            .filter(aBoolean -> !aBoolean)
            .findFirst()
            .orElse(true);

        log.debug("Saved backups: %s", success);
        return success;
    }

    /**
     * Saves a given object when its class is bound to the store.
     *
     * @param config to save
     * @return file path of saved config or null when not successful
     */
    @Nullable
    public Path save(Object config) {
        Class<?> configClass = config.getClass();
        ConfigDefinition configDefinition = this.configDefinitions.get(configClass);

        if (configDefinition == null) {
            log.debug("Can not save object %s. No config entry present.", configClass.getSimpleName());
            return null;
        }

        return save(config, configDefinition, false);
    }

    /**
     * Will delete the config file associated with the given class.
     * Will also remove the config object from the store.
     *
     * @return whether deletion was successful
     */
    public boolean delete(Class<?> configClass, boolean removeFromStore) {
        log.debug("Deleting config for %s", configClass.getSimpleName());
        ConfigDefinition configDefinition = configDefinitions.get(configClass);

        if (configDefinition == null) {
            log.debug("Can not delete config file for %s. No config entry.", configClass.getSimpleName());
            // nothing there to delete? OK!
            return true;
        }

        return delete(configDefinition, removeFromStore);
    }


    /**
     * Will save a stored config as backups.
     *
     * @param deleteOriginal whether original config file shall be deleted
     * @return whether config was successfully backed up
     */
    private boolean saveBackup(Class<?> configClass, boolean deleteOriginal) {
        ConfigDefinition configDefinition = this.configDefinitions.get(configClass);
        Object config = configStore.get(configClass);

        if (configDefinition == null || config == null) {
            log.debug("Can not backup config for %s. No config present.", configClass.getSimpleName());
            return false;
        }

        Path savePath = save(config, configDefinition, true);
        if (savePath != null && deleteOriginal) {
            Path originalPath = filePath(configDefinition, false);
            if (originalPath != null) {
                return jsonService.delete(originalPath);
            }
        }

        return true;
    }

    /**
     * Will read a config file with the given path. Will not store the read config.
     *
     * @param configClass to read
     * @param path of the file with content
     * @return read config class with content
     * @param <T> type of the config class
     */
    public <T> Optional<T> readFromPath(Class<T> configClass, Path path) {
        ConfigDefinition configDefinition = this.configDefinitions.get(configClass);

        if (configDefinition == null) {
            log.debug("Can not load %s config from file %s. No config entry.", configClass, path);
            return Optional.empty();
        }

        return load(configClass, path, false);
    }

    private <T> Optional<T> reloadBackup(Class<T> configClass) {
        ConfigDefinition configDefinition = configDefinitions.get(configClass);

        if (configDefinition == null) {
            log.debug("Can not load backup config for %s. No config entry.", configClass);
            return Optional.empty();
        }

        return load(configClass, configDefinition, true).map(backupConfig -> {
            storeBackup(configClass, backupConfig);
            return backupConfig.getConfig();
        });
    }

    private List<FileService.FileMeta> readMetas(ConfigDefinition configDefinition) {
        if (!configDefinition.isBoundToSave()) {
            return Lists.of();
        }

        return fileService.readMetas(configDefinition.getPath());
    }

    /**
     * Will delete only the backup config file associated with the given class.
     * Will also remove the config object from the backup store.
     *
     * @return whether deletion was successful
     */
    public boolean deleteBackup(Class<?> configClass, boolean removeFromStore) {
        log.debug("Deleting backup config for %s", configClass.getSimpleName());
        ConfigDefinition configDefinition = configDefinitions.get(configClass);

        if (configDefinition == null) {
            log.debug("Can not delete backup config file for %s. No config entry.", configClass.getSimpleName());
            // nothing there to delete? OK!
            return true;
        }

        return deleteBackup(configDefinition, removeFromStore);
    }

    /**
     * Will delete all backup config files bound to the store.
     * Will also remove the config objects from the backup store.
     *
     * @return whether deletion of all backups was successful
     */
    public boolean deleteBackups(boolean removeFromStore) {
        Boolean success = backupConfigStore.keySet().stream()
            .map(configClass -> deleteBackup(configClass, removeFromStore))
            .filter(aBoolean -> !aBoolean)
            .findFirst()
            .orElse(true);

        log.debug("Deleted backups: %s", success);
        return success;
    }

    private <T> Optional<T> reload(Class<T> configClass) {
        // save in store when loaded
        return load(configClass).map(config -> {
            store(configClass, config);
            return config.getConfig();
        });
    }

    private <T> Optional<ConfigObject<T>> load(Class<T> configClass) {
        ConfigDefinition configDefinition = this.configDefinitions.get(configClass);

        if (configDefinition == null) {
            log.debug("Can not load config for %s. No config entry.", configClass);
            return Optional.empty();
        }

        return load(configClass, configDefinition, false);
    }

    private <T> Optional<T> load(
        Class<T> configClass,
        Path savePath,
        boolean asBackup
    ) {
        if (asBackup) {
            savePath = asBackupPath(savePath);
        }

        return jsonService.load(savePath, configClass);
    }

    private <T> Optional<ConfigObject<T>> load(Class<T> configClass, ConfigDefinition configDefinition, boolean asBackup) {
        log.debug("Loading config %s, as backup: %s", configClass.getSimpleName(), asBackup);
        Path path = filePath(configDefinition, asBackup);

        if (path == null) {
            return Optional.empty();
        }

        try {
            return jsonService.load(path, configClass).map(config -> ConfigObject.<T>builder()
                .config(config)
                .path(path)
                .build());
        } catch (Exception e) {
            log.error("Could not load config from %", path);
            throw e;
        }
    }

    /**
     * Saves a given config object as json.
     * 
     * @param config to save
     * @param configDefinition for determining the save path
     * @param asBackup whether config filename shall be prefixed with backup
     * @return path of the saved config or null when saving failed
     */
    @Nullable
    private Path save(Object config, ConfigDefinition configDefinition, boolean asBackup) {
        log.debug("Saving config %s", config.getClass().getSimpleName());
        Path savePath = filePath(configDefinition, asBackup);

        if (savePath == null) {
            return null;
        }

        // store newly saved config
        if (jsonService.save(savePath, config)) {
            store(config.getClass(), config, savePath);
            return savePath;
        }

        return null;
    }

    private boolean delete(ConfigDefinition configDefinition, boolean removeFromStore) {
        Path filePath = filePath(configDefinition, false);

        if (filePath == null) {
            return false;
        }

        if (jsonService.delete(filePath)) {
            if (removeFromStore) configStore.remove(configDefinition.getConfigClass());
            return true;
        }

        return false;
    }

    private boolean deleteBackup(ConfigDefinition configDefinition, boolean removeFromStore) {
        Path backupFilePath = filePath(configDefinition, true);

        if (backupFilePath == null) {
            return false;
        }

        if (jsonService.delete(backupFilePath)) {
            if (removeFromStore) backupConfigStore.remove(configDefinition.getConfigClass());
            return true;
        }

        return false;
    }

    private void store(Class<?> configClass, Object config, Path savePath) {
        log.debug("Storing config for %s", configClass.getSimpleName());
        configStore.put(configClass, ConfigObject.builder()
                .config(config)
                .path(savePath)
                .build());
    }
    private void store(Class<?> configClass, ConfigObject<?> configObject) {
        log.debug("Storing config for %s", configClass.getSimpleName());
        configStore.put(configClass, configObject);
    }

    private void storeBackup(Class<?> configClass, Object config, Path savePath) {
        log.debug("Storing backup config for %s", configClass.getSimpleName());
        backupConfigStore.put(configClass, ConfigObject.builder()
            .config(config)
            .path(savePath)
            .build());
    }

    private void storeBackup(Class<?> configClass, ConfigObject<?> configObject) {
        log.debug("Storing backup config for %s", configClass.getSimpleName());
        backupConfigStore.put(configClass, configObject);
    }

    /**
     * Creates a file path for saving or loading a config object as json.
     *
     * @param configDefinition with information on how to build the path
     * @param asBackup whether path shall be transformed to a backup path
     * @return path to save config or null when the save stamp isn't present
     */
    @Nullable
    private Path filePath(ConfigDefinition configDefinition, boolean asBackup) {
        Path path;
        if (configDefinition.isBoundToSave()) {
            String fileName = configSaveFileName(configDefinition.getNamePrefix());
            if (fileName == null) {
                log.debug("Could not create save game bound file path for %s. No game save stamp present.", configDefinition.getConfigClass().getSimpleName());
                return null;
            }
            
            path = configDefinition.getPath().resolve(fileName);
        } else {
            path = configDefinition.getPath();
        }

        if (asBackup) {
            path = asBackupPath(path);
        }

        return path;
    }

    /**
     * Configs bound to saves will get a special name with the game save stamp
     *
     * @param namePrefix used in combination with the saveStamp
     * @return filename for a save bound config or null when there is no stamp
     */
    @Nullable
    private String configSaveFileName(String namePrefix) {
        String saveStamp = saveStamp();
        
        if (saveStamp == null) {
            return null;
        }
        
        return saveStamp + "." + namePrefix + FILE_ENDING;
    }

    /**
     * For identifying and associating config files with game saves
     */
    @Nullable
    private String saveStamp() {
        return gameSaveApi.getSaveStamp();
    }

    /**
     * Will rewrite a path / filename with a {@link this#BACKUP_FILE_PREFIX} in it.
     *
     * @param path to create a backup path from
     * @return created backup path
     */
    private Path asBackupPath(Path path) {
        String fullName = path.getFileName().toString();
        String[] parts = fullName.split("\\.");

        String fileEnding = FILE_ENDING;
        String fileName = fullName;
        if (parts.length > 0) {
            fileEnding = parts[parts.length - 1];
            fileName = String.join(".", Arrays.copyOfRange(parts, 0, parts.length - 1));
        }

        String backupFileName = fileName + "." + BACKUP_FILE_PREFIX + "." + fileEnding;

        return path.getParent().resolve(backupFileName);
    }

    /**
     * Used for knowing how to save and load config from json files
     */
    @Getter
    @Builder
    private static class ConfigDefinition {
        /**
         * Class of the config object
         */
        private Class<?> configClass;
        /**
         * Where it shall be saved.
         * Can be folder path of boundToSave is true.
         */
        @NonNull
        private Path path;
        /**
         * Whether config shall be saved with a save game identifier (save stamp)
         */
        private boolean boundToSave;
        /**
         * Used when config shall be bound to save.
         * Will be used in the config file name in combination with the game save stamp.
         */
        @Builder.Default
        private String namePrefix = "";

        /**
         * Whether the store shall try to create and read backups from this config
         */
        @Builder.Default
        private boolean doBackup = false;
    }

    @Getter
    @Builder
    private static class ConfigObject<T> {
        private T config;
        private Path path;
    }
}
