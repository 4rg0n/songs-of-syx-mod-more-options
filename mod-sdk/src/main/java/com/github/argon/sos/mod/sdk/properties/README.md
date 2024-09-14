# Properties

Properties or "Java Properties" are a format to store configuration in a file.

File: `src/main/resources/your-mod.peroperties`
```properties
modUrl=https://your.mod
race.name=Blue Elves
race.desc=Maybe they are drunk?
```

You can read them from within a `.*jar` file via the [PropertiesStore](PropertiesStore.java).
In its default setup, the `ModSdkModule.propertiesStore()` can **only read** properties from within a `.*jar` file.
This means you have to put your `your-mod.peroperties` file into the `src/main/resources` folder of your Java module.
It is possible to create a `PropertiesStore` with a `PropertiesService` containing a [FileService](../file/FileService.java) to read files from outside the `*.jar` file.

```java
public class YourModScript extends AbstractModSdkScript {

    private final static Logger log = Loggers.getLogger(AbstractModSdkScript.class);

    @Override
    public void initBeforeGameCreated() {
        // bind the property class to the actual file and cache it when read
        ModSdkModule.propertiesStore().bind(YourModProperties.class, Paths.get("your-mod.properties"), true);
        super.initBeforeGameCreated();

        // read the actual file into your property class
        YourModProperties yourModProperties = ModSdkModule.propertiesStore().get(YourModProperties.class)
            .orElse(null);
        log.info("Mod Url: %s", yourModProperties.getModUrl());
        
        // Example on how to create a PropertiesStore to read files from outside a *.jar file
        PropertiesStore externalPropertiesStore = ModSdkModule.Factory.newPropertiesStore(
            ModSdkModule.Factory.newPropertiesService(
                ModSdkModule.fileService(),
                ModSdkModule.jacksonPropertiesMapper()));
    }
}

/**
 * This class reflects your *.properties file structure
 */
@Data
public class YourModProperties {
    private String modUrl;
    private Race race;

    @Data
    public class Race {
        private String name;
        private String desc;
    }
}
```

