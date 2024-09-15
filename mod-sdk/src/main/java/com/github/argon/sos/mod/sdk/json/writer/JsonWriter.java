package com.github.argon.sos.mod.sdk.json.writer;

import com.github.argon.sos.mod.sdk.json.element.JsonElement;

public interface JsonWriter {
    /**
     * Writes given {@link JsonElement} as a readable string
     *
     * @return readable json string
     */
    String write(JsonElement json);
}
