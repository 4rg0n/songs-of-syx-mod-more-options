package com.github.argon.sos.mod.sdk.properties;

import tools.jackson.dataformat.javaprop.JavaPropsMapper;
import com.github.argon.sos.mod.sdk.file.IOService;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

/**
 * Serves Java properties mapped to an object.
 * Depending on the used {@link IOService}, properties can be read from inside or outside a *.jar file.
 */
public class PropertiesService {

    private final IOService ioService;

    private final JavaPropsMapper javaPropsMapper;

    /**
     * Creates a new {@link PropertiesService}.
     *
     * @param ioService to read the properties file with
     * @param javaPropsMapper to map the read properties into an object
     */
    public PropertiesService(IOService ioService, JavaPropsMapper javaPropsMapper) {
        this.javaPropsMapper = javaPropsMapper;
        this.ioService = ioService;
    }

    /**
     * Reads properties from the given file path and maps them into an instance of the given class.
     *
     * @param path to the properties file
     * @param clazz to map the properties into
     * @return mapped properties object if present
     * @param <T> type of the properties object
     */
    public <T> Optional<T> read(Path path, Class<T> clazz) {
        try {
            Properties properties = ioService.readProperties(path);

            if (properties == null) {
                return Optional.empty();
            }

            return Optional.of(javaPropsMapper.readPropertiesAs(properties, clazz));
        } catch (Exception e) {
            throw new PropertiesException("Could not read or parse properties from " + path + " into " + clazz.getName(), e);
        }
    }
}
