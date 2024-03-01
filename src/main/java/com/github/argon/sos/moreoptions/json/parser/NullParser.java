package com.github.argon.sos.moreoptions.json.parser;

import com.github.argon.sos.moreoptions.json.Json;
import com.github.argon.sos.moreoptions.json.element.JsonElement;
import com.github.argon.sos.moreoptions.json.element.JsonNull;

public class NullParser extends Parser {
    @Override
    public JsonElement parse(Json json) {
        // should contain the "null" string; not needed
        json.getNextValue();
        return new JsonNull();
    }
}
