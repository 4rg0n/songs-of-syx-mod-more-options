package com.github.argon.sos.moreoptions.json.writer;

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
    private final static JsonWriter jsonEPretty = new JsonEWriter(4);
    /**
     * Standard JSON with indents
     */
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static JsonWriter jsonPretty = new JsonStandardWriter(2);

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
