package com.github.argon.sos.moreoptions.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.argon.sos.moreoptions.io.FileService;
import com.github.argon.sos.moreoptions.json.writer.JacksonWriter;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Uses Jackson as JSON parser and writer.
 */
@RequiredArgsConstructor
public class JacksonService implements JsonService {
    @Getter(lazy = true)
    private final static JacksonService instance = new JacksonService(
        new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT),
        new JacksonWriter(),
        FileService.getInstance()
    );
    private final static Logger log = Loggers.getLogger(JacksonService.class);

    private final ObjectMapper objectMapper;
    private final PrettyPrinter prettyPrinter;
    private final FileService fileService;

    public <T> Optional<T> load(Path path, Class<T> clazz) {
        return load(path)
            .map(json -> {
                try {
                    return objectMapper.readValue(json, clazz);
                } catch (JsonProcessingException e) {
                    throw new JsonException(String.format("Could not map json from %s for %s", path, clazz.getSimpleName()), e);
                }
            });
    }

    public Optional<String> load(Path path) {
        try {
            return Optional.ofNullable(fileService.read(path));
        } catch (IOException e) {
            throw new JsonException(String.format("Could not rad json file %s", path), e);
        }
    }

    public void save(Path path, Object object) {
        String jsonString;
        try {
            jsonString = objectMapper
                .writer(prettyPrinter)
                .writeValueAsString(object);
        } catch (Exception e) {
            throw new JsonException(String.format("Could not create json from object %s for saving in %s",
                path, object.getClass().getSimpleName()), e);
        }

        save(path, jsonString);
    }

    public void save(Path path, String json) {
        try {
            fileService.write(path, json);
        } catch (Exception e) {
            throw new JsonException(String.format("Could not write json file %s", path), e);
        }
    }

    public boolean delete(Path path) {
        try {
            return fileService.delete(path);
        } catch (Exception e) {
            throw new JsonException(String.format("Could not delete json file %s", path), e);
        }
    }
}
