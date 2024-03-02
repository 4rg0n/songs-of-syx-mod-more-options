package com.github.argon.sos.moreoptions.game.api;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.metric.MetricCollector;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GameStatsApi {

    private final static Logger log = Loggers.getLogger(GameStatsApi.class);

    @Getter(lazy = true)
    private final static GameStatsApi instance = new GameStatsApi(
        MetricCollector.getInstance()
    );

    private List<String> availableStatKeys;

    private final MetricCollector metricCollector;
    public List<String> getAvailableStatKeys() {
        if (availableStatKeys == null) {
            availableStatKeys = new ArrayList<>(metricCollector.collectStats().keySet());
            Collections.sort(availableStatKeys);

            log.debug("Collected %s available stat keys", availableStatKeys.size());
        }

        return availableStatKeys;
    }
}
