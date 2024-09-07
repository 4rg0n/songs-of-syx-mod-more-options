package com.github.argon.sos.mod.sdk.json.writer;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Prebuilt writers for different formats
 */
public class JsonWriters {

    /**
     * Songs of Syx JSON format with indents
     */
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static JsonWriter jsonEPretty = new JsonEWriter();
    /**
     * Standard JSON with indents
     */
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static JsonWriter jsonPretty = new JsonStandardWriter();

    /**
     * Songs of Syx JSON format
     */
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static JsonWriter jsonE = new JsonEWriter();
    /**
     * Standard JSON
     */
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static JsonWriter json = new JsonStandardWriter();
}
