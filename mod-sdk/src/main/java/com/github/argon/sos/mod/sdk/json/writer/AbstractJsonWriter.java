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

/**
 * Base class for all {@link JsonWriter}s.
 */
public abstract class AbstractJsonWriter implements JsonWriter {

    /**
     * Used internally for knowing how far to indent.
     */
    protected int printDepth;

    /**
     * Whether each line shall have a comma at the end.
     */
    protected boolean trailingComma;

    /**
     * Whether json keys shall be quoted.
     */
    protected boolean quoteKeys;

    /**
     * Whether string values shall be quoted.
     */
    protected boolean quoteStrings;

    /**
     * The current depth when writing the json.
     * Used for calculating the spaces for indention.
     */
    protected int depth;

    /**
     * How many spaces shall be used for one indention.
     */
    protected int indent;

    /**
     * Which characters shall be used for a line break.
     */
    protected String lineBreak;

    /**
     * Contains certain behaviors for json values by a given json key.
     */
    protected final Map<String, JsonWriterStrategy> strategies;

    /**
     * Creates  new json writer with given configuration.
     *
     * @param quoteKeys whether json keys shall be quoted
     * @param quoteStrings whether string values shall be quoted
     * @param trailingComma whether each line shall have a comma at the end
     * @param indent how many spaces shall be used for one indention
     */
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

    /**
     * Writes a given {@link JsonElement} to a {@link String}
     *
     * @param json to write
     * @return written json string
     */
    public String write(JsonElement json) {
        String jsonString = build(json, null).toString();
        printDepth = 0;

        return jsonString;
    }

    /**
     * Returns registered strategies for writing.
     * The key of the strategy is the json key for which it will be applied.
     *
     * @return a map with the json key as key and a given strategy to apply to the values
     */
    protected Map<String, JsonWriterStrategy> getStrategies() {
        return Map.of();
    }

    /**
     * A mark can be "{", "}", "[" or "]".
     *
     * @return whether a block shall be printed with marks
     */
    protected boolean doPrintBlockMarks() {
        return true;
    }

    /**
     * Will create a {@link StringBuilder} for writing {@link JsonObject} as a json string.
     *
     * @param jsonObject to write
     * @param strategy to use
     * @return string builder for writing json object as a string
     */
    protected StringBuilder build(JsonObject jsonObject, @Nullable JsonWriterStrategy strategy) {
        return block("{", "}", jsonObject.entries(), strategy);
    }

    /**
     * Will create a {@link StringBuilder} for writing {@link JsonArray} as a json string.
     *
     * @param jsonArray to write
     * @param strategy to use
     * @return string builder for writing json array as a string
     */
    protected StringBuilder build(JsonArray jsonArray, @Nullable JsonWriterStrategy strategy) {
        return block("[", "]", jsonArray.getElements(), strategy);
    }

    /**
     * Will create a {@link StringBuilder} for writing {@link JsonTuple} as a json string.
     *
     * @param jsonTuple to write
     * @return string builder for writing json tuple as a string
     */
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

    /**
     * Will create a {@link StringBuilder} for writing {@link JsonString} as a json string.
     *
     * @param jsonString to write
     * @return string builder for writing json string as a string
     */
    protected StringBuilder build(JsonString jsonString, @Nullable JsonWriterStrategy strategy) {
        return jsonString(jsonString, strategy);
    }

