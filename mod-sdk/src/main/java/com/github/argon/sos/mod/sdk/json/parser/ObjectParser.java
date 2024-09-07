package com.github.argon.sos.mod.sdk.json.parser;

import com.github.argon.sos.mod.sdk.json.Json;
import com.github.argon.sos.mod.sdk.json.element.JsonElement;
import com.github.argon.sos.mod.sdk.json.element.JsonObject;
import com.github.argon.sos.mod.sdk.json.element.JsonTuple;

/**
 * For parsing key value pairs present in objects e.g. {NAME: FOO, SIZE: 1,}
 */
public class ObjectParser implements Parser {
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

            JsonElement element = JsonParser.parse(json);
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
