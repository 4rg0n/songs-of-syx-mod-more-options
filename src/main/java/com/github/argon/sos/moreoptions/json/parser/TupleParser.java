package com.github.argon.sos.moreoptions.json.parser;

import com.github.argon.sos.moreoptions.json.Json;
import com.github.argon.sos.moreoptions.json.element.JsonElement;
import com.github.argon.sos.moreoptions.json.element.JsonTuple;

public class TupleParser {
    public static JsonTuple parse(String key, Json json) {
        json.skipBlank();
        JsonElement element = JsonParser.delegate(json);
        return new JsonTuple(key, element);
    }

    public static String parseKey(Json json) {
        json.skipBlank();
        String key = json.getNextValue(':');
        if (key.isEmpty()) {
            return "";
        }

        json.indexMove();
        return key;
    }
}
