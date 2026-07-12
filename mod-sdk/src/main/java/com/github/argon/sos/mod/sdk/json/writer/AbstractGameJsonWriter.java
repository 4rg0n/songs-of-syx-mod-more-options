package com.github.argon.sos.mod.sdk.json.writer;

import com.github.argon.sos.mod.sdk.json.writer.strategy.JsonQuoteStrategy;
import com.github.argon.sos.mod.sdk.json.writer.strategy.JsonWriterStrategy;

import java.util.Map;

/**
 * Base class for all game {@link JsonWriter}s
 */
public class AbstractGameJsonWriter extends AbstractJsonWriter {

    /**
     * Creates  new json writer with given configuration.
     *
     * @param quoteKeys whether json keys shall be quoted
     * @param quoteStrings whether string values shall be quoted
     * @param trailingComma whether each line shall have a comma at the end
     * @param indent how many spaces shall be used for one indention
     */
    public AbstractGameJsonWriter(boolean quoteKeys, boolean quoteStrings, boolean trailingComma, int indent) {
        super(quoteKeys, quoteStrings, trailingComma, indent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<String, JsonWriterStrategy> getStrategies() {
        return Map.of(
            // values in the MINI_COLOR_PATTERN array must be quoted
            "MINI_COLOR_PATTERN", JsonQuoteStrategy.getInstance()
        );
    }
}
