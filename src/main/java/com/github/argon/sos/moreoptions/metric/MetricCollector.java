package com.github.argon.sos.moreoptions.metric;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.MapUtil;
import init.race.RACES;
import init.race.Race;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import settlement.stats.STATS;
import settlement.stats.colls.StatsReligion;
import settlement.stats.stat.SETT_STATISTICS;
import settlement.stats.stat.STAT;
import settlement.stats.stat.StatCollection;
import snake2d.util.sets.LIST;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MetricCollector {

    @Getter(lazy = true)
    private final static MetricCollector instance = new MetricCollector();

    private final static Logger log = Loggers.getLogger(MetricCollector.class);

    private final List<Metric> buffered = new ArrayList<>();

    @Getter
    private final List<String> keyList = new ArrayList<>();

    public void buffer() {
        try {
            buffered.add(collect());
        } catch (Exception e) {
            log.warn("Could not collect metrics for buffering", e);
        }

        log.trace("Buffered %s metrics", buffered.size());
    }

    public List<Metric> flush() {
        ArrayList<Metric> bufferedMetrics = new ArrayList<>(buffered);
        buffered.clear();

        log.debug("Flushed %s buffered metrics", bufferedMetrics.size());
        return bufferedMetrics;
    }

    public Metric collect() {
        final Map<String, Object> stats = new HashMap<>();

        for (StatCollection collection : STATS.COLLECTIONS()) {
            for (STAT stat : collection.all()) {
                if (stat.key() == null) {
                    continue;
                }

                String key = collection.key;

                stats.putAll(getRaceStats(key, stat));
                stats.putAll(getStats(key, stat));
            }
        }

        stats.putAll(getStats(STATS.POP().key, STATS.POP().POP));
        stats.putAll(getRaceStats(STATS.POP().key, STATS.POP().POP));

        stats.putAll(getReligionStats(STATS.RELIGION().key, STATS.RELIGION().ALL));
        stats.putAll(getReligionRaceStats(STATS.RELIGION().key, STATS.RELIGION().ALL));

        // fill keyList on first run
        if (keyList.isEmpty()) {
            keyList.addAll(stats.keySet());
            log.trace("Initialized key list: %s", keyList);
        }

        // keep results consistent
        int sizeDiff = keyList.size() - stats.size();
        if (sizeDiff > 0) {
            // fill empty fields with null
            keyList.forEach(s -> {
                stats.putIfAbsent(s, null);
            });

            log.debug("Filled % missing entries with nulls", sizeDiff);
        }

        Metric metric = Metric.builder()
            .values(stats)
            .build();

        log.debug("Collected %s stats for game time: %s", stats.size(), metric.getGameTime().formatted());
        log.trace("Stats: %s", stats);
        return metric;
    }

    private static Map<String, Integer> getReligionStats(String keyPrefix, LIST<StatsReligion.StatReligion> statList) {
        Map<String, Integer> stats = new HashMap<>();

        for (StatsReligion.StatReligion statReligion : statList) {
            stats.putAll(getReligionStats(keyPrefix, statReligion));
        }

        return stats;
    }

    private static Map<String, Integer> getReligionRaceStats(String keyPrefix, LIST<StatsReligion.StatReligion> statList) {
        Map<String, Integer> stats = new HashMap<>();

        for (StatsReligion.StatReligion statReligion : statList) {
            stats.putAll(getReligionRaceStats(keyPrefix, statReligion));
        }

        return stats;
    }
    private static Map<String, Integer> getReligionRaceStats(String keyPrefix, StatsReligion.StatReligion stat) {
        Map<String, Integer> stats = new HashMap<>();
        String key = keyPrefix + ":" + stat.religion.key;

        stats.putAll(getRaceStats(key, stat.followers));
        stats.putAll(getRaceStats(key, stat.shrine_access));
        stats.putAll(getRaceStats(key, stat.shrine_quality));
        stats.putAll(getRaceStats(key, stat.temple_access));
        stats.putAll(getRaceStats(key, stat.temple_quality));

        return stats;
    }

    private static Map<String, Integer> getReligionStats(String keyPrefix, StatsReligion.StatReligion stat) {
        Map<String, Integer> stats = new HashMap<>();
        String key = keyPrefix + ":" + stat.religion.key;

        stats.putAll(getStats(key, stat.followers));
        stats.putAll(getStats(key, stat.shrine_access));
        stats.putAll(getStats(key, stat.shrine_quality));
        stats.putAll(getStats(key, stat.temple_access));
        stats.putAll(getStats(key, stat.temple_quality));

        return stats;
    }

    private static Map<String, Integer> getStats(String keyPrefix, SETT_STATISTICS.SettStatistics stat) {
        String key = keyPrefix + ":" +  stat.info().name + ":_TOTAL";

        try {
            return MapUtil.of(key, stat.data().get(null));
        } catch (Exception e) {
            log.trace("Could not collect settlement stat %s. Skipping", key, e);

            return new HashMap<>();
        }
    }

    private static Map<String, Integer> getRaceStats(String keyPrefix, SETT_STATISTICS.SettStatistics stat) {
        Map<String, Integer> stats = new HashMap<>();

        for (Race race : RACES.all()) {
            String key = keyPrefix + ":" + stat.info().name + ":" + race.key;

            try {
                int value = stat.data().get(race);
                stats.put(key, value);
            } catch (Exception e) {
                log.trace("Could not collect settlement stat %s. Skipping", key, e);
            }
        }

        return stats;
    }

    private static Map<String, Integer> getStats(String keyPrefix, STAT stat) {
        String key = keyPrefix + ":" +  stat.key() + ":_TOTAL";

        try {
            return MapUtil.of(key, stat.data().get(null));
        } catch (Exception e) {
            log.trace("Could not collect stat %s. Skipping", key, e);

            return new HashMap<>();
        }
    }

    private static Map<String, Integer> getRaceStats(String keyPrefix, STAT stat) {
        Map<String, Integer> stats = new HashMap<>();


        if (!stat.hasIndu()) {
            return stats;
        }

        for (Race race : RACES.all()) {
            String key = keyPrefix + ":" + stat.key() + ":" + race.key;

            try {
                int value = stat.data().get(race);
                stats.put(key, value);
            } catch (Exception e) {
                log.trace("Could not collect stat %s. Skipping", key, e);
            }
        }

        return stats;
    }
}
