package com.github.argon.sos.mod.sdk.json.parser;

import com.github.argon.sos.mod.sdk.json.Json;
import com.github.argon.sos.mod.sdk.json.element.JsonElement;
import com.github.argon.sos.mod.sdk.json.element.JsonString;

/**
 * For parsing a string value e.g. FOO
 */
public class StringParser implements Parser {
    /**
     * Creates a new {@link StringParser}.
     */
    public StringParser() {
    }

    /**
     * Parses json string into a {@link JsonString}.
     *
     * @param json to parse
     * @return parsed json object
     */
    @Override
    public JsonElement parse(Json json) {
        // skip "
        json.indexMove();
        String value = json.getNextValue('\"');
        // skip "
        json.indexMove();
        return parseString(value);
    }

    /**
     * Creates a new {@link JsonString} from a given {@link String}.
     *
     * @param value string value
     * @return json string
     */
    public static JsonString parseString(String value) {
        return JsonString.of(value);
    }
}
