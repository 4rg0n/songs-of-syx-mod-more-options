package com.github.argon.sos.moreoptions.json.parser;

import com.github.argon.sos.moreoptions.json.Json;
import com.github.argon.sos.moreoptions.json.element.JsonDouble;
import com.github.argon.sos.moreoptions.json.element.JsonElement;
import com.github.argon.sos.moreoptions.json.element.JsonLong;
import com.github.argon.sos.moreoptions.util.StringUtil;

public class ValueParser extends Parser {
    @Override
    public JsonElement parse(Json json) {
        int startIndex = json.getIndex();
        String value = json.getNextValue();

        if (StringUtil.isNumeric(value)) {
            // decimal?
            if (value.contains(".") || value.contains(",")) {
                return new JsonDouble(Double.parseDouble(value));
            } else {
                return new JsonLong(Long.parseLong(value));
            }
        }

        if (value.contains(":")) {
            json.setIndex(startIndex);
            String key = TupleParser.parseKey(json);
            return TupleParser.parse(key, json);
        } else {
            return StringParser.parseString(value);
        }
    }
}
