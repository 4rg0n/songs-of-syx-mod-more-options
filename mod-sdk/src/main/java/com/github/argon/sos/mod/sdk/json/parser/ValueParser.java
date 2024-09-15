package com.github.argon.sos.mod.sdk.json.parser;

import com.github.argon.sos.mod.sdk.json.Json;
import com.github.argon.sos.mod.sdk.json.element.JsonDouble;
import com.github.argon.sos.mod.sdk.json.element.JsonElement;
import com.github.argon.sos.mod.sdk.json.element.JsonLong;
import com.github.argon.sos.mod.sdk.util.StringUtil;

/**
 * For parsing any kind of simple values: Double, Long, Tuple, String
 * e.g. [1.1, 100, FOO: BAR, BAR]
 */
public class ValueParser implements Parser {

    private final TupleParser tupleParser = new TupleParser();

    @Override
    public JsonElement parse(Json json) {
        int startIndex = json.getIndex();
        String value = json.getNextValue();
        json.setIndex(startIndex);

        if (value.contains(":")) {
            return tupleParser.parse(json);
        }

        value = json.getNextValue(true);
        if (StringUtil.isNumeric(value)) {
            // decimal?
            if (value.contains(".") || value.contains(",")) {
                return new JsonDouble(Double.parseDouble(value));
            } else {
                return new JsonLong(Long.parseLong(value));
            }
        }

        return StringParser.parseString(value);
    }
}
