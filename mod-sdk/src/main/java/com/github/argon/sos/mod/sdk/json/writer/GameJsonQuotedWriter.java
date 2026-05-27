package com.github.argon.sos.mod.sdk.json.writer;

import com.github.argon.sos.mod.sdk.json.element.JsonElement;

/**
 * For printing {@link JsonElement}s as Songs of Syx JSON string
 * Quotes strings.
 */
public class GameJsonQuotedWriter extends AbstractGameJsonWriter {

    public GameJsonQuotedWriter() {
        this(0);
    }

    public GameJsonQuotedWriter(int indent) {
        super(false, true, true, indent);
    }

    @Override
    protected boolean doPrintBlockMarks() {
        return depth > 0;
    }
}
