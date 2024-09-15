package com.github.argon.sos.mod.sdk.properties;

import com.github.argon.sos.mod.sdk.file.IOService;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Takes care of reading and caching properties objects read from Java *.properties files.
 * Depending on the used {@link PropertiesService} and its used {@link IOService}, properties can be read from inside or outside a *.jar file.
 */
@RequiredArgsConstructor
public class PropertiesStore {

    private final static Logger log = Loggers.getLogger(PropertiesStore.class);

    private final PropertiesService propertiesService;

    /**
     * Contains properties objects loaded from *.properties files
     */
    private final Map<Class<?>, PropertiesObject<?>> propertiesStore = new HashMap<>();

    /**
     * Contains definitions on how to handle the properties
     */
    private final Map<Class<?>, PropertiesDefinition> propertyDefinitions = new HashMap<>();

    /**
     * Registers a properties class with its path to save in the store.
     *
     * @param propertiesClass of the properties to bing
     * @param file path for reading the file
     * @param doCache whether the properties shall be stored in memory or read everytime
     */
    public void bind(Class<?> propertiesClass, Path file, boolean doCache) {
        propertyDefinitions.put(propertiesClass, PropertiesDefinition.builder()
            .path(file)
            .doCache(doCache)
            .propertiesClass(propertiesClass)
            .build());
    }

    /**
     * Will look into the store for a properties object with given class.
     * If not present, will try to load the properties from file with usage of the {@link PropertiesObject}.
     *
     * @param propertiesClass of the properties object to get
     * @return properties object if present
     * @param <T> type of the properties object
     */
    public <T> Optional<T> get(Class<T> propertiesClass) {
        PropertiesDefinition propertiesDefinition = this.propertyDefinitions.get(propertiesClass);

        if (propertiesDefinition != null && !propertiesDefinition.isDoCache()) {
            return reload(propertiesClass);
        }

        T properties = null;
        try {
            PropertiesObject<?> propertiesObject = propertiesStore.get(propertiesClass);

            if (propertiesObject != null) {
                //noinspection unchecked
                properties = (T) propertiesObject.getProperties();
            }
        } catch (Exception e) {
            log.info("Could not get properties for class %s", propertiesClass.getName());
            return Optional.empty();
        }

        if (properties != null) {
            return Optional.of(properties);
        }

        return reload(propertiesClass);
    }

    private <T> Optional<T> reload(Class<T> propertiesClass) {
        // save in store when loaded
        return load(propertiesClass).map(propertiesObject -> {
            store(propertiesClass, propertiesObject);
            return propertiesObject.getProperties();
        });
    }

    private <T> Optional<PropertiesObject<T>> load(Class<T> propertiesClass) {
        PropertiesDefinition propertiesDefinition = this.propertyDefinitions.get(propertiesClass);

        if (propertiesDefinition == null) {
            return Optional.empty();
        }

        Path propertiesPath = propertiesDefinition.getPath();
        return propertiesService.read(propertiesPath, propertiesClass)
            .map(properties -> PropertiesObject.<T>builder()
                .properties(properties)
                .path(propertiesPath)
                .build());
    }

    private void store(Class<?> propertiesClass, PropertiesObject<?> propertiesObject) {
        log.debug("Storing properties for %s", propertiesClass.getSimpleName());
        propertiesStore.put(propertiesClass, propertiesObject);
    }

    @Getter
    @Builder
    private static class PropertiesDefinition {
        /**
         * Class of the properties object
         */
        private Class<?> propertiesClass;

        /**
         * Where it is stored.
         */
        @NonNull
        private Path path;

        /**
         * Whether the properties shall be read everytime from file or saved in memory.
         */
        private boolean doCache;
    }

    @Getter
    @Builder
    private static class PropertiesObject<T> {
        private T properties;
        private Path path;
    }
}
