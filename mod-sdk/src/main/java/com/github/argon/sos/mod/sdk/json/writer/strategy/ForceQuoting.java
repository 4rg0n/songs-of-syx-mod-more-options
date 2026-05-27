package com.github.argon.sos.mod.sdk.json.writer.strategy;

import com.github.argon.sos.mod.sdk.json.element.JsonElement;
import com.github.argon.sos.mod.sdk.json.element.JsonString;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ForceQuoting implements JsonWriterStrategy {

    @Getter(lazy = true)
    private static final ForceQuoting instance = new ForceQuoting();

    public JsonWriterStrategyType get(JsonElement element) {
        if (element instanceof JsonString) {
            return JsonWriterStrategyType.QUOTE;
        }

        return JsonWriterStrategyType.NONE;
    }
}
