package com.github.argon.sos.moreoptions.json.util;

import com.github.argon.sos.moreoptions.json.JsonParser;
import com.github.argon.sos.moreoptions.util.MethodUtil;
import com.github.argon.sos.moreoptions.util.StringUtil;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonUtil {
    /**
     * This exists to make it easier for the {@link JsonParser} to process the games json format
     */
    public static @Nullable String normalizeGameJson(@Nullable String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return jsonString;
        }

        // remove comments
        Pattern regex = Pattern.compile("^\\s*\\*\\*\\s*.*", Pattern.MULTILINE);
        Matcher regexMatcher = regex.matcher(jsonString);
        jsonString = regexMatcher.replaceAll("");

        if (jsonString.charAt(0) != '{') {
            // wrap everything in {}
            jsonString = "{" + jsonString + "}";
        }

        // remove trailing commas
        jsonString = jsonString.replaceAll("\\s*,\\s*}", "}");
        jsonString = jsonString.replaceAll("\\s*,\\s*]", "]");

        return jsonString;
    }

    public static @Nullable String toJsonKey(Method method) {
        String name = MethodUtil.extractSetterGetterFieldName(method);
        return StringUtil.toScreamingSnakeCase(name);
    }
}
