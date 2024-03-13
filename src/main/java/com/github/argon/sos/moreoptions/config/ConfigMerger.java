package com.github.argon.sos.moreoptions.config;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

import static com.github.argon.sos.moreoptions.config.MoreOptionsV2Config.*;

/**
 * Can merge two {@link MoreOptionsV2Config}s while one serves as target and the other source of the data.
 * Will fill empty fields. Used for e.g. merging default configs into loaded configs.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigMerger {
    public static void merge(MoreOptionsV2Config target, @Nullable MoreOptionsV2Config source) {
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
            addMissing(target.getBoosters(), source.getBoosters());
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

    public static void merge(RacesConfig target, @Nullable RacesConfig source) {
        if (source == null) {
            return;
        }

        if (target.getLikings() == null) {
            target.setLikings(source.getLikings());
        } else {
            target.setLikings(replace(target.getLikings(), source.getLikings()));
        }
    }

    public static void merge(Metrics target, @Nullable Metrics source) {
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

        if (target.getStats() == null) {
            target.setStats(source.getStats());
        } else {
            target.setStats(replace(target.getStats(), source.getStats()));
        }
    }

    public static void merge(Events target, @Nullable Events source) {
        if (source == null) {
            return;
        }

        if (target.getChance() == null) {
            target.setChance(source.getChance());
        } else {
            addMissing(target.getChance(), source.getChance());
        }

        if (target.getWorld() == null) {
            target.setWorld(source.getWorld());
        } else {
            addMissing(target.getWorld(), source.getWorld());
        }

        if (target.getSettlement() == null) {
            target.setSettlement(source.getSettlement());
        } else {
            addMissing(target.getSettlement(), source.getSettlement());
        }
    }

    public static void merge(Sounds target, @Nullable Sounds source) {
        if (source == null) {
            return;
        }

        if (target.getRoom() == null) {
            target.setRoom(source.getRoom());
        } else {
            addMissing(target.getRoom(), source.getRoom());
        }

        if (target.getAmbience() == null) {
            target.setAmbience(source.getAmbience());
        } else {
            addMissing(target.getAmbience(), source.getAmbience());
        }

        if (target.getSettlement() == null) {
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
