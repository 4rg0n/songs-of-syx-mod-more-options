package com.github.argon.sos.mod.sdk.json.writer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

import java.io.IOException;

/**
 * Custom pretty printer for writing JSON
 */
public class JacksonWriter extends DefaultPrettyPrinter {
    @Override
    public DefaultPrettyPrinter createInstance() {
        return new JacksonWriter();
    }

    @Override
    public void writeObjectFieldValueSeparator(JsonGenerator jg) throws IOException {
        // "key": "value"
        jg.writeRaw(": ");
    }
}
