package com.github.argon.sos.moreoptions.json;

import com.github.argon.sos.moreoptions.json.element.JsonArray;
import com.github.argon.sos.moreoptions.json.element.JsonElement;
import com.github.argon.sos.moreoptions.json.element.JsonObject;
import com.github.argon.sos.moreoptions.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import snake2d.util.file.JsonE;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * For printing {@link JsonElement}s as string
 */
public class JsonWriter {

    /**
     * Used internally for knowing how far to indent
     */
    private int printDepth;

    /**
     * Will quote keys in "" when true
     */
    @Getter @Setter
    private boolean quoteKeys = true;

    /**
     * Will print with indents and line breaks when true
     */
    @Getter @Setter
    private boolean prettyPrint = true;

    /**
     * Will print with trailing when true
     */
    @Getter @Setter
    private boolean trailingComma = false;

    /**
     * Will wrap the root element in {} when true
     */
    @Getter @Setter
    private boolean rootBraces = true;

    /**
     * @return writer that prints json the same as the games {@link JsonE}
     */
    public static JsonWriter getJsonE() {
        return new JsonWriter(false, true, true, false);
    }

    public JsonWriter() {
    }

    public JsonWriter(boolean quoteKeys, boolean prettyPrint, boolean trailingComma, boolean rootBraces) {
        this.quoteKeys = quoteKeys;
        this.prettyPrint = prettyPrint;
        this.trailingComma = trailingComma;
        this.rootBraces = rootBraces;
    }

    /**
     * Converts given {@link JsonElement} to a readable string
     *
     * @return readable json string
     */
    public String write(JsonElement json) {
        String jsonString = toJsonString(json);
        printDepth = 0;

        // remove braces to be compliant with game json format?
        if (!rootBraces) {
            jsonString = StringUtil.unwrap(jsonString, '{', '}');
        }

        // add trailing comma?
        if (trailingComma) {
            jsonString += ",";
        }

        return jsonString;
    }

    private String toJsonString(JsonElement json) {
        if (json instanceof JsonObject) {
            JsonObject jsonObject = (JsonObject) json;

            if (prettyPrint) {
                return prettyPrint(jsonObject);
            } else {
                return print(jsonObject);
            }
        } if (json instanceof JsonArray) {
            JsonArray jsonArray = (JsonArray) json;

            return print(jsonArray);
        } else {
            return json.toString();
        }
    }

    private String print(JsonObject jsonObject) {
        String suffix = "}";
        if (trailingComma) {
            suffix = ",}";
        }

        Map<String, JsonElement> map = jsonObject.getMap();

        return map.keySet().stream()
            .map(key -> (quoteKeys) ? StringUtil.quote(key) : key + ": " + toJsonString(map.get(key)))
            .collect(Collectors.joining(", ", "{", suffix));
    }

    private String prettyPrint(JsonObject jsonObject) {
        printDepth += 2;
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");

        boolean isFirst = true;
        for (String key : jsonObject.getMap().keySet()) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(",\n");
            }
            String indent = StringUtil.repeat(' ', Math.max(0, printDepth));
            sb.append(indent);

            // quote keys?
            if (quoteKeys) {
                sb.append("\"").append(key).append("\"").append(": ");
            } else {
                sb.append(key).append(": ");
            }

            JsonElement value = jsonObject.getMap().get(key);
            sb.append(toJsonString(value));
        }
        sb.append('\n');
        String indent = StringUtil.repeat(' ', Math.max(0, printDepth - 2));
        sb.append(indent);

        // add trailing comma?
        if (trailingComma) {
            sb.append(",");
        }
        sb.append("}");

        printDepth -= 2;
        return sb.toString();
    }

    private String print(JsonArray jsonArray) {
        List<JsonElement> elements = jsonArray.getElements();
        String suffix = "]";

        // add trailing comma?
        if (trailingComma) {
            suffix = ",]";
        }

        return elements.stream()
            .map(this::toJsonString)
            .collect(Collectors.joining(", ", "[", suffix));
    }
}
