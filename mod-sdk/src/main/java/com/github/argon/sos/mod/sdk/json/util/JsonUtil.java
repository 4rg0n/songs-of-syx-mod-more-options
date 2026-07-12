package com.github.argon.sos.mod.sdk.json.util;

import com.github.argon.sos.mod.sdk.json.parser.JsonParser;
import com.github.argon.sos.mod.sdk.util.MethodUtil;
import com.github.argon.sos.mod.sdk.util.StringUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Various methods to deal with the game json format.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtil {
    /**
     * This exists to make it easier for the {@link JsonParser} to process the games json format.
     *
     * @param jsonString game json to normalize
     * @return normalized game json string
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

        return jsonString;
    }

    /**
     * Creates a key name from a method name.
     *
     * @param method to create the name from
     * @return e.g. a method name like getBioFile will be BIO_FILE
     */
    public static String toJsonEKey(Method method) {
        String name = MethodUtil.extractSetterGetterFieldName(method);
        return StringUtil.toScreamingSnakeCase(name);
    }
}
