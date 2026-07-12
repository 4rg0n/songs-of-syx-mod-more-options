package com.github.argon.sos.mod.sdk.data.domain;

import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility methods for data related stuff
 */
@UtilityClass
public class DataUtil {

    /**
     * Extracts range values from a given range map
     *
     * @param rangeMap to extract the values from
     * @return values from ranges
     */
    public static Map<String, Integer> extractValues(Map<String, Range> rangeMap) {
        return rangeMap.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().getValue()));
    }
}
