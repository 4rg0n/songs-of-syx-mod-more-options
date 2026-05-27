package com.github.argon.sos.mod.sdk.json.writer;

import com.github.argon.sos.mod.sdk.json.writer.strategy.ForceQuoting;
import com.github.argon.sos.mod.sdk.json.writer.strategy.JsonWriterStrategy;

import java.util.Map;

public class AbstractGameJsonWriter extends AbstractJsonWriter {

    public AbstractGameJsonWriter(boolean quoteKeys, boolean quoteStrings, boolean trailingComma, int indent) {
        super(quoteKeys, quoteStrings, trailingComma, indent);
    }

    @Override
    protected Map<String, JsonWriterStrategy> getStrategies() {
        return Map.of(
            // values in the MINI_COLOR_PATTERN array must be quoted
            "MINI_COLOR_PATTERN", ForceQuoting.getInstance()
        );
    }
}
