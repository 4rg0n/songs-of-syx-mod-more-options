package com.github.argon.sos.moreoptions.json.parser;

import com.github.argon.sos.moreoptions.json.Json;
import com.github.argon.sos.moreoptions.json.element.JsonElement;
import com.github.argon.sos.moreoptions.json.element.JsonString;

/**
 * For parsing a string value e.g. FOO
 */
public class StringParser implements Parser {
    @Override
    public JsonElement parse(Json json) {
        // skip "
        json.indexMove();
        String value = json.getNextValue('\"');
        // skip "
        json.indexMove();
        return parseString(value);
    }

    public static JsonString parseString(String value) {
        return JsonString.of(value);
    }
}
