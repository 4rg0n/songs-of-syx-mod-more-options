# Config

This can help you to manage your configurations if you have some and don't want to store them into the save file of the game.

## [JsonConfigStore](json/JsonConfigStore.java)

The `JsonConfigStore` is a mighty tool to manage any kind of JSON configurations. 
It caches configs, so it doesn't need to be read from the file system everytime a config is needed.

`Your Config.json`
```json
{
  "name": "A Name",
  "amounts": [69, 42, 420, 1337]
}
```

```java
public class YourModScript extends AbstractModSdkScript {
    private final static Logger log = Loggers.getLogger(YourModScript.class);
    private final JsonConfigStore jsonConfigStore = ModSdkModule.jsonConfigStore();

    @Override
    public void initBeforeGameCreated() {
        // bind your json object to the store under the games settings folder in a file called "Your Config.json"
        // "true" means that the file shall be written as a backup when needed
        jsonConfigStore.bind(YourJson.class, PATHS.local().SETTINGS.get().resolve("Your Config.json"), true);
        
        // read the files content
        YourJsonConfig yourJson = jsonConfigStore.get(YourJsonConfig.class).orElse(null);
        log.info("Name: %s", yourJson.getName());
        
        // save changes back to the JSON file
        yourJson.setName("Another Name");
        jsonConfigStore.save(yourJson);
    }
    
    @Override
    public void onCrash(Throwable exception) {
        // save backups on a game crash and don't delete the original files
        jsonConfigStore.saveBackups(false);
    }
}

/**
 * Represents your JSON data structure
 */
@Data
public class YourJsonConfig {
    private String name;
    private List<Integer> amounts;
}
```

## [ConfigVersionHandlers](ConfigVersionHandlers.java)

The `ConfigVersionHandlers` provides a way to be backwards compatible with your configuration.
As each config structure is unique and has special requirements, this is only a very simple tool to get you started.
So you will need to write a lot of your own logic doing the actual mapping between the different config structures. 

```java
public class YourModScript extends AbstractModSdkScript {
    private final static Logger log = Loggers.getLogger(YourModScript.class);

    @Override
    public void initBeforeGameCreated() {
        // bind your different json config objects to the same file
        ModSdkModule.jsonConfigStore().bind(YourJsonV1Config.class, PATHS.local().SETTINGS.get().resolve("Your Config.json"), true);
        ModSdkModule.jsonConfigStore().bind(YourJsonV2Config.class, PATHS.local().SETTINGS.get().resolve("Your Config.json"), true);
        
        // assume the config you are reading is in version 1, it will map it to the latest version 2
        YourJsonV2Config config = YourModModule.configVersionHandlers().handle(1).orElse(null);
        log.info("Config: %s", config);
    }
}

/**
 * Your config v1 structure
 */
@Data
@AllArgsConstructor
public class YourJsonV1Config {
    private final Integer version = 1;
    private String name;
    private List<Integer> amounts;
}

/**
 * Your config v2 structure
 */
@Data
@AllArgsConstructor
public class YourJsonV2Config {
    private final Integer version = 2;
    private String name;
    // amounts removed
}

public class YourModModule {

    /**
     * Build the version handlers here. So you can reuse them.
     */
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static ConfigVersionHandlers<YourJsonConfig> configVersionHandlers = new ConfigVersionHandlers<YourJsonConfig>()
        .register(1, version -> ModSdkModule.jsonConfigStore().get(YourJsonV1Config.class)
            .map(yourJsonV1Config -> new YourJsonV2Config(yourJsonV1Config.getName())))
        .register(2, version -> ModSdkModule.jsonConfigStore().get(YourJsonV2Config.class));
}
```