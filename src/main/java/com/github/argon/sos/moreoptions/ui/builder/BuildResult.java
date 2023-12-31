package com.github.argon.sos.moreoptions.ui.builder;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BuildResult<T, E> {

    private T result;

    private E interactable;
}
