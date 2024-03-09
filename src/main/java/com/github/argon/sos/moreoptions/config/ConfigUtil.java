package com.github.argon.sos.moreoptions.config;

import java.util.Map;
import java.util.stream.Collectors;

public class ConfigUtil {

    public static Map<String, Integer> extractValues(Map<String, MoreOptionsV2Config.Range> rangeMap) {
        return rangeMap.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().getValue()));
    }

    public static MoreOptionsV2Config.Range newRange(int value) {
        return MoreOptionsV2Config.Range.builder()
            .value(value)
            .build();
    }
}
