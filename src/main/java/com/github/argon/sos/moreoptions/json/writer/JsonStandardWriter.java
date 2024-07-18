package com.github.argon.sos.moreoptions.json.writer;

import com.github.argon.sos.moreoptions.json.element.JsonElement;

/**
 * For printing {@link JsonElement}s as JSON Standard string
 */
public class JsonStandardWriter extends AbstractJsonWriter {

    public JsonStandardWriter() {
        super(false, 0);
    }

    public JsonStandardWriter(int indent) {
        super(false, indent);
    }

    @Override
    protected boolean doPrintBlockMarks() {
        return depth > 0;
    }
}
