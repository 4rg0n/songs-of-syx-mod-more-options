package com.github.argon.sos.mod.sdk.json.writer;

import com.github.argon.sos.mod.sdk.json.element.JsonElement;

/**
 * For printing {@link JsonElement}s as Songs of Syx JSON string
 * Will quote string values.
 */
public class GameJsonQuotedWriter extends AbstractGameJsonWriter {

    /**
     * Creates a new {@link GameJsonQuotedWriter} with no indention.
     */
    public GameJsonQuotedWriter() {
        this(0);
    }

    /**
     * Creates a new {@link GameJsonQuotedWriter} with given indention.
     *
     * @param indent amount of spaces to use for one indention
     */
    public GameJsonQuotedWriter(int indent) {
        super(false, true, true, indent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean doPrintBlockMarks() {
        return depth > 0;
    }
}
