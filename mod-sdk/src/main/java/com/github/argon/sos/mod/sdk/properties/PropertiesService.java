package com.github.argon.sos.mod.sdk.properties;

import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
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

    public PropertiesService(IOService ioService, JavaPropsMapper javaPropsMapper) {
        this.javaPropsMapper = javaPropsMapper;
        this.ioService = ioService;
    }

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
