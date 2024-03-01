package com.github.argon.sos.moreoptions.json.parser;

import com.github.argon.sos.moreoptions.json.Json;
import com.github.argon.sos.moreoptions.json.element.JsonDouble;
import com.github.argon.sos.moreoptions.json.element.JsonElement;
import com.github.argon.sos.moreoptions.json.element.JsonLong;
import com.github.argon.sos.moreoptions.json.element.JsonString;

public class ValueParser extends Parser {
    @Override
    public JsonElement parse(Json json) {
        String value = json.getNextValue();
        try {

            // decimal?
            if (value.contains(".") || value.contains(",")) {
                return new JsonDouble(Double.parseDouble(value));
            } else {
                return new JsonLong(Long.parseLong(value));
            }
        } catch (RuntimeException e) {
            // must be a string then?
            return new JsonString(value);
        }
    }
}
