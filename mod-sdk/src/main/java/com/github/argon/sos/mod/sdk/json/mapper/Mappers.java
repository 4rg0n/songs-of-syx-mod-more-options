package com.github.argon.sos.mod.sdk.json.mapper;

import com.github.argon.sos.mod.sdk.json.element.JsonElement;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Contains all mappers for mapping {@link JsonElement}s into and from java objects.
 */
@RequiredArgsConstructor
public class Mappers {
    private final List<Mapper<?>> mappers;

    /**
     * Contains all mappers provided by the sdk.
     *
     * @return mapper with all mappers.
     */
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

    /**
     * Tries to find mappers, which supports mapping of the given class.
     *
     * @param clazz to map
     * @return list of matching mappers
     */
    public List<Mapper<?>> find(Class<?> clazz) {
        return mappers.stream()
            .filter(mapper -> mapper.supports(clazz))
            .toList();
    }

    /**
     * Tries to find exactly one mapper, which supports mapping og the given class.
     *
     * @param clazz to map
     * @return one mapper if present
     * @throws JsonMapperException if more than one mapper was found.
     */
    public Optional<Mapper<?>> findOne(Class<?> clazz) {
        List<Mapper<?>> foundMappers = find(clazz);

        if (foundMappers.isEmpty()) {
            return Optional.empty();
        }

        if (foundMappers.size() > 1) {
            throw new JsonMapperException("Found " + foundMappers.size() + " for mapping " + clazz.getTypeName() + ". Required one.");
        }

        return Optional.of(foundMappers.getFirst());
    }
}
