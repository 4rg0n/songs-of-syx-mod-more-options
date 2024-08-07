package com.github.argon.sos.moreoptions.config;


import com.github.argon.sos.moreoptions.config.domain.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Can merge two {@link MoreOptionsV4Config}s while one serves as target and the other source of the data.
 * Will fill empty fields. Used for e.g. merging default configs into loaded configs.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigMerger {
    public static void merge(MoreOptionsV4Config target, @Nullable MoreOptionsV4Config source) {
        if (source == null) {
            return;
        }

        // intentionally ignore updating of VERSION and filePath
        if (target.getLogLevel() == null) target.setLogLevel(source.getLogLevel());

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
    public static void merge(WeatherConfig target, @Nullable WeatherConfig source) {
        if (source == null) {
            return;
        }

        if (target.getEffects() == null || target.getEffects().isEmpty()) {
            target.setEffects(source.getEffects());
        }else {
            addMissing(target.getEffects(), source.getEffects());
        }
    }

    public static void merge(BoostersConfig target, @Nullable BoostersConfig source) {
        if (source == null) {
            return;
        }

        if (target.getPlayer() == null || target.getPlayer().isEmpty()) {
            target.setPlayer(source.getPlayer());
        } else {
            merge(target.getPlayer(), source.getPlayer());
        }

        if (target.getFaction() == null || target.getFaction().isEmpty()) {
            target.setFaction(source.getFaction());
        } else {
            merge(target.getFaction(), source.getFaction());
        }
    }

    public static void merge(RacesConfig target, @Nullable RacesConfig source) {
        if (source == null) {
            return;
        }

        if (target.getLikings() == null || target.getLikings().isEmpty()) {
            target.setLikings(source.getLikings());
        } else {
            target.setLikings(replace(target.getLikings(), source.getLikings()));
        }
    }

    public static void merge(MetricsConfig target, @Nullable MetricsConfig source) {
        if (source == null) {
            return;
        }

        target.setEnabled(source.isEnabled());

        if (target.getCollectionRateSeconds() == null) {
            target.setCollectionRateSeconds(source.getCollectionRateSeconds());
        }

        if (target.getExportRateMinutes() == null) {
            target.setExportRateMinutes(source.getExportRateMinutes());
        }

        if (target.getStats() == null || target.getStats().isEmpty()) {
            target.setStats(source.getStats());
        } else {
            target.setStats(replace(target.getStats(), source.getStats()));
        }
    }

    public static void merge(EventsConfig target, @Nullable EventsConfig source) {
        if (source == null) {
            return;
        }

        if (target.getChance() == null || target.getChance().isEmpty()) {
            target.setChance(source.getChance());
        } else {
            addMissing(target.getChance(), source.getChance());
        }

        if (target.getEvents() == null || target.getEvents().isEmpty()) {
            target.setEvents(source.getEvents());
        } else {
            addMissing(target.getEvents(), source.getEvents());
        }
    }

    public static void merge(SoundsConfig target, @Nullable SoundsConfig source) {
        if (source == null) {
            return;
        }

        if (target.getAmbience() == null || target.getAmbience().isEmpty()) {
            target.setAmbience(source.getAmbience());
        } else {
            addMissing(target.getAmbience(), source.getAmbience());
        }
    }

    public static <T> Set<T> replace(Set<T> target, @Nullable Set<T> source) {
        if (source == null) {
            return target;
        }

        return source;
    }

    public static <K, V> void merge(Map<K, V> target, @Nullable Map<K, V> source) {
        if (source == null) {
            return;
        }

        if (target.isEmpty()) {
            target.putAll(source);
            return;
        }

        removeExcess(target, source);
        addMissing(target, source);
    }

    public static <K, V> void removeExcess(Map<K, V> target, @Nullable Map<K, V> source) {
        if (source == null) {
            return;
        }

        Set<K> toRemove = new HashSet<>();
        target.forEach((key, value) -> {
            if (!source.containsKey(key)) {
                toRemove.add(key);
            }
        });

        toRemove.forEach(target::remove);
    }

    public static <K, V> void addMissing(Map<K, V> target, @Nullable Map<K, V> source) {
        if (source == null) {
            return;
        }

        // add missing entries
        source.forEach((key, value) -> {
            if (!target.containsKey(key)) {
                target.put(key, value);
            }
        });
    }
}
