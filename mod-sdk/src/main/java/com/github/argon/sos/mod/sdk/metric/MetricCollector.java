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

    /**
     * Buffers and collects values for the given stat names.
     *
     * @param statNames to buffer and collect values from.
     */
    public void buffer(Set<String> statNames) {
        try {
            buffered.add(collect(statNames));
        } catch (Exception e) {
            log.warn("Could not collect metrics for buffering", e);
        }

        log.trace("Buffered %s metrics", buffered.size());
    }

    /**
     * Clears the buffered stats and returns them as {@link Metric}s
     *
     * @return buffered metrics
     */
    public List<Metric> flush() {
        ArrayList<Metric> bufferedMetrics = new ArrayList<>(buffered);
        buffered.clear();

        log.debug("Flushed %s buffered metrics", bufferedMetrics.size());
        return bufferedMetrics;
    }

    /**
     * Collects all possible stats from the game as a key value map.
     *
     * @return map with stat name as key and the stat value as value
     */
    public Map<String, Object> collectStats() {
        return collectStats(Collections.emptySet());
    }

    /**
     * Collects only stats which are present in the given stat name list.
     *
     * @param statNames to collect
     * @return map with stat name as key and the stat value as value
     */
    public Map<String, Object> collectStats(Set<String> statNames) {
        final Map<String, Object> stats = new HashMap<>();
        StatsExtractor statsExtractor = new StatsExtractor(new HashSet<>(statNames));

        for (int i = 0; i < STATS.all().size(); i++) {
            STAT stat = STATS.all().get(i);
            stats.putAll(statsExtractor.extractRaceStats(stat));
            stats.putAll(statsExtractor.extractStat(stat));
        }

        // Population
        stats.putAll(statsExtractor.extractStat(STATS.POP().key, STATS.POP().POP));
        // Religion
        stats.putAll(statsExtractor.extractReligionStats(STATS.RELIGION().ALL));
        stats.putAll(statsExtractor.extractReligionRaceStats(STATS.RELIGION().ALL));

        for (int i = 0; i < GAME.count().ALL.size(); i++) {
            GCOUNTS.SAccumilator accumulator = GAME.count().ALL.get(i);
            stats.put(accumulator.key, accumulator.current());
        }

        return stats;
    }

    /**
     * Collects only stats which are present in the given stat name list as a {@link Metric}.
     *
     * @param statNames to collect
     * @return metric with stats and their values
     */
    public Metric collect(Set<String> statNames) {
        Map<String, Object> stats = collectStats(statNames);
        log.debug("Collected %s game stats", stats.size());
        log.trace("Stats: %s", stats);

        return Metric.builder()
            .values(stats)
            .build();
    }
}
