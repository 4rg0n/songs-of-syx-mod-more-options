package com.github.argon.sos.moreoptions.json.writer;

import com.github.argon.sos.moreoptions.json.element.*;
import com.github.argon.sos.moreoptions.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.StringJoiner;

public abstract class AbstractJsonWriter implements JsonWriter {

    /**
     * Used internally for knowing how far to indent
     */
    protected int printDepth;

    protected boolean trailingComma;

    protected int depth;

    protected int indent;

    protected String lineBreak = "";

    public AbstractJsonWriter(boolean trailingComma, int indent) {
        this.trailingComma = trailingComma;
        this.indent = indent;
        this.lineBreak = "\n";
    }

    public String write(JsonElement json) {
        String jsonString = build(json).toString();
        printDepth = 0;

        return jsonString;
    }

    protected boolean doPrintBlockMarks() {
        return true;
    }

    protected StringBuilder build(JsonObject jsonObject) {
        return block("{", "}", jsonObject.entries());
    }

    protected StringBuilder build(JsonArray jsonArray) {
        return block("[", "]", jsonArray.getElements());
    }
    protected StringBuilder build(JsonTuple jsonTuple) {
        StringBuilder sb = new StringBuilder();
        sb.append(jsonTuple.getKey()).append(": ");

        JsonElement value = jsonTuple.getValue();
        sb.append(build(value));

        return sb;
    }

    protected StringBuilder build(JsonString json) {
        return jsonString(json);
    }

    protected StringBuilder build(JsonElement json) {
        if (json instanceof JsonObject) {
            return build((JsonObject) json);
        } if (json instanceof JsonArray) {
            return build((JsonArray) json);
        } if (json instanceof JsonString) {
            return build((JsonString) json);
        } if (json instanceof JsonTuple) {
            return build((JsonTuple) json);
        } else if (json instanceof JsonLong)  {
            return jsonString(json);
        } else if (json instanceof JsonDouble)  {
            return jsonString(json);
        } else if (json instanceof JsonNull)  {
            return jsonString(json);
        } else if (json instanceof JsonBoolean)  {
            return jsonString(json);
        } else {
            throw new RuntimeException("Does not support json element " + json.getClass().getSimpleName());
        }
    }

    protected StringBuilder block(String start, String end, Collection<? extends JsonElement> elements) {
        StringBuilder sb = new StringBuilder();
        String indent = "";
        if (doPrintBlockMarks()) {
            indent = startIndention();
            sb.append(start).append(lineBreak);
        } else {
            depth++;
        }


        String comma = "";
        if (trailingComma) {
            comma = ",";
        }

        StringJoiner stringJoiner = new StringJoiner(comma + lineBreak);
        for (JsonElement element : elements) {
            StringBuilder elementBuilder = new StringBuilder();
            elementBuilder
                .append(indent)
                .append(build(element));

            stringJoiner.add(elementBuilder);
        }

        sb.append(stringJoiner);
        sb.append(comma).append(lineBreak);

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
    protected StringBuilder jsonString(JsonElement json) {
        return new StringBuilder(json.toString());
    }
}
