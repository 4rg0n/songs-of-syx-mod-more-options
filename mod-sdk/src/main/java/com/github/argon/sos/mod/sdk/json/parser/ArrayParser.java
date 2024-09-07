package com.github.argon.sos.mod.sdk.json.parser;

import com.github.argon.sos.mod.sdk.json.Json;
import com.github.argon.sos.mod.sdk.json.element.JsonArray;
import com.github.argon.sos.mod.sdk.json.element.JsonElement;

/**
 * For parsing array structures e.g. [TEXT,TEXT,]
 */
public class ArrayParser implements Parser {
    @Override
    public JsonElement parse(Json json) {
        JsonArray jsonArray = new JsonArray();

        // skip [
        json.indexMove();
        while (true) {
            json.skipBlank();

            if (isEnd(json.currentChar())) {
                break;
            }

            JsonElement element = JsonParser.parse(json);
            jsonArray.add(element);

            json.skipBlank();
            if (json.currentChar() == ',') {
                json.indexMove();
            }
        }
        // skip ]
        json.indexMove();

        return jsonArray;
    }

    boolean isEnd(char c) {
        return c == ']';
    }
}
