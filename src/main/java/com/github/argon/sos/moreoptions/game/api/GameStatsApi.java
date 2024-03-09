package com.github.argon.sos.moreoptions.game.api;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.metric.MetricCollector;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GameStatsApi {

    private final static Logger log = Loggers.getLogger(GameStatsApi.class);

    @Getter(lazy = true)
    private final static GameStatsApi instance = new GameStatsApi(
        MetricCollector.getInstance()
    );

    private Set<String> availableStatKeys;

    private final MetricCollector metricCollector;

    /**
     * For providing a default whitelist of stats to collect
     */
    public Set<String> getAvailableStatKeys() {
        if (availableStatKeys == null) {
            availableStatKeys = metricCollector.collectStats().keySet();

            log.debug("Collected %s available stat keys", availableStatKeys.size());
        }

        return availableStatKeys;
    }
}
