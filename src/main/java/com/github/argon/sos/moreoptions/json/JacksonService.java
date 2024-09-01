package com.github.argon.sos.moreoptions.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.argon.sos.moreoptions.io.FileService;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import lombok.Getter;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Uses Jackson as JSON parser and writer.
 */
public class JacksonService implements JsonService {
    @Getter(lazy = true)
    private final static JacksonService instance = new JacksonService(
        new ObjectMapper(),
        FileService.getInstance()
    );
    private final static Logger log = Loggers.getLogger(JacksonService.class);

    private final ObjectMapper objectMapper;
    private final FileService fileService;

    public JacksonService(ObjectMapper objectMapper, FileService fileService) {
        this.objectMapper = objectMapper;
        this.fileService = fileService;
    }

    public <T> Optional<T> load(Path path, Class<T> clazz) {
        return load(path)
            .map(json -> {
                try {
                    return objectMapper.readValue(json, clazz);
                } catch (JsonProcessingException e) {
                    log.warn("Could not map json from %s for %s", path, clazz.getSimpleName(), e);
                    return null;
                }
            });
    }

    public Optional<String> load(Path path) {
        return Optional.ofNullable(fileService.read(path));
    }

    public boolean save(Path path, Object object) {
        String jsonString;
        try {
            jsonString = objectMapper
                .writerWithDefaultPrettyPrinter() // pretty print
                .writeValueAsString(object);
        } catch (Exception e) {
            log.warn("Could not create json from object %s for saving in %s",
                path, object.getClass().getSimpleName(), e);
            return false;
        }

        boolean success = save(path, jsonString);
        if (!success) {
            log.warn("Could not write json from object %s", object.getClass().getSimpleName());
        }

        return success;
    }

    public boolean save(Path path, String json) {
        try {
            return fileService.write(path, json);
        } catch (Exception e) {
            log.warn("Could not write json to into %s", path, e);
            return false;
        }
    }

    public boolean delete(Path path) {
        return fileService.delete(path);
    }
}
