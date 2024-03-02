package com.github.argon.sos.moreoptions.config;

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;

public class ConfigUtil {
    public static Map<String, MoreOptionsV2Config.Range> mergeIntoRange(Map<String, Integer> intMap, Map<String, MoreOptionsV2Config.Range> rangeMap) {
        rangeMap.forEach((key, range) -> {
            if (intMap.containsKey(key)) {
                range.setValue(intMap.get(key));
            }
        });

        return rangeMap;
    }

    public static Map<String, MoreOptionsV2Config.Range> mergeIntoNewRange(Map<String, MoreOptionsV2Config.Range> merge, Map<String, MoreOptionsV2Config.Range> into) {
        return into.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            (entry) -> {
                MoreOptionsV2Config.Range newRange = entry.getValue().clone();
                if (merge.containsKey(entry.getKey())) {
                    newRange.setValue(merge.get(entry.getKey()).getValue());
                    newRange.setApplyMode(merge.get(entry.getKey()).getApplyMode());
                }
                return newRange;
            }));
    }

    public static Map<String, MoreOptionsV2Config.Range> mergeIntegerIntoNewRange(Map<String, Integer> intMap, Map<String, MoreOptionsV2Config.Range> rangeMap) {
        return rangeMap.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            (entry) -> {
                MoreOptionsV2Config.Range newRange = entry.getValue().clone();
                if (intMap.containsKey(entry.getKey())) {
                    newRange.setValue(intMap.get(entry.getKey()));
                }
                return newRange;
            }));
    }

    public static Map<String, Integer> extract(Map<String, MoreOptionsV2Config.Range> rangeMap) {
        return rangeMap.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().getValue()));
    }

    public static MoreOptionsV2Config.Range mergeIntegerIntoNewRange(int value, @Nullable MoreOptionsV2Config.Range defaultRange) {
        MoreOptionsV2Config.Range range = MoreOptionsV2Config.Range.builder()
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
