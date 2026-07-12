package com.github.argon.sos.mod.sdk.json.writer;

import com.github.argon.sos.mod.sdk.json.element.JsonElement;

/**
 * Interface for json writers.
 */
public interface JsonWriter {
    /**
     * Writes given {@link JsonElement} as a readable string
     *
     * @param json to write as a string
     * @return readable json string
     */
    String write(JsonElement json);
}
