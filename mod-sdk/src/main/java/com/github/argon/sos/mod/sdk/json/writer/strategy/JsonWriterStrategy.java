package com.github.argon.sos.mod.sdk.json.writer.strategy;

import com.github.argon.sos.mod.sdk.json.element.JsonElement;
import org.jetbrains.annotations.Nullable;

/**
 * A strategy defines certain behavior for a given json value
 */
public interface JsonWriterStrategy {
    /**
     * Returns a strategy for a given {@link JsonElement}.
     *
     * @param jsonElement to get the strategy for
     * @return found strategy
     */
    @Nullable
    JsonWriterStrategyType get(JsonElement jsonElement);
}
