package com.github.argon.sos.moreoptions.ui.builder;

public interface Translatable {
    String getKey();

    void setTitle(String title);

    void setDescription(String description);

    boolean isTranslate();
}
