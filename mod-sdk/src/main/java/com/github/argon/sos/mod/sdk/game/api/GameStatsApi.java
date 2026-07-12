package com.github.argon.sos.mod.sdk.game.api;

import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.metric.MetricCollector;
import lombok.RequiredArgsConstructor;

import java.util.Set;

/**
 * For interacting with game stats.
 */
@RequiredArgsConstructor
public class GameStatsApi {

    private final static Logger log = Loggers.getLogger(GameStatsApi.class);

    private Set<String> availableStatKeys;

    private final MetricCollector metricCollector;

    /**
     * For providing a default whitelist of stats to collect
     *
     * @return set of all available game stat keys
     */
    public Set<String> getAvailableStatKeys() {
        if (availableStatKeys == null) {
            availableStatKeys = metricCollector.collectStats().keySet();

            log.debug("Collected %s available stat keys", availableStatKeys.size());
        }

        return availableStatKeys;
    }
}
