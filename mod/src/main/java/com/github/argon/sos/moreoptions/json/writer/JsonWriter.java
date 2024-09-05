package com.github.argon.sos.moreoptions.json.writer;

import com.github.argon.sos.moreoptions.json.element.JsonElement;

public interface JsonWriter {
    /**
     * Writes given {@link JsonElement} as a readable string
     *
     * @return readable json string
     */
    String write(JsonElement json);
}
