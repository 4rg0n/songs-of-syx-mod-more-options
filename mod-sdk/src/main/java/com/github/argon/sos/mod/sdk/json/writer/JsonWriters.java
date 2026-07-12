package com.github.argon.sos.mod.sdk.json.writer;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Prebuilt writers for different formats
 */
public class JsonWriters {

    /** Creates a new {@link JsonWriters}. */
    public JsonWriters() {
    }

    /**
     * Songs of Syx JSON format with indents
     */
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static JsonWriter gameJsonUnquotedPretty = new GameJsonUnquotedWriter();

    /**
     * Songs of Syx JSON format with indents and quoted strings
     */
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static JsonWriter gameJsonQuotedPretty = new GameJsonQuotedWriter();

    /**
     * Standard JSON with indents
     */
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static JsonWriter jsonPretty = new JsonStandardWriter();

    /**
     * Standard JSON
     */
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static JsonWriter json = new JsonStandardWriter();
}
