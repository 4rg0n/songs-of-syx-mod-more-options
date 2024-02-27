package com.github.argon.sos.moreoptions.metric;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MetricScheduler {
    @Getter(lazy = true)
    private final static MetricScheduler instance = new MetricScheduler();

    private final static Logger log = Loggers.getLogger(MetricScheduler.class);
    private final Map<Runnable, Trigger> tasks = new HashMap<>();
    @Nullable
    private ScheduledExecutorService scheduler;

    @Getter
    private boolean started = false;

    public void clear() {
        tasks.clear();
    }

    public MetricScheduler start() {
        if (scheduler == null) {
            scheduler = createScheduler(tasks.size());
        }

        try {
            log.debug("Start scheduling of %s metric tasks", tasks.size());
            tasks.forEach((runnable, trigger) ->
                scheduler.scheduleAtFixedRate(runnable,
                    trigger.getInitialDelay(), trigger.getPeriod(), trigger.getUnit())
            );
            started = true;
        } catch (Exception e) {
            log.warn("Could not start metric scheduler", e);
        }

        return this;
    }

    public MetricScheduler stop() {
        if (scheduler == null) {
            return this;
        }

        scheduler.shutdownNow();
        scheduler = null;
        started = false;

        log.debug("Stopped scheduling of % metric tasks", tasks.size());
        return this;
    }

    public MetricScheduler schedule(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        tasks.put(runnable, Trigger.builder()
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
    public static class Trigger {
        private final long initialDelay;
        private final long period;

        private final TimeUnit unit;
    }
}
