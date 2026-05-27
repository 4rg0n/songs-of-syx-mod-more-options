package com.github.argon.sos.mod.sdk.json.writer.strategy;

import com.github.argon.sos.mod.sdk.json.element.JsonElement;

/**
 * A strategy defines certain behavior for a given json value
 */
public interface JsonWriterStrategy {
    JsonWriterStrategyType get(JsonElement value);
}
