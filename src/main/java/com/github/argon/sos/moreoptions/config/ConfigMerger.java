package com.github.argon.sos.moreoptions.config;


import com.github.argon.sos.moreoptions.config.MoreOptionsV2Config.Metrics;
import com.github.argon.sos.moreoptions.config.MoreOptionsV2Config.Range;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigMerger {
    public static void merge(MoreOptionsV2Config target, MoreOptionsV2Config source) {
        // intentionally ignore updating of VERSION and filePath
        target.setLogLevel(source.getLogLevel());

        // Sounds
        if (target.getSounds() == null) {
            target.setSounds(source.getSounds());
        } else {
            merge(target.getSounds(), source.getSounds());
        }

        // Events
        if (target.getEvents() == null) {
            target.setEvents(source.getEvents());
        } else {
            merge(target.getEvents(), source.getEvents());
        }

        // Weather
        if (target.getWeather() == null) {
            target.setWeather(source.getWeather());
        } else {
            merge(target.getWeather(), source.getWeather());
        }

        // Boosters
        if (target.getBoosters() == null) {
            target.setBoosters(source.getBoosters());
        } else {
            merge(target.getBoosters(), source.getBoosters());
        }

        // Metrics
        if (target.getMetrics() == null) {
            target.setMetrics(source.getMetrics());
        } else {
            merge(target.getMetrics(), source.getMetrics());
        }

        // Races
        if (target.getRaces() == null) {
            target.setRaces(source.getRaces());
        } else {
            merge(target.getRaces(), source.getRaces());
        }
    }

    private static void merge(MoreOptionsV2Config.Races target, MoreOptionsV2Config.Races source) {
        if (target.getLikings() == null) {
            target.setLikings(source.getLikings());
        } else {
            merge(target.getLikings(), source.getLikings());
        }
    }

    public static void merge(Metrics target, Metrics source) {
        target.setEnabled(source.isEnabled());

        if (target.getCollectionRateSeconds() == null) {
            target.setCollectionRateSeconds(source.getCollectionRateSeconds());
        } else {
            merge(target.getCollectionRateSeconds(), source.getCollectionRateSeconds());
        }

        if (target.getExportRateMinutes() == null) {
            target.setExportRateMinutes(source.getExportRateMinutes());
        } else {
            merge(target.getExportRateMinutes(), source.getExportRateMinutes());
        }

        if (target.getStats() == null) {
            target.setStats(source.getStats());
        } else {
            merge(target.getStats(), source.getStats());
        }
    }
    public static void merge(MoreOptionsV2Config.Events target, MoreOptionsV2Config.Events source) {
        if (target.getChance() == null) {
            target.setChance(source.getChance());
        } else {
            merge(target.getChance(), source.getChance());
        }

        if (target.getWorld() == null) {
            target.setWorld(source.getWorld());
        } else {
            merge(target.getWorld(), source.getWorld());
        }

        if (target.getSettlement() == null) {
            target.setSettlement(source.getSettlement());
        } else {
            merge(target.getSettlement(), source.getSettlement());
        }
    }

    public static void merge(MoreOptionsV2Config.Sounds target, MoreOptionsV2Config.Sounds source) {
        if (target.getRoom() == null) {
            target.setRoom(source.getRoom());
        } else {
            merge(target.getRoom(), source.getRoom());
        }

        if (target.getAmbience() == null) {
            target.setAmbience(source.getAmbience());
        } else {
            merge(target.getAmbience(), source.getAmbience());
        }

        if (target.getSettlement() == null) {
            target.setSettlement(source.getSettlement());
        } else {
            merge(target.getSettlement(), source.getSettlement());
        }
    }

    public static void merge(Range target, Range source) {
        target.setValue(source.getValue());
        target.setMin(source.getMin());
        target.setMax(source.getMax());
        target.setApplyMode(source.getApplyMode());
        target.setDisplayMode(source.getDisplayMode());
    }

    public static <T> void merge(List<T> target, List<T> source) {
        target.clear();
        target.addAll(source);
    }

    public static <K, V> void merge(Map<K, V> target, Map<K, V> source) {
        // replace or update target entries with source entries
        target.forEach((key, value) -> {
            if (source.containsKey(key)) {
                if (value instanceof Range) {
                    merge((Range) value, (Range) source.get(key));
                } else {
                    target.put(key, source.get(key));
                }
            }
        });

        // add missing entries
        source.forEach((key, value) -> {
            if (!target.containsKey(key)) {
                source.put(key, value);
            }
        });

        target.clear();
        target.putAll(source);
    }
}
