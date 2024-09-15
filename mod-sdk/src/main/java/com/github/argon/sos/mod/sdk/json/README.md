# JSON

Here you can find various classes for handling Songs of Syx JSON format and the standard JSON format.
You can use this to read game config files or to create your config files.

## Game JSON Mapping


The [JsonMapper](JsonMapper.java) provides various static methods to map a [Java Pojo](https://www.baeldung.com/java-pojo-class).
It is inspired by the [Jackson ObjectMapper](https://www.baeldung.com/jackson-object-mapper-tutorial).

With it, you will be able to write and read the games JSON format to and from Java objects. 
For example if you want to put a race configuration `HUMAN.txt` into a structure:

**Parts of init/race/HUMAN.txt**
```
PLAYABLE: true,
PROPERTIES: {
	HEIGHT: 6,
	WIDTH: 9,
},
BIO_FILE: Normal,
HOME: HUMAN,
TECH: [
	*,
],
```

**Java Pojo and Service**

```java
@Getter
@Setter
@Builder
public class Race {
    private boolean playable;
    private Properties properties;
    private String bioFile;
    private String home;
    private List<String> tech;
    // ...

    @Getter
    @Setter
    @Builder
    public static class Properties {
        private int height;
        private int width;
        // ...
    }
}

public class MyJsonService {
    public Race readHumanRace() {
        // read json content from init/race/HUMAN.txt
        String jsonContent = ModSdkModule.fileService().read(PATHS.RACE().init.get("HUMAN"));
        // content is null, when the file couldn't be read
        Objects.requireNonNull(jsonContent);
        // parse json string to JsonElement objects
        Json json = new Json(jsonContent);
        // map Json with parsed values into Race class
        Race race = JsonMapper.mapJson(json, Race.class);

        return race;
    }

    public Optional<Race> readHumanRace2() {
        // the JsonService can do the heavy lifting for you =)
        return ModSdkModule.jsonGameService().load(PATHS.RACE().init.get("HUMAN"), Race.class);
    }

    public void writeHumanRace2() {
        // A race pojo with some data
        Race race = Race.builder()
            .bioFile("NORMAL")
            .home("HUMAN")
            .playable(true)
            .tech(List.of("*"))
            .properties(Race.Properties.builder()
                .height(6)
                .width(9)
                .build())
            .build();

        // map the pojo to a JsonElement
        JsonElement jsonElement = JsonMapper.mapObject(race);
        // create a new Json object, so we can generate a json string from it
        Json json = new Json(jsonElement);
        // create json content string
        String jsonContent = json.write();
        // write content
        ModSdkModule.fileService().write(PATHS.RACE().init.get("HUMAN"), jsonContent);
    }

    public void writeHumanRace2() {
        Race race = Race.builder()
            .bioFile("NORMAL")
            .home("HUMAN")
            .playable(true)
            .tech(List.of("*"))
            .properties(Race.Properties.builder()
                .height(6)
                .width(9)
                .build())
            .build();

        // the JsonService can do the heavy lifting for you =)
        ModSdkModule.jsonGameService().save(PATHS.RACE().init.get("HUMAN"), race);
    }
}
```

## Game JSON Path

A [JsonPath](JsonPath.java) can be used to extract or even write Json elements from and into a Json.
So you could read a single value from a whole json tree by pointing to it.

**Parts of init/race/HUMAN.txt**
```
PLAYABLE: true,
PROPERTIES: {
	HEIGHT: 6,
	WIDTH: 9,
},
BIO_FILE: Normal,
HOME: HUMAN,
TECH: [
	*,
],
```

```java
public class MyJsonExtractor {
    public Long readHumanRaceWidth() {
        // read the games init/race/HUMAN.txt file as Json
        return ModSdkModule.jsonGameService().load(PATHS.RACE().init.get("HUMAN"))
            // extract the json element via JsonPath when present
            .flatMap(json -> {
                JsonPath jsonPath = JsonPath.get("PROPERTIES.WIDTH");
                Optional<JsonLong> jsonLong = jsonPath.get(json, JsonLong.class);
                return jsonLong;
            })
            // extract the actual value from the json element when present
            .map(JsonLong::getValue)
            // when something isn't present in the chain, use 0 as fallback
            .orElse(0L);
    }

    public void writeHumanRaceWidth(Long width) {
        // read the games init/race/HUMAN.txt file as Json
        ModSdkModule.jsonGameService().load(PATHS.RACE().init.get("HUMAN"))
            .ifPresent(json -> {
                // put the Long value into the fitting JsonElement
                JsonLong jsonLong = new JsonLong(width);

                // insert the value into the read json object
                JsonPath jsonPath = JsonPath.get("PROPERTIES.WIDTH");
                jsonPath.put(json, jsonLong);

                // save the new changed object into init/race/HUMAN.txt json file
                ModSdkModule.jsonGameService().save(PATHS.RACE().init.get("HUMAN"), json);
            });
    }
}
```

## Standard JSON

For dealing with standard JSON there is the [JacksonService](JacksonService.java). 
It uses [FasterXML Jackson](https://mvnrepository.com/artifact/com.fasterxml.jackson.core) to do the parsing and mapping.

`YourConfig.json`
```json
{
  "name": "foo",
  "dimensions": {
    "x": 100,
    "y": 200
  }
}
```

```java
import com.github.argon.sos.mod.sdk.ModSdkModule;

public class YourModScript extends AbstractModSdkScript {
    private final static Logger log = Loggers.getLogger(YourModScript.class);

    @Override
    public void initBeforeGameCreated() {
        Path jsonConfigPath = PATHS.local().SETTINGS.get().resolve("YourConfig.json");
        
        // read JSON file "YourConfig.json" from games "settings" folder
        YourConfigJson yourConfigJson = ModSdkModule.jacksonService()
            .load(jsonConfigPath, YourConfigJson.class)
            .orElse(null);
        log.info("Name: %s", yourConfigJson.getName());
        
        // change name and save in file
        yourConfigJson.setName("bar");
        ModSdkModule.jacksonService().save(jsonConfigPath, yourConfigJson);
    }
}

/**
 * Your JSON structure
 */
@Data
public class YourConfigJson {
    private String name;
    private Dimensions dimensions;

    public static class Dimensions {
        private int x;
        private int y;
    }
}
```


