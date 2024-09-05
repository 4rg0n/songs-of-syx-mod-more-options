package com.github.argon.sos.moreoptions.json.parser;

import com.github.argon.sos.moreoptions.json.Json;
import com.github.argon.sos.moreoptions.json.element.JsonElement;
import com.github.argon.sos.moreoptions.json.element.JsonNull;

/**
 * For parsing null values: null
 */
public class NullParser implements Parser {
    @Override
    public JsonElement parse(Json json) {
        json.getNextValue(true);
        return new JsonNull();
    }
}
