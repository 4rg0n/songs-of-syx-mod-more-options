package com.github.argon.sos.mod.sdk.json.parser;

import com.github.argon.sos.mod.sdk.json.Json;
import com.github.argon.sos.mod.sdk.json.element.JsonElement;

/**
 * Methods for parsing json into {@link JsonElement}s.
 */
public interface Parser {
    /**
     * Parses a {@link Json} containing the raw string into a {@link JsonElement}.
     *
     * @param json to parse
     * @return parsed json element
     */
    JsonElement parse(Json json);
}
