package com.github.argon.sos.mod.sdk.metric;

import com.github.argon.sos.mod.sdk.phase.Phases;
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
 * For scheduling metric collection and exporting tasks.
 */
@RequiredArgsConstructor
public class MetricScheduler implements Phases {
    private final static Logger log = Loggers.getLogger(MetricScheduler.class);

    private final Map<Runnable, Trigger> tasks = new HashMap<>();
    @Nullable
    private ScheduledExecutorService scheduler;

    @Getter
    private boolean started = false;

    /**
     * Will clear the list of collection and export tasks.
     */
    public void clear() {
        tasks.clear();
    }

    /**
     * Will start the collecting and exporting of {@link Metric}s.
     *
     * @return this
     */
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
                    trigger.initialDelay(), trigger.period(), trigger.timeUnit())
            );
            started = true;
        } catch (Exception e) {
            log.warn("Could not start metric scheduler", e);
        }

        return this;
    }

    /**
     * Will stop the collecting and exporting of {@link Metric}s.
     *
     * @return this
     */
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

    /**
     * Will schedule a collection or export task as {@link Runnable} with given time settings.
     *
     * @param runnable task to schedule
     * @param initialDelay starting delay
     * @param period delay between each task
     * @param timeUnit for the initialDelay and period
     * @return this
     */
    public MetricScheduler schedule(Runnable runnable, long initialDelay, long period, TimeUnit timeUnit) {
        tasks.put(runnable, Trigger.builder()
            .initialDelay(initialDelay)
            .period(period)
            .timeUnit(timeUnit)
            .build());

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCrash(Throwable e) {
        log.info("Stopping metric scheduler because of game crash");
        stop();
    }

    private ScheduledExecutorService createScheduler(int poolSize) {
        return Executors.newScheduledThreadPool(poolSize);
    }

    /**
     * A trigger for starting a collection or exporting task.
     *
     * @param initialDelay starting delay
     * @param period delay between each task
     * @param timeUnit for the initialDelay and period
     */
    @Builder
    public record Trigger(long initialDelay, long period, TimeUnit timeUnit) {}
}
