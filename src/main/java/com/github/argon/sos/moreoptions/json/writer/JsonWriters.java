package com.github.argon.sos.moreoptions.json.writer;

import lombok.Getter;
import lombok.experimental.Accessors;

public class JsonWriters {

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static JsonWriter jsonEPretty = new JsonEWriter(4);
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static JsonWriter jsonPretty = new JsonStandardWriter(2);

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static JsonWriter jsonE = new JsonEWriter();
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static JsonWriter json = new JsonStandardWriter();

}
