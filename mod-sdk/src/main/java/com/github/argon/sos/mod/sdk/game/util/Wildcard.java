package com.github.argon.sos.mod.sdk.game.util;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Songs of Syx uses a wildcard matching system in its configurations.
 * When there's a list of possible values, you can enter e.g. an "*" for all.
 * Or "ALCO*", for everything starting with the word "ALCO".
 */
@Getter
public class Wildcard {
    private final List<String> wildcardValues;
    private final List<String> originalValues;

    private final static String DELIMITER = "_";
    private final static String WILDCARD = "*";

    private Wildcard(List<String> values) {
        this.originalValues = new ArrayList<>(values);
        this.originalValues.add(WILDCARD);
        this.wildcardValues = buildWildcardValues(originalValues);
    }

    /**
     * Returns a list of matches from given texts to match.
     *
     * @param texts to match
     * @return matched texts
     */
    public List<String> matches(@Nullable List<String> texts) {
        if (texts == null) {
            return List.of();
        }

        return texts.stream()
            .flatMap(text -> matches(text).stream())
            .toList();
    }

    /**
     * Match a string against the list of values.
     * The text can contain a "*" (wildcard). 
     * A given text like "ALCO*" would e.g. match "ALCO_BEER" and "ALCO_WINE".
     *
     * @param text to match
     * @return list of matched values
     */
    public List<String> matches(@Nullable String text) {
        if (text == null) {
            return List.of();
        }

        if (text.length() > 1 && text.contains(WILDCARD)) {
            String regexMatch = text.replace(WILDCARD, ".*");
            Pattern pattern = Pattern.compile(Pattern.quote(regexMatch));

            return originalValues.stream()
                .filter(value -> {
                    Matcher matcher = pattern.matcher(value);
                    return matcher.matches();
                })
                .toList();
        } else {
            return originalValues.stream()
                .filter(value -> value.equals(text))
                .toList();
        }
    }

    /**
     * Creates a new {@link Wildcard} instance from given texts.
     *
     * @param texts to create wildcard with
     * @return created wildcard
     */
    public static Wildcard of(List<String> texts) {
        return new Wildcard(texts);
    }

    /**
     * Creates a new {@link Wildcard} instance from a given text.
     *
     * @param text to create wildcard with
     * @return created wildcard
     */
    public static Wildcard of(String text) {
        return new Wildcard(List.of(text));
    }

    private static List<String> buildWildcardValues(List<String> values) {
        return values.stream()
            .flatMap(value -> buildWildcardValues(value).stream())
            .toList();
    }

    /**
     * Will create multiple possible strings with an "*" at the end.
     * E.g. "ALCO_BEER" will result in "ALCO*" and "ALCO_BEER"
     *
     * @param value to build the wildcard values from
     * @return possible wildcard values
     */
    private static List<String> buildWildcardValues(String value) {
        List<String> values = new ArrayList<>();
        String[] parts = value.split(DELIMITER);
        String key = "";

        for (int i = 0, partsSize = parts.length; i < partsSize; i++) {
            String part = parts[i];

            if (part.isEmpty()) {
                key = DELIMITER;
                continue;
            }

            key = key + part;
            if (key.startsWith(DELIMITER) || i == partsSize - 1) {
                values.add(key);
            } else {
                values.add(key + WILDCARD);
            }

            key = key + DELIMITER;
        }

        return values;
    }
}
