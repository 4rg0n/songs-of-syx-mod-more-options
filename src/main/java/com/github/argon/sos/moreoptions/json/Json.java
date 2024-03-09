package com.github.argon.sos.moreoptions.json;

import com.github.argon.sos.moreoptions.json.element.JsonElement;
import com.github.argon.sos.moreoptions.json.element.JsonObject;
import com.github.argon.sos.moreoptions.json.util.JsonUtil;
import lombok.Getter;

/**
 * Can process and produce the games json format.
 * Can also process and produce standard json.
 *
 * <pre>{@code
 *     // standard JSON
 *     String jsonString = "{\"TEST\": 1}";
 *     JsonObject jsonObject = new Json(jsonString).getRoot();
 *
 *     // game JSON
 *     String jsonString = "TEST: 1,";
 *     JsonObject jsonObject = new Json(jsonString).getRoot();
 * }</pre>
 */
public class Json {
    private final String rawJson;

    /**
     * For parsing the json with {@link JsonParser}.
     * Points to the current character in {@link this#rawJson} to parse.
     */
    @Getter
    private int index;

    /**
     * Contains the parsed {@link JsonObject}, which can be mapped into an object via the {@link JsonMapper}
     */
    @Getter
    private final JsonObject root;

    /**
     * For writing the {@link JsonElement}s to a json string
     */
    @Getter
    private final JsonWriter writer;

    public Json(JsonElement root) {
        this(root, new JsonWriter());
    }

    public Json(JsonElement root, JsonWriter writer) {
        if (!(root instanceof JsonObject)) {
            throw new IllegalArgumentException("Given element must be of type " + JsonObject.class.getName());
        }

        this.root = (JsonObject) root;
        this.writer = writer;
        this.rawJson = "";
    }

    public Json(JsonObject root) {
        this(root, new JsonWriter());
    }

    public Json(JsonObject root, JsonWriter writer) {
        this.root = root;
        this.writer = writer;
        this.rawJson = "";
    }

    public Json(String rawJson) {
        this(rawJson, new JsonWriter());
    }

    public Json(String rawJson, JsonWriter writer) {
        this.rawJson = JsonUtil.normalizeJson(rawJson.trim());
        this.writer = writer;
        this.index = 0;
        this.root = (JsonObject) JsonParser.parse(this);
    }

    /**
     * For parsing the {@link this#rawJson}
     *
     * @return current character the index is pointing at
     */
    public char currentChar(){
        return rawJson.charAt(index);
    }

    /**
     * For parsing the {@link this#rawJson}
     * Moves the index to the next character.
     */
    public void indexMove(){
        this.index++;
    }

    /**
     * Moves the index until the next non whitespace character.
     * See {@link this#isWhiteSpace(char)}
     */
    public void skipBlank(){
        while (!atEnd() && isWhiteSpace(currentChar())){
            indexMove();
        }
    }

    /**
     * Returns the string until a stop character is reached.
     * See {@link this#isEndOfValue(char)}
     */
    public String getNextValue() {
        StringBuilder sb = new StringBuilder();
        while (true) {
            char c = currentChar();
            if (isEndOfValue(c))
                break;
            sb.append(c);
            indexMove();
        }
        return sb.toString();
    }

    /**
     * Returns the string until the given character is reached.
     *
     * @param stopChar marking the end
     */
    public String getNextValue(char stopChar) {
        StringBuilder sb = new StringBuilder();
        while (true) {
            char c = currentChar();
            if (c == stopChar)
                break;
            sb.append(c);
            indexMove();
        }
        return sb.toString();
    }

    /**
     * @return whether the end of the json string is reached
     */
    public boolean atEnd(){
        return getIndex() == rawJson.length();
    }

    private boolean isEndOfValue(char c) {
        return isWhiteSpace(c) || c == ',' || c == '}' || c == ']';
    }

    private boolean isWhiteSpace(char c) {
        return (c == ' '
            || c=='\n'
            || c=='\t'
            || c=='\f'
            || c=='\r'
        );
    }

    /**
     * Dependent on set {@link JsonWriter}, will transform the {@link JsonElement}s to a human-readable string.
     *
     * @return objects as readable json string
     */
    @Override
    public String toString() {
        return writer.write(this.getRoot());
    }
}
