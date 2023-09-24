package com.github.argon.sos.moreoptions.util;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class StringUtil {

    public static String toString(Object[] objects) {
        return Arrays.toString(objects);
    }

    public static String toString(Map<?, ?> map) {
        return map.keySet().stream()
            .map(key -> key + "=" + map.get(key))
            .collect(Collectors.joining(", ", "{", "}"));
    }

    public static String unCapitalize(String text) {
        return Character.toLowerCase(text.charAt(0)) + text.substring(1);
    }

    public static String toStringPrimitiveArray(Object arg) {
        if (arg instanceof int[]) {
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
            stringArgs[i] = stringifyValue(arg);
        }

        return stringArgs;
    }

    public static String stringifyValue(Object arg) {
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

    public static String shortenName(Class<?> clazz) {
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
}
