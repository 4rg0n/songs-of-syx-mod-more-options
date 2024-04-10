package com.github.argon.sos.moreoptions.json.parser;

import com.github.argon.sos.moreoptions.json.Json;
import com.github.argon.sos.moreoptions.json.element.JsonElement;
import com.github.argon.sos.moreoptions.json.element.JsonObject;
import com.github.argon.sos.moreoptions.json.element.JsonTuple;

public class ObjectParser extends Parser {
    @Override
    public JsonElement parse(Json json) {
        JsonObject jsonObject = new JsonObject();
        // skip {
        json.indexMove();

        while (true){
            json.skipBlank();
            if (isEnd(json.currentChar())) {
                break;
            }

            JsonElement element = JsonParser.delegate(json);
            if (!(element instanceof JsonTuple)) {
                throw new JsonParseException("Received a " + element.getClass().getSimpleName() + " instead of JsonTuple");
            }

            jsonObject.put(element);

            json.skipBlank();
            if (json.currentChar() == ','){
                json.indexMove();
            }
        }

        // skip }
        json.indexMove();

        return jsonObject;
    }

    boolean isEnd(char c) {
        return c == '}';
    }
}
