package com.github.argon.sos.moreoptions.json;

import com.github.argon.sos.moreoptions.json.element.JsonElement;
import com.github.argon.sos.moreoptions.json.parser.*;

/**
 * For parsing the games json format into a {@link JsonElement}
 */
public class JsonParser {
    /**
     * Delegates to responsible parser when at certain character
     */
    public static JsonElement parse(Json json){
        char c = json.currentChar();
        Parser parser;

        switch (c) {
            case '[':
                parser = new ArrayParser();
                break;
            case '\"':
                parser = new StringParser();
                break;
            case '{':
                parser = new ObjectParser();
                break;
            case 'n': // null
                parser = new NullParser();
                break;
            case 't': // true
            case 'f': // false
                parser = new BooleanParser();
                break;
            default:
                parser = new ValueParser();
        }

        return parser.parse(json);
    }
}
