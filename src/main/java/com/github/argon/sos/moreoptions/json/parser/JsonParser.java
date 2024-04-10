package com.github.argon.sos.moreoptions.json.parser;

import com.github.argon.sos.moreoptions.json.Json;
import com.github.argon.sos.moreoptions.json.element.JsonElement;

/**
 * For parsing the games json format into a {@link JsonElement}
 */
public class JsonParser {

    private final static ArrayParser arrayParser = new ArrayParser();
    private final static StringParser stringParser = new StringParser();
    private final static ObjectParser objectParser = new ObjectParser();
    private final static NullParser nullParser = new NullParser();
    private final static BooleanParser booleanParser = new BooleanParser();
    private final static ValueParser valueParser = new ValueParser();

    /**
     * Writes a json string into a json object
     */
    public static JsonElement parse(Json json){
        try {
            return delegate(json);
        } catch (Exception e) {
            throw new JsonParseException(json, e);
        }
    }

    /**
     * Delegates to responsible parser when at certain character
     */
    static JsonElement delegate(Json json) {
        char c = json.currentChar();

        switch (c) {
            case '[':
                return arrayParser.parse(json);
            case '\"':
                return stringParser.parse(json);
            case '{':
                return objectParser.parse(json);
            case 'n': // null
                return nullParser.parse(json);
            case 't': // true
            case 'f': // false
                return booleanParser.parse(json);
            default:
                return valueParser.parse(json);
        }
    }
}
