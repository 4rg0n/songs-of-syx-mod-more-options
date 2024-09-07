package com.github.argon.sos.mod.sdk.json.writer;

import com.github.argon.sos.mod.sdk.json.element.JsonElement;

/**
 * For printing {@link JsonElement}s as JSON Standard string
 */
public class JsonStandardWriter extends AbstractJsonWriter {

    public JsonStandardWriter() {
        this(2);
    }

    public JsonStandardWriter(int indent) {
        super(true, true, false, indent);
    }

    @Override
    protected boolean doPrintBlockMarks() {
        return depth > 0;
    }
}
