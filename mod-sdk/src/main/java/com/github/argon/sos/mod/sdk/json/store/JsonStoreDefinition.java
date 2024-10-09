package com.github.argon.sos.mod.sdk.json.store;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.nio.file.Path;

/**
 * Used for knowing how to save and load config from JSON files
 */
@Getter
@Builder
public class JsonStoreDefinition {
    /**
     * Class of the config object
     */
    @NonNull
    private Class<?> clazz;
    /**
     * Where it shall be saved.
     * Can be the folder path when boundToSave is true.
     */
    @NonNull
    private Path path;
    /**
     * Used when config shall be bound to save.
     * Will be used in the config file name in combination with the game save stamp.
     */
    @Builder.Default
    private String name = "";
}
