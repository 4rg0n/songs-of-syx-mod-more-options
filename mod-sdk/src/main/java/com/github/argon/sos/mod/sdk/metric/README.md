# Metric

This thing can collect game stats and export it as a CSV file while the game is running. 
Metric CSV files will be saved into the games app data directory under `saves/profile/Metric Exports/`.
For each game start (even when you reload into an existing save) a new CSV file is created.

```java
public class YourModScript extends AbstractModSdkScript {
    @Override
    protected void registerPhases(PhaseManager phaseManager) {
        // you have to register the metric stuff to the correct phases to make it work
        phaseManager
            .register(Phase.INIT_BEFORE_GAME_CREATED, ModSdkModule.metricExporter())
            .register(Phase.ON_GAME_SAVE_RELOADED, ModSdkModule.metricExporter())
            .register(Phase.ON_CRASH, ModSdkModule.metricScheduler());
    }

    @Override
    public void initBeforeGameCreated() {
        // set up the scheduler
        ModSdkModule.metricScheduler()
            // schedule the collection of metrics; start at 0 seconds and repeat each 60 seconds
            // an empty set Sets.of() means collect every stat
            .schedule(() -> ModSdkModule.metricCollector().buffer(Sets.of()),
                0, 60, TimeUnit.SECONDS)
            // schedule sport of collected stats; start at 5 minutes and repeat each 5 minutes
            .schedule(ModSdkModule.metricExporter()::export,
                5, 5, TimeUnit.MINUTES)
            .start();
    }

    @Override
    public void onViewSetup() {
        // start the collection and export of metrics
        ModSdkModule.metricScheduler().start();
    }
}
```