package com.github.argon.sos.mod.sdk.json.mapper;

import com.github.argon.sos.mod.sdk.json.element.JsonElement;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Contains all mappers for mapping {@link JsonElement}s into and from objects
 */
@RequiredArgsConstructor
public class Mappers {
    private final List<Mapper<?>> mappers;

    public static Mappers getDefault() {
        return new Mappers(Arrays.asList(
            new BooleanMapper(),
            new DoubleMapper(),
            new LongMapper(),
            new EnumMapper(),
            new StringMapper(),
            new ListMapper(),
            new MapMapper(),
            new ObjectMapper(),
            new NullMapper()
        ));
    }

    public List<Mapper<?>> find(Class<?> clazz) {
        return mappers.stream()
            .filter(mapper -> mapper.supports(clazz))
            .collect(Collectors.toList());
    }

    public Optional<Mapper<?>> findOne(Class<?> clazz) {
        List<Mapper<?>> foundMappers = find(clazz);

        if (foundMappers.isEmpty()) {
            return Optional.empty();
        }

        if (foundMappers.size() > 1) {
            throw new JsonMapperException("Found " + foundMappers.size() + " for mapping " + clazz.getTypeName() + ". Required one.");
        }

        return Optional.of(foundMappers.get(0));
    }
}
