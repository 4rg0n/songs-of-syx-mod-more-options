package com.github.argon.sos.moreoptions.json.parser;

import com.github.argon.sos.moreoptions.json.Json;
import com.github.argon.sos.moreoptions.json.JsonParser;
import com.github.argon.sos.moreoptions.json.element.JsonElement;
import com.github.argon.sos.moreoptions.json.element.JsonObject;
import com.github.argon.sos.moreoptions.util.StringUtil;

public class ObjectParser extends Parser {
    @Override
    public JsonElement parse(Json json) {
        JsonObject jsonObject = new JsonObject();
        // skip {
        json.indexMove();
        try {
            while (true){
                json.skipBlank();
                if (isEnd(json.currentChar())) {
                    break;
                }

                String key = json.getNextValue(':');
                // skip :
                json.indexMove();
                json.skipBlank();

                JsonElement element = JsonParser.parse(json);
                jsonObject.put(StringUtil.unquote(key), element);

                json.skipBlank();
                if (json.currentChar() == ','){
                    json.indexMove();
                }
            }
        } catch (RuntimeException e) {
            throw new JsonParseException("Could not parse object at position " + json.getIndex(), e);
        }
        // skip }
        json.indexMove();

        return jsonObject;
    }

    boolean isEnd(char c) {
        return c == '}';
    }
}
