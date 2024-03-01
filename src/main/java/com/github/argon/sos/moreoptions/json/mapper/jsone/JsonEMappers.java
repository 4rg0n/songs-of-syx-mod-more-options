package com.github.argon.sos.moreoptions.json.mapper.jsone;

import com.github.argon.sos.moreoptions.json.element.JsonElement;
import com.github.argon.sos.moreoptions.json.mapper.*;
import lombok.RequiredArgsConstructor;
import snake2d.util.file.JsonE;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Contains all mappers for mapping {@link JsonElement}s into games {@link JsonE}
 */
@RequiredArgsConstructor
public class JsonEMappers {
    private final List<JsonEMapper<?>> mappers;

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

    public List<JsonEMapper<?>> find(Class<?> clazz) {
        return mappers.stream()
            .filter(mapper -> mapper.supports(clazz))
            .collect(Collectors.toList());
    }

    public Optional<JsonEMapper<?>> findOne(Class<?> clazz) {
        List<JsonEMapper<?>> foundMappers = find(clazz);

        if (foundMappers.isEmpty()) {
            return Optional.empty();
        }

        if (foundMappers.size() > 1) {
            throw new JsonMapperException("Found " + foundMappers.size() + " for mapping " + clazz.getTypeName() + ". Required one.");
        }

        return Optional.of(foundMappers.get(0));
    }
}
