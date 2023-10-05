package com.github.argon.sos.moreoptions.ui.builder;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.Map;

@Getter
@Builder
public class BuildResults<T, E> {

    private T result;

    @Singular
    private Map<String, E> elements;
}
