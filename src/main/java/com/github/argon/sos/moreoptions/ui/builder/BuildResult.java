package com.github.argon.sos.moreoptions.ui.builder;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.Map;
import java.util.Optional;

@Getter
@Builder
public class BuildResult<T, E> {

    public final static String NO_KEY = "_";

    private T result;

    @Singular
    private Map<String, E> elements;

    /**
     * @return element with {@link this#NO_KEY} as key
     */
    public Optional<E> getElement() {
        if (elements.containsKey(NO_KEY)) {
            return Optional.of(elements.get(NO_KEY));
        } else {
            return Optional.empty();
        }
    }
}
