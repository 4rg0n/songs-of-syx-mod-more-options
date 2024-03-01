package com.github.argon.sos.moreoptions.json.parser;

import com.github.argon.sos.moreoptions.json.Json;
import com.github.argon.sos.moreoptions.json.JsonParser;
import com.github.argon.sos.moreoptions.json.element.JsonArray;
import com.github.argon.sos.moreoptions.json.element.JsonElement;

public class ArrayParser extends Parser {
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
