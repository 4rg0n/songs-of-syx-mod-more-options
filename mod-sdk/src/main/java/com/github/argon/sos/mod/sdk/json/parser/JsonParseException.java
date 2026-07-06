package com.github.argon.sos.mod.sdk.json.parser;

import com.github.argon.sos.mod.sdk.json.Json;
import com.github.argon.sos.mod.sdk.util.StringUtil;

/**
 * Exception thrown from within the "json.parser" namespace.
 */
public class JsonParseException extends RuntimeException {
    /**
     * @see RuntimeException#RuntimeException()
     */
    public JsonParseException() {
    }

    /**
     * @see RuntimeException#RuntimeException(String)
     */
    public JsonParseException(String message) {
        super(message);
    }

    /**
     * @see RuntimeException#RuntimeException(String, Throwable)
     */
    public JsonParseException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @see RuntimeException#RuntimeException(Throwable)
     */
    public JsonParseException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new {@link JsonParseException} with the faulty json as information.
     *
     * @param json containing the faulty json string
     * @param cause thrown cause exception
     */
    public JsonParseException(Json json, Throwable cause) {
        super(error(json), cause);
    }

    /**
     * Creates a descriptive error message with the faulty json part.
     *
     * @param json containing the faulty json
     * @return error message
     */
    private static String error(Json json) {
        String rawJson = json.getRawJson();
        if (rawJson.isEmpty()) {
            return "NO JSON CONTENT!";
        }

        try {
            int index = json.getIndex();
            char currentChar = json.currentChar();

            String errorMsg = "Error parsing json content "
                + "at character '" + currentChar + "' "
                + "at index " + index + " "
                + "on line " + (StringUtil.countChar(rawJson.substring(0, index), '\n') + 1) + ":";
            int radius = 10;
            int cursorPos = radius;

            int start = index - radius;
            if (start < 0) {
                start = 0;
                cursorPos = cursorPos - index;
            }

            int end = index + radius;
            if (end > rawJson.length() - 1) {
                end = rawJson.length() - 1;
            }

            int length = end - start;

            if (length <= 0) {
                return "NO JSON CONTENT!";
            }

            String text = rawJson.substring(start, end)
                .replaceAll("\\n", " ")
                .replaceAll("\\t", " ");

            return errorMsg + "\n" + text + "\n" + StringUtil.repeat(' ', cursorPos) + "^";
        } catch (Exception e) {
            e.printStackTrace();

            return "";
        }
    }
}
