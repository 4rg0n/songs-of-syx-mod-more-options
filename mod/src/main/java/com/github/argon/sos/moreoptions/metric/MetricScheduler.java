package com.github.argon.sos.moreoptions.metric;

import com.github.argon.sos.moreoptions.phase.Phases;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import lombok.*;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * For scheduling tasks.
 * Used for collecting and exporting metrics.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MetricScheduler implements Phases {
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

    public synchronized MetricScheduler start() {
        if (scheduler == null) {
            scheduler = createScheduler(tasks.size());
        } else {
            log.debug("Tried to schedule tasks on already running scheduler");
            return this;
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

    public synchronized MetricScheduler stop() {
        if (scheduler == null) {
            return this;
        }

        scheduler.shutdownNow();
        scheduler = null;
        started = false;

        log.debug("Stopped scheduling of %s metric tasks", tasks.size());
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

    @Override
    public void onCrash(Throwable e) {
        log.info("Stopping metric scheduler because of game crash");
        stop();
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
