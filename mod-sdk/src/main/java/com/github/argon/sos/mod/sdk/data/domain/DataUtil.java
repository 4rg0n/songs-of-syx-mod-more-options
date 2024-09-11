package com.github.argon.sos.mod.sdk.data.domain;

import java.util.Map;
import java.util.stream.Collectors;

public class DataUtil {

    public static Map<String, Integer> extractValues(Map<String, Range> rangeMap) {
        return rangeMap.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().getValue()));
    }
}
