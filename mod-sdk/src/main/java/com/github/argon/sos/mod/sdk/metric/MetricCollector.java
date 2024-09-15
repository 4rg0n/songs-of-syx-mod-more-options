package com.github.argon.sos.mod.sdk.metric;

import com.github.argon.sos.mod.sdk.game.StatsExtractor;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import game.GAME;
import game.values.GCOUNTS;
import lombok.RequiredArgsConstructor;
import settlement.stats.STATS;
import settlement.stats.stat.STAT;

import java.util.*;

/**
 * For collecting game stats as {@link Metric}
 */
@RequiredArgsConstructor
public class MetricCollector {

    private final static Logger log = Loggers.getLogger(MetricCollector.class);

    private final List<Metric> buffered = Collections.synchronizedList(new ArrayList<>());

    public void buffer(Set<String> whitelist) {
        try {
            buffered.add(collect(whitelist));
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

    public Map<String, Object> collectStats() {
        return collectStats(Collections.emptySet());
    }

    public Map<String, Object> collectStats(Set<String> whitelist) {
        final Map<String, Object> stats = new HashMap<>();
        StatsExtractor statsExtractor = new StatsExtractor(new HashSet<>(whitelist));

        for (int i = 0; i < STATS.all().size(); i++) {
            STAT stat = STATS.all().get(i);
            stats.putAll(statsExtractor.getRaceStats(stat));
            stats.putAll(statsExtractor.getStat(stat));
        }

        // Population
        stats.putAll(statsExtractor.getStat(STATS.POP().key, STATS.POP().POP));
        // Religion
        stats.putAll(statsExtractor.getReligionStats(STATS.RELIGION().ALL));
        stats.putAll(statsExtractor.getReligionRaceStats(STATS.RELIGION().ALL));

        for (int i = 0; i < GAME.count().ALL.size(); i++) {
            GCOUNTS.SAccumilator accumulator = GAME.count().ALL.get(i);
            stats.put(accumulator.key, accumulator.current());
        }

        return stats;
    }

    public Metric collect(Set<String> whitelist) {
        Map<String, Object> stats = collectStats(whitelist);
        log.debug("Collected %s game stats", stats.size());
        log.trace("Stats: %s", stats);

        return Metric.builder()
            .values(stats)
            .build();
    }
}
