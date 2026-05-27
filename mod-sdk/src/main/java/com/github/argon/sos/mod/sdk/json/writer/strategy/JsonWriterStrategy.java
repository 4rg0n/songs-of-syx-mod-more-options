package com.github.argon.sos.mod.sdk.json.writer.strategy;

import com.github.argon.sos.mod.sdk.json.element.JsonElement;

public interface JsonWriterStrategy {
    JsonWriterStrategyType get(JsonElement value);
}
