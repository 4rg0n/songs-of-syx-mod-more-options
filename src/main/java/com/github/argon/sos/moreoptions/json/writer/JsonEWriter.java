package com.github.argon.sos.moreoptions.json.writer;

import com.github.argon.sos.moreoptions.json.element.JsonElement;

/**
 * For printing {@link JsonElement}s as Songs of Syx JSON string
 */
public class JsonEWriter extends AbstractJsonWriter {

    public JsonEWriter() {
        super(true, 0);
    }

    public JsonEWriter(int indent) {
        super(true, indent);
    }

    @Override
    protected boolean doPrintBlockMarks() {
        return depth > 0;
    }
}