    /**
     * Will create a {@link StringBuilder} for writing any {@link JsonElement} as a json string.
     * Delegates the building to other build methods.
     *
     * @param jsonElement to write
     * @return string builder for writing json element as a string
     */
    protected StringBuilder build(JsonElement jsonElement, @Nullable JsonWriterStrategy strategy) {
        if (jsonElement instanceof JsonObject) {
            return build((JsonObject) jsonElement, strategy);
        } if (jsonElement instanceof JsonArray) {
            return build((JsonArray) jsonElement, strategy);
        } if (jsonElement instanceof JsonString) {
            return build((JsonString) jsonElement, strategy);
        } if (jsonElement instanceof JsonTuple) {
            return build((JsonTuple) jsonElement);
        } else if (jsonElement instanceof JsonLong)  {
            return jsonLong((JsonLong) jsonElement, strategy);
        } else if (jsonElement instanceof JsonDouble)  {
            return jsonDouble((JsonDouble) jsonElement, strategy);
        } else if (jsonElement instanceof JsonNull)  {
            return jsonNull((JsonNull) jsonElement, strategy);
        } else if (jsonElement instanceof JsonBoolean)  {
            return jsonBoolean((JsonBoolean) jsonElement, strategy);
        } else {
            throw new RuntimeException("Does not support writing json element " + jsonElement.getClass().getSimpleName());
        }
    }

    /**
     * Will write a json block with indention if configured.
     * A json block can either be created for a {@link JsonObject} or a {@link JsonArray}.
     *
     * @param startMark to use. E.g. "{" or "["
     * @param endMark to use. E.g. "}" or "]"
     * @param elements to write into the block
     * @param strategy to apply
     * @return string builder for writing the json elements as a block
     */
    protected StringBuilder block(String startMark, String endMark, Collection<? extends JsonElement> elements, @Nullable JsonWriterStrategy strategy) {
        StringBuilder sb = new StringBuilder();
        String indent = "";
        if (doPrintBlockMarks()) {
            indent = startIndention();
            sb.append(startMark).append(lineBreak);
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
        if (doPrintBlockMarks()) sb.append(endMark);

        return sb;
    }

    /**
     * Will calculate the current indention for blocks.
     *
     * @return string with the amount of spaces needed for indention
     */
    protected String indention() {
        return StringUtil.repeat(' ', Math.max(0, printDepth));
    }

    /**
     * Begins an indention.
     *
     * @return string with the amount of spaces needed for the beginning indention
     */
    protected String startIndention() {
        depth++;
        printDepth += indent;
        return indention();
    }

    /**
     * Ends an indention.
     *
     * @return string with the amount of spaces needed for the ending indention
     */
    protected String endIndention() {
        depth--;
        printDepth -= this.indent;
        return indention();
    }

    /**
     * Will create a {@link StringBuilder} for writing any {@link JsonElement} as a json string.
     *
     * @param json to write
     * @param strategy to use
     * @return string builder for writing a json element as a string
     */
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

    /**
     * Will create a {@link StringBuilder} for writing {@link JsonBoolean} as a json string.
     *
     * @param jsonBoolean to write
     * @param strategy not used
     * @return string builder for writing json boolean as a string
     */
    @NotNull
    protected StringBuilder jsonBoolean(JsonBoolean jsonBoolean, @Nullable JsonWriterStrategy strategy) {
        return new StringBuilder(jsonBoolean.toString());
    }

    /**
     * Will create a {@link StringBuilder} for writing {@link JsonDouble} as a json string.
     *
     * @param jsonDouble to write
     * @param strategy not used
     * @return string builder for writing json double as a string
     */
    @NotNull
    protected StringBuilder jsonDouble(JsonDouble jsonDouble, @Nullable JsonWriterStrategy strategy) {
        return new StringBuilder(jsonDouble.toString());
    }

    /**
     * Will create a {@link StringBuilder} for writing {@link JsonLong} as a json string.
     *
     * @param jsonLong to write
     * @param strategy not used
     * @return string builder for writing json long as a string
     */
    @NotNull
    protected StringBuilder jsonLong(JsonLong jsonLong, @Nullable JsonWriterStrategy strategy) {
        return new StringBuilder(jsonLong.toString());
    }

    /**
     * Will create a {@link StringBuilder} for writing {@link JsonNull} as a json string.
     *
     * @param jsonNull to write
     * @param strategy not used
     * @return string builder for writing json null as a string
     */
    @NotNull
    protected StringBuilder jsonNull(JsonNull jsonNull, @Nullable JsonWriterStrategy strategy) {
        return new StringBuilder(jsonNull.toString());
    }
}
