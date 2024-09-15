# Booster

For adding your own boosters to present game boosters.

```java

public class YourModScript extends AbstractModSdkScript {
    @Override
    public void initBeforeGameCreated() {
        BSourceInfo boosterInfo = new BSourceInfo("Custom Happiness Booster");
        FactionBooster customHappinessBooster = new FactionBooster(BOOSTABLES.BEHAVIOUR().HAPPI, boosterInfo, 0, 1000, 2, BoostMode.PERCENT);
        
        // will increase happiness by 1000% for the player faction
        customHappinessBooster.set(FACTIONS.player(), 1000);
    }
}
```