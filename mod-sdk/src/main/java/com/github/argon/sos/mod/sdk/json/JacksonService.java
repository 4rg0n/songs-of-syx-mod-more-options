package com.github.argon.sos.mod.sdk.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.argon.sos.mod.sdk.file.IOService;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Uses Jackson as JSON parser and writer.
 */
@RequiredArgsConstructor
public class JacksonService implements JsonService {

    private final ObjectMapper objectMapper;
    private final PrettyPrinter printer;
    private final IOService ioService;

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Optional<T> read(Path filePath, Class<T> clazz) {
        return read(filePath)
            .map(json -> {
                try {
                    return objectMapper.readValue(json, clazz);
                } catch (JsonProcessingException e) {
                    throw new JsonException(String.format("Could not map json from %s for %s", filePath, clazz.getSimpleName()), e);
                }
            });
    }

    /**
     * {@inheritDoc}
     *
     * @throws JsonException when parsing fails
     */
    @Override
    public void write(Path filePath, Object object) {
        String jsonString;
        try {
            jsonString = objectMapper
                .writer(printer)
                .writeValueAsString(object);
        } catch (Exception e) {
            throw new JsonException(String.format("Could not create json from object %s for saving in %s",
                filePath, object.getClass().getSimpleName()), e);
        }

        write(filePath, jsonString);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(Path filePath) {
        try {
            return ioService.delete(filePath);
        } catch (Exception e) {
            throw new JsonException(String.format("Could not delete json file %s", filePath), e);
        }
    }

    private void write(Path path, String json) {
        try {
            ioService.write(path, json);
        } catch (Exception e) {
            throw new JsonException(String.format("Could not write json file %s", path), e);
        }
    }

    private Optional<String> read(Path path) {
        try {
            return Optional.ofNullable(ioService.read(path));
        } catch (IOException e) {
            throw new JsonException(String.format("Could not read json file %s", path), e);
        }
    }
}
