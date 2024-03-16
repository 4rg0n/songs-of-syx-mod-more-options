package com.github.argon.sos.moreoptions.config;


import com.github.argon.sos.moreoptions.config.domain.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * Can merge two {@link MoreOptionsV3Config}s while one serves as target and the other source of the data.
 * Will fill empty fields. Used for e.g. merging default configs into loaded configs.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigMerger {
    public static void merge(MoreOptionsV3Config target, @Nullable MoreOptionsV3Config source) {
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
            addMissing(target.getWeather(), source.getWeather());
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

    public static void merge(BoostersConfig target, @Nullable BoostersConfig source) {
        if (source == null) {
            return;
        }

        if (target.getPlayer() == null || target.getPlayer().isEmpty()) {
            target.setPlayer(source.getPlayer());
        } else {
            addMissing(target.getPlayer(), source.getPlayer());
        }

        if (target.getFaction() == null || target.getFaction().isEmpty()) {
            target.setFaction(source.getFaction());
        } else {
            merge(target.getFaction(), source.getFaction());
        }
    }

    private static <T> void addMissing(Set<T> target, @Nullable Set<T> source) {
        if (source == null) {
            return;
        }

        target.addAll(source);
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

        if (target.getWorld() == null || target.getWorld().isEmpty()) {
            target.setWorld(source.getWorld());
        } else {
            addMissing(target.getWorld(), source.getWorld());
        }

        if (target.getSettlement() == null || target.getSettlement().isEmpty()) {
            target.setSettlement(source.getSettlement());
        } else {
            addMissing(target.getSettlement(), source.getSettlement());
        }
    }

    public static void merge(SoundsConfig target, @Nullable SoundsConfig source) {
        if (source == null) {
            return;
        }

        if (target.getRoom() == null || target.getRoom().isEmpty()) {
            target.setRoom(source.getRoom());
        } else {
            addMissing(target.getRoom(), source.getRoom());
        }

        if (target.getAmbience() == null || target.getAmbience().isEmpty()) {
            target.setAmbience(source.getAmbience());
        } else {
            addMissing(target.getAmbience(), source.getAmbience());
        }

        if (target.getSettlement() == null || target.getSettlement().isEmpty()) {
            target.setSettlement(source.getSettlement());
        } else {
            addMissing(target.getSettlement(), source.getSettlement());
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

        target.forEach((key, value) -> {
            if (!source.containsKey(key)) {
                target.remove(key);
            }
        });
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
