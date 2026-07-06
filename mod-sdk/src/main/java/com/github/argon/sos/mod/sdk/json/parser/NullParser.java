package com.github.argon.sos.mod.sdk.json.parser;

import com.github.argon.sos.mod.sdk.json.Json;
import com.github.argon.sos.mod.sdk.json.element.JsonElement;
import com.github.argon.sos.mod.sdk.json.element.JsonNull;

/**
 * For parsing null values: null
 */
public class NullParser implements Parser {
    /**
     * Parses a null value in a json string
     *
     * @param json to parse
     * @return json null
     */
    @Override
    public JsonElement parse(Json json) {
        json.getNextValue(true);
        return new JsonNull();
    }
}
