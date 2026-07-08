package com.github.argon.sos.mod.sdk.json.writer;

import com.github.argon.sos.mod.sdk.json.element.JsonElement;

/**
 * For printing {@link JsonElement}s as Songs of Syx JSON string
 * Will not quote string values.
 */
public class GameJsonUnquotedWriter extends AbstractGameJsonWriter {

    /**
     * Creates a new {@link GameJsonUnquotedWriter} with no indention.
     */
    public GameJsonUnquotedWriter() {
        this(0);
    }

    /**
     * Creates a new {@link GameJsonUnquotedWriter} with given indention.
     *
     * @param indent amount of spaces to use for one indention
     */
    public GameJsonUnquotedWriter(int indent) {
        super(false, false, true, indent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean doPrintBlockMarks() {
        return depth > 0;
    }
}
