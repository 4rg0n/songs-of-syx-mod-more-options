package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.mod.sdk.data.domain.Range;

import java.util.Map;
import java.util.stream.Collectors;

public class ConfigUtil {

    public static Map<String, Integer> extractValues(Map<String, Range> rangeMap) {
        return rangeMap.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().getValue()));
    }

    public static Range newRange(int value) {
        return Range.builder()
            .value(value)
            .build();
    }
}
