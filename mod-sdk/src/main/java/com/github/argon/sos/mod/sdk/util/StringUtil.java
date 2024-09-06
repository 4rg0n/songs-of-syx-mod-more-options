package com.github.argon.sos.mod.sdk.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;
import snake2d.util.sprite.text.Str;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtil {

    public static String toString(Object[] objects) {
        return Arrays.toString(objects);
    }

    public static String toString(Map<?, ?> map) {
        return map.keySet().stream()
            .map(key -> key + "=" + map.get(key))
            .collect(Collectors.joining(", ", "{", "}"));
    }

    public static String removeTrailing(final String str, String toRemove) {
        if (!str.endsWith(toRemove)) {
            return str;
        }

        return str.substring(0, str.length() - toRemove.length());
    }

    public static String unCapitalize(String text) {
        if (text.isEmpty()) {
            return "";
        }

        if (text.length() == 1) {
            return "" + Character.toLowerCase(text.charAt(0));
        }

        return Character.toLowerCase(text.charAt(0)) + text.substring(1);
    }

    public static String extractTail(String text, String delimiterRegex) {
        String[] split = text.split(delimiterRegex);

        if (split.length == 0) {
            return text;
        }

        return split[split.length - 1];
    }

    public static String toStringPrimitiveArray(@Nullable Object arg) {
        if (arg == null) {
            return "null";
        } else if (arg instanceof int[]) {
            return Arrays.toString((int[]) arg);
        } else if (arg instanceof long[]) {
            return Arrays.toString((long[]) arg);
        } else if (arg instanceof short[]) {
            return Arrays.toString((short[]) arg);
        } else if (arg instanceof double[]) {
            return Arrays.toString((double[]) arg);
        } else if (arg instanceof float[]) {
            return Arrays.toString((float[]) arg);
        } else if (arg instanceof byte[]) {
            return Arrays.toString((byte[]) arg);
        } else if (arg instanceof boolean[]) {
            return Arrays.toString((boolean[]) arg);
        } else if (arg instanceof char[]) {
            return Arrays.toString((char[]) arg);
        }

        return arg.toString();
    }

    public static Object[] stringifyValues(Object[] args) {
        Object[] stringArgs = new Object[args.length];

        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            stringArgs[i] = stringify(arg);
        }

        return stringArgs;
    }

    public static String stringify(@Nullable Object arg) {
        if (arg == null) {
            return "null";
        } else if (arg instanceof String) {
            return (String) arg;
        } if (arg instanceof Double) {
            return String.format("%1$,.4f", (Double) arg);
        } else if (arg instanceof Map) {
            return StringUtil.toString((Map<?, ?>) arg);
        } else if (arg instanceof Object[]) {
            return StringUtil.toString((Object[]) arg);
        } else if (arg.getClass().isArray()) {
            return StringUtil.toStringPrimitiveArray(arg);
        } else {
            return arg.toString();
        }
    }

    public static String stringify(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);

        return stringWriter.toString();
    }

    public static String shortenClassName(Class<?> clazz) {
        return shortenPackageName(clazz.getPackage().getName()) + '.' + clazz.getSimpleName();
    }

    public static String shortenPackageName(String packageName) {
        return Arrays.stream(packageName.split("\\."))
            .map(segment -> Character.toString(segment.charAt(0)))
            .collect(Collectors.joining("."));
    }

    public static String cutOrFill(String string, int maxLength, boolean cutTail) {
        if (string.length() == maxLength) {
            return string;
        }

        if (!cutTail && string.length() > maxLength) {
            return string.substring(string.length() - maxLength);
        } else if (cutTail && string.length() > maxLength) {
            return string.substring(0, maxLength);
        }

        int spaceLength = maxLength - string.length();
        String spacesString = repeat(' ', spaceLength);
        return string + spacesString;
    }

    public static String repeat(char character, int length) {
        char[] chars = new char[length];
        Arrays.fill(chars, character);

        return new String(chars);
    }

    public static String capitalize(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    public static String unwrap(String string, char prefix, char suffix) {
        if (string.isEmpty()) {
            return string;
        }

        if (string.charAt(0) == prefix && string.charAt(string.length() - 1) == suffix) {
            string = string.substring(1, string.length() - 1);
        }

        return string;
    }

    public static List<String> quote(List<String> strings) {
        return strings.stream()
            .map(StringUtil::quote)
            .collect(Collectors.toList());
    }

    public static String quote(String string) {
        return "\"" + string + "\"";
    }

    public static String unquote(String string) {
        return unwrap(string, '"', '"');
    }

    public static String toScreamingSnakeCase(String text) {
        if (text.isEmpty()) {
            return text;
        }

        return text.replaceAll("\\B([A-Z])", "_$1").toUpperCase();
    }

    /**
     * Replaces tokens like {0} {1} etc. in a string with the given argument on that place
     */
    public static String replaceTokens(String template, Object... args) {
        if (args.length == 0 || !template.contains("{")) {
            return template;
        }

        Str tmp = Str.TMP.clear().add(template);
        for (int i = 0, argsLength = args.length; i < argsLength; i++) {
            Object arg = args[i];
            tmp.insert(i, stringify(arg));
        }

        return tmp.toString();
    }

    public static String removeFileExtension(String fileName) {
        String[] parts = fileName.split("\\.");

        if (parts.length < 2) {
            return fileName;
        }

        return Arrays.stream(parts, 0, parts.length - 1)
            .collect(Collectors.joining("."));
    }

    public static String removeBeginning(String resource, String startString) {
        if (resource.startsWith(startString)) {
            return resource.substring(startString.length());
        }

        return resource;
    }

    public static String wrap(String string, String wrap) {
        return wrap + string + wrap;
    }

    private static Pattern numericPattern = Pattern.compile("-?\\d+(\\.\\d+)?");
    private static Pattern integerPattern = Pattern.compile("-?\\d+?");

    public static int countLines(@Nullable String string) {
        if (string == null) {
            return 0;
        }

        Matcher m = Pattern.compile("\r\n|\r|\n").matcher(string);
        int lines = 1;
        while (m.find())
        {
            lines ++;
        }

        return lines;
    }

    public static int countChar(@Nullable String string, char character) {
        int count = 0;
        if (string == null) {
            return count;
        }

        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == character) {
                count++;
            }
        }

        return count;
    }

    public static boolean isNumeric(@Nullable String strNum) {
        if (strNum == null || strNum.isEmpty()) {
            return false;
        }

        return numericPattern.matcher(strNum).matches();
    }

    public static boolean isNumeric(@Nullable Character character) {
        if (character == null) {
            return false;
        }

        return isInteger(character.toString());
    }

    public static boolean isInteger(@Nullable String strNum) {
        if (strNum == null) {
            return false;
        }
        return integerPattern.matcher(strNum).matches();
    }
}
