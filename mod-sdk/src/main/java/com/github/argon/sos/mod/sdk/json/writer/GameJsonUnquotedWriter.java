package com.github.argon.sos.mod.sdk.json.writer;

import com.github.argon.sos.mod.sdk.json.element.JsonElement;

/**
 * For printing {@link JsonElement}s as Songs of Syx JSON string
 */
public class GameJsonUnquotedWriter extends AbstractGameJsonWriter {

    public GameJsonUnquotedWriter() {
        this(0);
    }

    public GameJsonUnquotedWriter(int indent) {
        super(false, false, true, indent);
    }

    @Override
    protected boolean doPrintBlockMarks() {
        return depth > 0;
    }
}
