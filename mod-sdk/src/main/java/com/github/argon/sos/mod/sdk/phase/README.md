# Phase

The game undergoes different "phases" or "events" while it is running. Certain resources are only available after a certain phase has passed.
For example if you want to add something to the ui, you will have to do it in `onViewSetup`, `initGameUiPresent` or `initSettlementUiPresent`.
See the [Phases](Phases.java) interface for some explanation of them.
The [PhaseManager](PhaseManager.java) exists to register your logic to these phases.

```java
public class YourModScript extends AbstractModSdkScript {
    @Override
    protected void registerPhases(PhaseManager phaseManager) {
        // will be called everytime the game saves
        phaseManager.register(Phase.ON_GAME_SAVED, new MyGameSaver());
    }
}

public class MyGameSaver implements Phases {

    private final static Logger log = Loggers.getLogger(MyGameSaver.class);

    /**
     * You have to override the correct method belonging to the phase.
     * In this case Phase.ON_GAME_SAVED belongs to "onGameSaved".
     */
    @Override
    public void onGameSaved(Path saveFilePath) {
        log.info("Game saved at %s", saveFilePath);
    }
}
```