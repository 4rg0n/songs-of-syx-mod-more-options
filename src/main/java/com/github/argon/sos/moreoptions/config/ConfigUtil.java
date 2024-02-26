package com.github.argon.sos.moreoptions.config;

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;

public class ConfigUtil {
    public static Map<String, MoreOptionsConfig.Range> mergeIntoRange(Map<String, Integer> intMap, Map<String, MoreOptionsConfig.Range> rangeMap) {
        rangeMap.forEach((key, range) -> {
            if (intMap.containsKey(key)) {
                range.setValue(intMap.get(key));
            }
        });

        return rangeMap;
    }

    public static Map<String, MoreOptionsConfig.Range> mergeIntoNewRange(Map<String, MoreOptionsConfig.Range> merge, Map<String, MoreOptionsConfig.Range> into) {
        return into.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            (entry) -> {
                MoreOptionsConfig.Range newRange = entry.getValue().clone();
                if (merge.containsKey(entry.getKey())) {
                    newRange.setValue(merge.get(entry.getKey()).getValue());
                    newRange.setApplyMode(merge.get(entry.getKey()).getApplyMode());
                }
                return newRange;
            }));
    }

    public static Map<String, MoreOptionsConfig.Range> mergeIntegerIntoNewRange(Map<String, Integer> intMap, Map<String, MoreOptionsConfig.Range> rangeMap) {
        return rangeMap.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            (entry) -> {
                MoreOptionsConfig.Range newRange = entry.getValue().clone();
                if (intMap.containsKey(entry.getKey())) {
                    newRange.setValue(intMap.get(entry.getKey()));
                }
                return newRange;
            }));
    }

    public static Map<String, Integer> extract(Map<String, MoreOptionsConfig.Range> rangeMap) {
        return rangeMap.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().getValue()));
    }

    public static MoreOptionsConfig.Range mergeIntegerIntoNewRange(int value, @Nullable MoreOptionsConfig.Range defaultRange) {
        MoreOptionsConfig.Range range = MoreOptionsConfig.Range.builder()
            .value(value)
            .build();

        if (defaultRange != null) {
            range.setMin(defaultRange.getMin());
            range.setMax(defaultRange.getMax());
            range.setDisplayMode(defaultRange.getDisplayMode());
            range.setApplyMode(defaultRange.getApplyMode());
        }

        return range;
    }
}
