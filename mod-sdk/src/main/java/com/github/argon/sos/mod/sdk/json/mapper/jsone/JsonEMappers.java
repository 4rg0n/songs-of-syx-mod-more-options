package com.github.argon.sos.mod.sdk.json.mapper.jsone;

import com.github.argon.sos.mod.sdk.json.element.JsonElement;
import com.github.argon.sos.mod.sdk.json.mapper.JsonMapperException;
import lombok.RequiredArgsConstructor;
import snake2d.util.file.JsonE;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Contains all mappers for mapping {@link JsonElement}s into games {@link JsonE}
 */
@RequiredArgsConstructor
public class JsonEMappers {
    private final List<JsonEMapper<?>> mappers;

    /**
     * All {@link JsonEMapper}s provided by the sdk.
     *
     * @return default mappers from the sdk
     */
    public static JsonEMappers getDefault() {
        return new JsonEMappers(Arrays.asList(
            new JsonEBooleanMapper(),
            new JsonEDoubleMapper(),
            new JsonELongMapper(),
            new JsonEStringMapper(),
            new JsonEArrayMapper(),
            new JsonEObjectMapper(),
            new JsonENullMapper()
        ));
    }

    /**
     * Will find mappers supporting the mapping of a given type class.
     *
     * @param clazz to check
     * @return found supported mappers
     */
    public List<JsonEMapper<?>> find(Class<?> clazz) {
        return mappers.stream()
            .filter(mapper -> mapper.supports(clazz))
            .toList();
    }

    /**
     * Will find exactly one mapper supporting the mapping of a given type class.
     *
     * @param clazz to check
     * @return found supported mapper
     * @throws JsonMapperException if more than one mapper was found
     */
    public Optional<JsonEMapper<?>> findOne(Class<?> clazz) {
        List<JsonEMapper<?>> foundMappers = find(clazz);

        if (foundMappers.isEmpty()) {
            return Optional.empty();
        }

        if (foundMappers.size() > 1) {
            throw new JsonMapperException("Found " + foundMappers.size() + " for mapping " + clazz.getTypeName() + ". Required one.");
        }

        return Optional.of(foundMappers.getFirst());
    }
}
