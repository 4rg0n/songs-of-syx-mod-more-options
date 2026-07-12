package com.github.argon.sos.mod.sdk.json.parser;

import com.github.argon.sos.mod.sdk.json.Json;
import com.github.argon.sos.mod.sdk.json.element.JsonBoolean;
import com.github.argon.sos.mod.sdk.json.element.JsonElement;

/**
 * For parsing boolean values: true and false
 */
public class BooleanParser implements Parser {

    /**
     * Creates a new {@link BooleanParser}.
     */
    public BooleanParser() {
    }

    /**
     * Parses json booleans into a {@link JsonBoolean}.
     *
     * @param json to parse
     * @return parsed json boolean
     * @throws JsonParseException when the value isn't "true" or "false"
     */
    @Override
    public JsonElement parse(Json json) {
        String value = json.getNextValue(true);

        return switch (value) {
            case "true" -> new JsonBoolean(true);
            case "false" -> new JsonBoolean(false);
            default ->
                throw new JsonParseException("Could not parse boolean value from '" + value + "' at position " + json.getIndex());
        };
    }
}
