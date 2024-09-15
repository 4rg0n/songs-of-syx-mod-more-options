# Game Asset

For accessing and managing game files like configs or sprites.
You can access the game through the [GameAssets](GameAssets.java) class.

```java
public class YourModScript extends AbstractModSdkScript {
    private final static Logger log = Loggers.getLogger(YourModScript.class);

    @Override
    public void onViewSetup() {
        // read config as JsonObject
        JsonObject human = GameAssets.init().race().json("HUMAN").orElse(null);
        log.info("Is HUMAN race playable? %s", human.get("PLAYABLE"));
        
        // create a new game folder and read the file names from it
        List<String> dictionaryFiles = GameFolder.of(PATHS.DICTIONARY()).fileNames();
        log.info("Dictionary files: %s", dictionaryFiles);
    }
}
```
