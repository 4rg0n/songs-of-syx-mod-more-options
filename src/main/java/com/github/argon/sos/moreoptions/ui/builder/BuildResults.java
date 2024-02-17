package com.github.argon.sos.moreoptions.ui.builder;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class BuildResults<T, E> {

    private List<T> results;

    @Singular
    private List<E> elements;
}
