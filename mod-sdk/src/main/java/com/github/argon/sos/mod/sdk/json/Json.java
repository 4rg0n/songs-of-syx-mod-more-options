package com.github.argon.sos.mod.sdk.json;

import com.github.argon.sos.mod.sdk.json.element.JsonElement;
import com.github.argon.sos.mod.sdk.json.element.JsonObject;
import com.github.argon.sos.mod.sdk.json.parser.JsonParser;
import com.github.argon.sos.mod.sdk.json.util.JsonUtil;
import com.github.argon.sos.mod.sdk.json.writer.JsonWriter;
import com.github.argon.sos.mod.sdk.json.writer.JsonWriters;
import lombok.Getter;
import lombok.Setter;

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
    @Getter
    private final String rawJson;

    /**
     * For parsing the json with {@link JsonParser}.
     * Points to the current character in {@link Json#rawJson} to parse.
     */
    @Setter
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

    /**
     * Creates a new {@link Json} instance with given {@link JsonElement} as content.
     * Will use {@code JsonWriters.gameJsonUnquotedPretty()} as writer.
     *
     * @param root to use as content
     */
    public Json(JsonElement root) {
        this(root, JsonWriters.gameJsonUnquotedPretty());
    }

    /**
     * Creates a new {@link Json} instance with given {@link JsonElement} as content.
     *
     * @param root to use as content
     * @param writer used for writing content as string
     */
    public Json(JsonElement root, JsonWriter writer) {
        if (!(root instanceof JsonObject)) {
            throw new IllegalArgumentException("Given element must be of type " + JsonObject.class.getName());
        }

        this.root = (JsonObject) root;
        this.writer = writer;
        this.rawJson = "";
    }

    /**
     * Creates a new {@link Json} instance with given {@link JsonObject} as content.
     * Will use {@code JsonWriters.gameJsonUnquotedPretty()} as writer.
     *
     * @param root to use as content
     */
    public Json(JsonObject root) {
        this(root, JsonWriters.gameJsonUnquotedPretty());
    }

    /**
     * Creates a new {@link Json} instance with given {@link JsonElement} as content.
     *
     * @param root to use as content
     * @param writer used for writing content as string
     */
    public Json(JsonObject root, JsonWriter writer) {
        this.root = root;
        this.writer = writer;
        this.rawJson = "";
    }

    /**
     * Creates a new {@link Json} instance with given raw json parsed as content.
     * Will use {@code JsonWriters.gameJsonUnquotedPretty()} as writer.
     *
     * @param rawJson to use as content
     */
    public Json(String rawJson) {
        this(rawJson, JsonWriters.gameJsonUnquotedPretty());
    }

    /**
     * Creates a new {@link Json} instance with given raw json parsed as content.
     *
     * @param rawJson to use as content
     * @param writer used for writing content as string
     */
    public Json(String rawJson, JsonWriter writer) {
        this.rawJson = JsonUtil.normalizeGameJson(rawJson.trim());
        this.writer = writer;
        this.index = 0;
        this.root = (JsonObject) JsonParser.parse(this);
    }

    /**
     * For parsing the {@link Json#rawJson}
     *
     * @return the current character the index is pointing at
     */
    public char currentChar(){
        return rawJson.charAt(index);
    }

    /**
     * For parsing the {@link Json#rawJson}
     * Moves the index to the next character.
     */
    public void indexMove(){
        this.index++;
    }

    /**
     * Moves the index until the next non whitespace character.
     * See {@link Json#isWhiteSpace(char)}
     */
    public void skipBlank(){
        while (!atEnd() && isWhiteSpace(currentChar())){
            indexMove();
        }
    }

    /**
     * Returns the string until a stop character is reached.
     * See {@link Json#isEndOfValue(char)}
     *
     * @return the next value
     */
    public String getNextValue() {
        return getNextValue(false);
    }

    /**
     * Returns the string until a stop character is reached.
     * See {@link Json#isEndOfValue(char)}
     *
     * @param checkWhitespace whether whitespaces shall be included or not
     * @return the next value
     */
    public String getNextValue(boolean checkWhitespace) {
        StringBuilder sb = new StringBuilder();
        while (true) {
            char c = currentChar();
            if ((checkWhitespace && isWhiteSpace(c)) || isEndOfValue(c))
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
     * @return the next value
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
     * Checks whether end of content is reached.
     *
     * @return whether the end of the json string is reached
     */
    public boolean atEnd(){
        return getIndex() >= rawJson.length() - 1;
    }

    /**
     * Dependent on set {@link JsonWriter}, will transform the {@link JsonElement}s to a human-readable string.
     *
     * @return objects as readable json string
     */
    public String write() {
        return writer.write(this.getRoot());
    }

    private boolean isEndOfValue(char c) {
        return c == ',' || c == '}' || c == ']';
    }

    private boolean isWhiteSpace(char c) {
        return (c == ' '
            || c=='\n'
            || c=='\t'
            || c=='\f'
            || c=='\r'
        );
    }
}
