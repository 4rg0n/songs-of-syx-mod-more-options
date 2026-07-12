package com.github.argon.sos.mod.sdk.json.writer;

import com.github.argon.sos.mod.sdk.json.element.JsonElement;

/**
 * For printing {@link JsonElement}s as JSON Standard string.
 */
public class JsonStandardWriter extends AbstractJsonWriter {

    /**
     * Creates a new {@link JsonStandardWriter} with 2 spaces used for indention.
     */
    public JsonStandardWriter() {
        this(2);
    }

    /**
     * Creates a new {@link JsonStandardWriter} with given indention.
     *
     * @param indent amount of spaces to use for one indention
     */
    public JsonStandardWriter(int indent) {
        super(true, true, false, indent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean doPrintBlockMarks() {
        return depth > 0;
    }
}
