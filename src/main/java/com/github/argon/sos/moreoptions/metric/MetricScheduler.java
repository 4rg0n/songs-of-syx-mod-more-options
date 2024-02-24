package com.github.argon.sos.moreoptions.metric;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MetricScheduler {
    @Getter(lazy = true)
    private final static MetricScheduler instance = new MetricScheduler();

    private final static Logger log = Loggers.getLogger(MetricScheduler.class);

    private ScheduledExecutorService scheduler;

    private final Map<Runnable, Toggle> tasks = new HashMap<>();

    public MetricScheduler start() {
        if (scheduler == null) {
            scheduler = createScheduler(tasks.size());
        }

        try {
            log.debug("Starting scheduling of %s tasks", tasks.size());
            tasks.forEach((runnable, toggle) ->
                scheduler.scheduleAtFixedRate(runnable,
                    toggle.getInitialDelay(), toggle.getPeriod(), toggle.getUnit())
            );
        } catch (Exception e) {
            log.warn("Could not start scheduler", e);
        }

        return this;
    }

    public MetricScheduler stop() {
        log.debug("Stopping scheduling of % tasks", tasks.size());
        scheduler.shutdownNow();
        scheduler = null;

        return this;
    }

    public MetricScheduler schedule(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        tasks.put(runnable, Toggle.builder()
            .initialDelay(initialDelay)
            .period(period)
            .unit(unit)
            .build());

        return this;
    }

    private ScheduledExecutorService createScheduler(int poolSize) {
        return Executors.newScheduledThreadPool(poolSize);
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Toggle {
        private final long initialDelay;
        private final long period;

        private final TimeUnit unit;
    }
}
