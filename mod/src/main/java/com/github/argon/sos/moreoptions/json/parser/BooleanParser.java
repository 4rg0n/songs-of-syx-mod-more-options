package com.github.argon.sos.moreoptions.json.parser;

import com.github.argon.sos.moreoptions.json.Json;
import com.github.argon.sos.moreoptions.json.element.JsonBoolean;
import com.github.argon.sos.moreoptions.json.element.JsonElement;

/**
 * For parsing boolean values: true and false
 */
public class BooleanParser implements Parser {
    @Override
    public JsonElement parse(Json json) {
        String value = json.getNextValue(true);

        switch (value) {
            case "true":
                return new JsonBoolean(true);
            case "false":
                return new JsonBoolean(false);
            default:
                throw new JsonParseException("Could not parse boolean value from '" + value + "' at position " + json.getIndex());
        }
    }
}
