package com.github.argon.sos.mod.sdk.game;

import com.github.argon.sos.mod.sdk.util.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Wildcard {
    private final List<String> parts;
    private final static String DELIMITER = "_";
    private final static String WILDCARD = "*";

    public Wildcard(List<String> parts) {
        this.parts = parts;
    }

    public List<String> get() {
        List<String> keys = new ArrayList<>();
        keys.add(WILDCARD);

        String key = "";
        for (int i = 0, partsSize = parts.size(); i < partsSize; i++) {
            String part = parts.get(i);

            if (part.isEmpty()) {
                key = DELIMITER;
                continue;
            }

            key = key + part;
            if (key.startsWith(DELIMITER) || i == partsSize - 1) {
                keys.add(key);
            } else {
                keys.add(key + WILDCARD);
            }

            key = key + DELIMITER;
        }

        return keys;
    }

    public static Wildcard of(String path) {
        String[] parts = path.split(DELIMITER);
        return new Wildcard(Lists.of(parts));
    }

    public static Set<String> from(List<String> paths) {
        return paths.stream()
            .flatMap(path -> Wildcard.of(path).get().stream())
            .collect(Collectors.toSet());
    }
}
