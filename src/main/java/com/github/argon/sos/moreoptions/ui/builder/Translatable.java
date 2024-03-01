package com.github.argon.sos.moreoptions.ui.builder;

import org.jetbrains.annotations.Nullable;

public interface Translatable<Key> {
    @Nullable Key getKey();

    void setTitle(String title);

    void setDescription(String description);

    boolean isTranslatable();
}
