package com.github.argon.sos.mod.sdk.json.parser;

import com.github.argon.sos.mod.sdk.json.Json;
import com.github.argon.sos.mod.sdk.json.element.JsonElement;
import com.github.argon.sos.mod.sdk.json.element.JsonTuple;

/**
 * For parsing a single key value pair e.g. NAME: FOO,
 */
public class TupleParser implements Parser {

    @Override
    public JsonElement parse(Json json) {
        String key = parseKey(json);
        return parse(key, json);
    }

    private JsonTuple parse(String key, Json json) {
        json.skipBlank();
        JsonElement element = JsonParser.parse(json);
        return new JsonTuple(key, element);
    }

    private String parseKey(Json json) {
        json.skipBlank();
        String key = json.getNextValue(':');
        if (key.isEmpty()) {
            return "";
        }

        json.indexMove();
        return key;
    }
}
