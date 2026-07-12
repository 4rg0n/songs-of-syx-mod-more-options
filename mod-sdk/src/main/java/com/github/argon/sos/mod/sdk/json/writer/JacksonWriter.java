package com.github.argon.sos.mod.sdk.json.writer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

import java.io.IOException;

/**
 * Custom pretty printer for writing JSON via Jackson
 */
public class JacksonWriter extends DefaultPrettyPrinter {
    /**
     * Creates a new {@link JacksonWriter}.
     */
    public JacksonWriter() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefaultPrettyPrinter createInstance() {
        return new JacksonWriter();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeObjectFieldValueSeparator(JsonGenerator jg) throws IOException {
        // "key": "value"
        jg.writeRaw(": ");
    }
}
