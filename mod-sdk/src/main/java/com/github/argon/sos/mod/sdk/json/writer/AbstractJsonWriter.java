package com.github.argon.sos.mod.sdk.json.writer;

import com.github.argon.sos.mod.sdk.json.element.*;
import com.github.argon.sos.mod.sdk.json.writer.strategy.JsonWriterStrategy;
import com.github.argon.sos.mod.sdk.json.writer.strategy.JsonWriterStrategyType;
import com.github.argon.sos.mod.sdk.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.StringJoiner;

public abstract class AbstractJsonWriter implements JsonWriter {

    /**
     * Used internally for knowing how far to indent
     */
    protected int printDepth;

    protected boolean trailingComma;

    protected boolean quoteKeys;

    protected boolean quoteStrings;

    protected int depth;

    protected int indent;

    protected String lineBreak;

    /**
     * Contains certain behaviors for json values by a given json key
     */
    protected final Map<String, JsonWriterStrategy> strategies;

    public AbstractJsonWriter(
        boolean quoteKeys,
        boolean quoteStrings,
        boolean trailingComma,
        int indent
    ) {
        this.quoteKeys = quoteKeys;
        this.quoteStrings = quoteStrings;
        this.trailingComma = trailingComma;
        this.indent = indent;
        this.lineBreak = System.lineSeparator();
        this.strategies = getStrategies();
    }

    public String write(JsonElement json) {
        String jsonString = build(json, null).toString();
        printDepth = 0;

        return jsonString;
    }

    /**
     * @return a map with the json key as key and a given strategy to apply to the values
     */
    protected Map<String, JsonWriterStrategy> getStrategies() {
        return Map.of();
    }

    protected boolean doPrintBlockMarks() {
        return true;
    }

    protected StringBuilder build(JsonObject jsonObject, @Nullable JsonWriterStrategy strategy) {
        return block("{", "}", jsonObject.entries(), strategy);
    }

    protected StringBuilder build(JsonArray jsonArray, @Nullable JsonWriterStrategy strategy) {
        return block("[", "]", jsonArray.getElements(), strategy);
    }
    protected StringBuilder build(JsonTuple jsonTuple) {
        StringBuilder sb = new StringBuilder();

        String key = jsonTuple.getKey();
        JsonWriterStrategy jsonWriterStrategy = strategies.get(key);

        if (quoteKeys) {
            key = StringUtil.quote(key);
        }

        sb.append(key).append(": ");

        JsonElement value = jsonTuple.getValue();
        sb.append(build(value, jsonWriterStrategy));

        return sb;
    }

    protected StringBuilder build(JsonString json, @Nullable JsonWriterStrategy strategy) {
        return jsonString(json, strategy);
    }

    protected StringBuilder build(JsonElement json, @Nullable JsonWriterStrategy strategy) {
        if (json instanceof JsonObject) {
            return build((JsonObject) json, strategy);
        } if (json instanceof JsonArray) {
            return build((JsonArray) json, strategy);
        } if (json instanceof JsonString) {
            return build((JsonString) json, strategy);
        } if (json instanceof JsonTuple) {
            return build((JsonTuple) json);
        } else if (json instanceof JsonLong)  {
            return jsonLong((JsonLong) json, strategy);
        } else if (json instanceof JsonDouble)  {
            return jsonDouble((JsonDouble) json, strategy);
        } else if (json instanceof JsonNull)  {
            return jsonNull((JsonNull) json, strategy);
        } else if (json instanceof JsonBoolean)  {
            return jsonBoolean((JsonBoolean) json, strategy);
        } else {
            throw new RuntimeException("Does not support writing json element " + json.getClass().getSimpleName());
        }
    }

    protected StringBuilder block(String start, String end, Collection<? extends JsonElement> elements, @Nullable JsonWriterStrategy strategy) {
        StringBuilder sb = new StringBuilder();
        String indent = "";
        if (doPrintBlockMarks()) {
            indent = startIndention();
            sb.append(start).append(lineBreak);
        } else {
            depth++;
        }

        String comma = ",";
        StringJoiner stringJoiner = new StringJoiner(comma + lineBreak);
        for (JsonElement element : elements) {
            StringBuilder elementBuilder = new StringBuilder();
            elementBuilder
                .append(indent)
                .append(build(element, strategy));

            stringJoiner.add(elementBuilder);
        }

        sb.append(stringJoiner);
        if (!trailingComma) {
            comma = "";
        }

        if (!elements.isEmpty()) sb.append(comma).append(lineBreak);

        // reset indent and close
        indent = endIndention();
        sb.append(indent);
        // do not print a } on root level
        if (doPrintBlockMarks()) sb.append(end);

        return sb;
    }

    protected String indention() {
        return StringUtil.repeat(' ', Math.max(0, printDepth));
    }

    protected String startIndention() {
        depth++;
        printDepth += indent;
        return indention();
    }

    protected String endIndention() {
        depth--;
        printDepth -= this.indent;
        return indention();
    }

    @NotNull
    protected StringBuilder jsonString(JsonElement json, @Nullable JsonWriterStrategy strategy) {
        String jsonString = json.toString();
        JsonWriterStrategyType strategyType = null;
        if (strategy != null) {
            strategyType = strategy.get(json);
        }

        if (quoteStrings || JsonWriterStrategyType.QUOTE.equals(strategyType)) {
            jsonString = StringUtil.quote(jsonString);
        }

        return new StringBuilder(jsonString);
    }

    @NotNull
    protected StringBuilder jsonBoolean(JsonBoolean json, @Nullable JsonWriterStrategy strategy) {
        return new StringBuilder(json.toString());
    }

    @NotNull
    protected StringBuilder jsonDouble(JsonDouble json, @Nullable JsonWriterStrategy strategy) {
        return new StringBuilder(json.toString());
    }

    @NotNull
    protected StringBuilder jsonLong(JsonLong json, @Nullable JsonWriterStrategy strategy) {
        return new StringBuilder(json.toString());
    }

    @NotNull
    protected StringBuilder jsonNull(JsonNull json, @Nullable JsonWriterStrategy strategy) {
        return new StringBuilder(json.toString());
    }
}
