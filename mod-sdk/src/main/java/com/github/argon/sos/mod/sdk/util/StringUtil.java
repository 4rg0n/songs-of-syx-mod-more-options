package com.github.argon.sos.mod.sdk.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;
import snake2d.util.sprite.text.Str;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtil {

    private static final Pattern numericPattern = Pattern.compile("-?\\d+(\\.\\d+)?");
    private static final Pattern integerPattern = Pattern.compile("-?\\d+?");
    private static final Pattern decimalPattern = Pattern.compile("-?\\d+\\.\\d+?");

    /**
     * Transforms multiple objects into a string.
     *
     * @param objects to transform into a string
     * @return build string
     */
    public static String toString(Object[] objects) {
        return Arrays.toString(objects);
    }

    /**
     * Transforms a key value map into a string.
     *
     * @param map to transform into a string
     * @return build string
     */
    public static String toString(Map<?, ?> map) {
        return map.keySet().stream()
            .map(key -> key + "=" + map.get(key))
            .collect(Collectors.joining(", ", "{", "}"));
    }

    /**
     * Removes a given part from the end of a string
     *
     * @param string to remove from the end
     * @param toRemove the part to remove
     * @return string with removed part
     */
    public static String removeTrailing(final String string, String toRemove) {
        if (!string.endsWith(toRemove)) {
            return string;
        }

        return string.substring(0, string.length() - toRemove.length());
    }

    /**
     * @return e.g. Test to test
     */
    public static String unCapitalize(String text) {
        if (text.isEmpty()) {
            return text;
        }

        if (text.length() == 1) {
            return "" + Character.toLowerCase(text.charAt(0));
        }

        return Character.toLowerCase(text.charAt(0)) + text.substring(1);
    }

    /**
     * Extracts the last part of a segmented string.
     * E.g. for "test.foo.bar" and "\\." as delimiter it would return "bar".
     *
     * @param text to extract the last part from
     * @param delimiterRegex to split the text
     * @return last part split by the delimiter
     */
    public static String extractTail(String text, String delimiterRegex) {
        String[] split = text.split(delimiterRegex);

        if (split.length == 0) {
            return text;
        }

        return split[split.length - 1];
    }

    /**
     * Will transform primitive array types into strings.
     *
     * @param arg the primitive array
     * @return built string
     */
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

    /**
     * Will transform an array of any object to a string.
     *
     * @param args array with object to transform
     * @return built string
     */
    public static Object[] stringifyValues(Object[] args) {
        Object[] stringArgs = new Object[args.length];

        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            stringArgs[i] = stringify(arg);
        }

        return stringArgs;
    }

    /**
     * Will transform a single value into a string.
     *
     * @param arg to transform
     * @return built string
     */
    public static String stringify(@Nullable Object arg) {
        if (arg == null) {
            return "null";
        } else if (arg instanceof String) {
            return (String) arg;
        } if (arg instanceof Double) {
            return String.format(Locale.US, "%.4f", (Double) arg);
        } else if (arg instanceof Float) {
            return String.format(Locale.US, "%.4f", (Float) arg);
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

    /**
     * Will transform a {@link Throwable} into a string with stacktrace.
     *
     * @param throwable to transform
     * @return built string
     */
    public static String stringify(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);

        return stringWriter.toString();
    }

    /**
     * Cuts a given text to a max length or fills it up with spaces to the max length.
     *
     *
     * @param string to cut to max length or fill with spaces
     * @param maxLength to fill or cut
     * @param cutTail whether to cut from the start or the end
     * @return cur ot filled string
     */
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

    /**
     * E.g. 'a' with amount 3 would be "aaa".
     *
     * @param character to repeat
     * @param amount how often to repeat
     * @return repeated character
     */
    public static String repeat(char character, int amount) {
        char[] chars = new char[amount];
        Arrays.fill(chars, character);

        return new String(chars);
    }

    /**
     * E.g. "test" will be "Test"
     *
     * @param text to make the first character uppercase
     * @return text with first character uppercase
     */
    public static String capitalize(String text) {
        if (text.isEmpty()) {
            return text;
        }

        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    /**
     * Removes certain parts in front and behind a string.
     * E.g. "{test}", "{", "}" would be "test".
     *
     * @param string to remove from
     * @param prefix to remove from start
     * @param suffix to remove from end
     * @return string with removed parts
     */
    public static String unwrap(String string, char prefix, char suffix) {
        if (string.isEmpty()) {
            return string;
        }

        if (string.charAt(0) == prefix) {
            string = string.substring(1);
        }

        if (string.charAt(string.length() - 1) == suffix) {
            string = string.substring(0, string.length() - 1);
        }

        return string;
    }

    /**
     * Will put a list of strings in ""
     *
     * @param strings to quote
     * @return list with quoted strings
     */
    public static List<String> quote(List<String> strings) {
        return strings.stream()
            .map(StringUtil::quote)
            .collect(Collectors.toList());
    }

    /**
     * Will put a string in ""
     *
     * @param string to quote
     * @return quoted string
     */
    public static String quote(String string) {
        return "\"" + string + "\"";
    }

    /**
     * Will remove "" from a string when present
     *
     * @param string to remove quotes from
     * @return string with removed quotes
     */
    public static String unquote(String string) {
        return unwrap(string, '"', '"');
    }

    /**
     * Will transform "camelCase" to "CAMEL_CASE"
     *
     * @param text to transform
     * @return text written in screaming snake case
     */
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

    /**
     * Cuts a file extension from a name.
     * E.g. "test.txt" will be "test".
     *
     * @param fileName to cut the extension from
     * @return file name without extension
     */
    public static String removeFileExtension(String fileName) {
        String[] parts = fileName.split("\\.");

        if (parts.length < 2) {
            return fileName;
        }

        return Arrays.stream(parts, 0, parts.length - 1)
            .collect(Collectors.joining("."));
    }

    /**
     * Will remove a certain part of the string from the start.
     *
     * @param string to remove from
     * @param toRemove part to remove at the start
     * @return string with removed part
     */
    public static String removeBeginning(String string, String toRemove) {
        if (string.startsWith(toRemove)) {
            return string.substring(toRemove.length());
        }

        return string;
    }

    /**
     * Will put a string between the given "wrap".
     * E.g. "test", "'" would be "'test'"
     *
     * @param string to wrap around
     * @param wrap to put around the string
     * @return wrapped string
     */
    public static String wrap(String string, String wrap) {
        return wrap + string + wrap;
    }

    /**
     * Will count the lines of string.
     *
     * @param string to count lines
     * @return number of lines
     */
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

    /**
     * Will count the appearance of a certain character in a string.
     *
     * @param string to look into
     * @param character to count
     * @return amount of characters in string
     */
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

    /**
     * @return whether the given string is a number
     */
    public static boolean isNumeric(@Nullable String strNum) {
        if (strNum == null || strNum.isEmpty()) {
            return false;
        }

        return numericPattern.matcher(strNum).matches();
    }

    /**
     * @return whether the given character is a number
     */
    public static boolean isNumeric(@Nullable Character character) {
        if (character == null) {
            return false;
        }

        return isInteger(character.toString());
    }

    /**
     * @return whether the given character is an integer
     */
    public static boolean isInteger(@Nullable String strNum) {
        if (strNum == null) {
            return false;
        }
        return integerPattern.matcher(strNum).matches();
    }

    /**
     * @return whether the given string is a decimal number
     */
    public static boolean isDecimal(@Nullable String strNum) {
        if (strNum == null) {
            return false;
        }
        return decimalPattern.matcher(strNum).matches();
    }
}
