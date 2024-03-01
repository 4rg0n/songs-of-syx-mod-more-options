package com.github.argon.sos.moreoptions.json.parser;

import com.github.argon.sos.moreoptions.json.Json;
import com.github.argon.sos.moreoptions.json.element.JsonElement;

/**
 * Common class for parsers parsing json text
 */
public abstract class Parser {
    public abstract JsonElement parse(Json json);
}
