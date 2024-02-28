package com.github.argon.sos.moreoptions.metric;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.Lists;
import lombok.Getter;
import settlement.stats.STATS;
import settlement.stats.stat.STAT;

import java.util.*;

public class MetricCollector {

    @Getter(lazy = true)
    private final static MetricCollector instance = new MetricCollector();

    private final static Logger log = Loggers.getLogger(MetricCollector.class);

    private final List<Metric> buffered = Collections.synchronizedList(new ArrayList<>());

    public void buffer(List<String> whitelist) {
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
        return collectStats(Lists.of());
    }

    public Map<String, Object> collectStats(List<String> whitelist) {
        final Map<String, Object> stats = new HashMap<>();
        StatsCollector statsCollector = new StatsCollector(new HashSet<>(whitelist));

        for (STAT stat : STATS.all()) {
            stats.putAll(statsCollector.getRaceStats(stat));
            stats.putAll(statsCollector.getStat(stat));
        }

        stats.putAll(statsCollector.getStat(STATS.POP().key, STATS.POP().POP));
        stats.putAll(statsCollector.getRaceStats(STATS.POP().key, STATS.POP().POP));

        stats.putAll(statsCollector.getReligionStats(STATS.RELIGION().ALL));
        stats.putAll(statsCollector.getReligionRaceStats(STATS.RELIGION().ALL));

        return stats;
    }

    public Metric collect(List<String> whitelist) {
        Map<String, Object> stats = collectStats(whitelist);
        log.debug("Collected %s game stats", stats.size());
        log.trace("Stats: %s", stats);

        return Metric.builder()
            .values(stats)
            .build();
    }
}
