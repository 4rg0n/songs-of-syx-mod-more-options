package com.github.argon.sos.moreoptions.json.parser;

import com.github.argon.sos.moreoptions.json.Json;
import com.github.argon.sos.moreoptions.util.StringUtil;

public class JsonParseException extends RuntimeException {
    public JsonParseException() {
    }

    public JsonParseException(String message) {
        super(message);
    }

    public JsonParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonParseException(Throwable cause) {
        super(cause);
    }

    public JsonParseException(Json json, Exception e) {
        super(error(json), e);
    }

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
