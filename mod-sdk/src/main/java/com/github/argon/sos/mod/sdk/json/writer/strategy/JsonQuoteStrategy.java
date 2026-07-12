package com.github.argon.sos.mod.sdk.json.writer.strategy;

import com.github.argon.sos.mod.sdk.json.element.JsonElement;
import com.github.argon.sos.mod.sdk.json.element.JsonString;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Will force quoting json strings
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonQuoteStrategy implements JsonWriterStrategy {

    @Getter(lazy = true)
    private static final JsonQuoteStrategy instance = new JsonQuoteStrategy();

    public JsonWriterStrategyType get(JsonElement jsonElement) {
        if (jsonElement instanceof JsonString) {
            return JsonWriterStrategyType.QUOTE;
        }

        return JsonWriterStrategyType.NONE;
    }
}
