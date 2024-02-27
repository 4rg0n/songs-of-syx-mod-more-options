package com.github.argon.sos.moreoptions.metric;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.Maps;
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

import java.util.*;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MetricCollector {

    @Getter(lazy = true)
    private final static MetricCollector instance = new MetricCollector();

    private final static Logger log = Loggers.getLogger(MetricCollector.class);

    private final List<Metric> buffered = Collections.synchronizedList(new ArrayList<>());

    @Getter
    private final List<String> keyList = new ArrayList<>();

    @Getter
    private final Set<String> whiteList = new HashSet<>();

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

        // fill whiteList on first run
        if (whiteList.isEmpty()) {
            whiteList.addAll(keyList);
            log.trace("Initialized white list: %s", whiteList);
        }

        // fill empty fields with null
        keyList.forEach(s -> {
            if (isWhitelisted(s)) {
                stats.putIfAbsent(s, null);
            }
        });

        Metric metric = Metric.builder()
            .values(stats)
            .build();

        log.debug("Collected %s stats for game time: %s", stats.size(), metric.getGameTime().formatted());
        log.trace("Stats: %s", stats);
        return metric;
    }

    private boolean isWhitelisted(String key) {
        // no whitelist = accept all
        if (whiteList.isEmpty()) {
            return true;
        }

        return whiteList.contains(key);
    }

    private Map<String, Integer> getReligionStats(String keyPrefix, LIST<StatsReligion.StatReligion> statList) {
        Map<String, Integer> stats = new HashMap<>();

        for (StatsReligion.StatReligion statReligion : statList) {
            stats.putAll(getReligionStats(keyPrefix, statReligion));
        }

        return stats;
    }

    private Map<String, Integer> getReligionRaceStats(String keyPrefix, LIST<StatsReligion.StatReligion> statList) {
        Map<String, Integer> stats = new HashMap<>();

        for (StatsReligion.StatReligion statReligion : statList) {
            stats.putAll(getReligionRaceStats(keyPrefix, statReligion));
        }

        return stats;
    }
    private Map<String, Integer> getReligionRaceStats(String keyPrefix, StatsReligion.StatReligion stat) {
        Map<String, Integer> stats = new HashMap<>();
        String key = keyPrefix + ":" + stat.religion.key;

        stats.putAll(getRaceStats(key, stat.followers));
        stats.putAll(getRaceStats(key, stat.shrine_access));
        stats.putAll(getRaceStats(key, stat.shrine_quality));
        stats.putAll(getRaceStats(key, stat.temple_access));
        stats.putAll(getRaceStats(key, stat.temple_quality));

        return stats;
    }

    private Map<String, Integer> getReligionStats(String keyPrefix, StatsReligion.StatReligion stat) {
        Map<String, Integer> stats = new HashMap<>();
        String key = keyPrefix + ":" + stat.religion.key;

        stats.putAll(getStats(key, stat.followers));
        stats.putAll(getStats(key, stat.shrine_access));
        stats.putAll(getStats(key, stat.shrine_quality));
        stats.putAll(getStats(key, stat.temple_access));
        stats.putAll(getStats(key, stat.temple_quality));

        return stats;
    }

    private Map<String, Integer> getStats(String keyPrefix, SETT_STATISTICS.SettStatistics stat) {
        String key = keyPrefix + ":" +  stat.info().name + ":_TOTAL";

        if (!isWhitelisted(key)) {
            return Maps.of();
        }

        try {
            return Maps.of(key, stat.data().get(null));
        } catch (Exception e) {
            log.trace("Could not collect settlement stat %s. Skipping", key, e);

            return Maps.of();
        }
    }

    private Map<String, Integer> getRaceStats(String keyPrefix, SETT_STATISTICS.SettStatistics stat) {
        Map<String, Integer> stats = new HashMap<>();


        for (Race race : RACES.all()) {
            String key = keyPrefix + ":" + stat.info().name + ":" + race.key;

            if (!isWhitelisted(key)) {
                continue;
            }

            try {
                int value = stat.data().get(race);
                stats.put(key, value);
            } catch (Exception e) {
                log.trace("Could not collect settlement stat %s. Skipping", key, e);
            }
        }

        return stats;
    }

    private Map<String, Integer> getStats(String keyPrefix, STAT stat) {
        String key = keyPrefix + ":" +  stat.key() + ":_TOTAL";

        if (!isWhitelisted(key)) {
            return Maps.of();
        }

        try {
            return Maps.of(key, stat.data().get(null));
        } catch (Exception e) {
            log.trace("Could not collect stat %s. Skipping", key, e);

            return Maps.of();
        }
    }

    private Map<String, Integer> getRaceStats(String keyPrefix, STAT stat) {
        Map<String, Integer> stats = new HashMap<>();

        if (!stat.hasIndu()) {
            return stats;
        }

        for (Race race : RACES.all()) {
            String key = keyPrefix + ":" + stat.key() + ":" + race.key;

            if (!isWhitelisted(key)) {
                continue;
            }

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
