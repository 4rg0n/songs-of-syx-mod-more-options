# Mod SDK

![Mod SDK Logo](../../../../../../../../../doc/img/mod-sdk-logo.png)

**But what is it?**
It provides some pre-build components and patterns to make it easier writing a Java code mod.

## Packages

* [Config](config) - For handling your mod configuration files
* [Data](data) - Some pre built data structures
* [File](file) - For reading and writing files
* [Game](game) - For everything related to the game
* [I18n](i18n) - For translating your mod texts
* [Json](json) - For everything around JSON
* [Log](log) - For logging messages and errors
* [Metric](metric) - For collecting game stats as CSV
* [Phase](phase) - For dealing with the games different states and phases
* [Properties](properties) - For reading and writing Java properties
* [Ui](ui) - Everything related to the game UI
* [Util](util) - Some little helper tools

## Game Class Replacements

These overwrite vanilla game code.

* [Game Boosting](../../../../../../game/boosting)
* [Settlement Weather](../../../../../../settlement/weather)
* [Snake2d](../../../../../../snake2d)

## Used Third Party Runtime Dependencies

**IMPORTANT:** Other mods using this dependencies will be incompatible.
The SDK uses as less third party dependencies as possible to have a high compatibility to other mods.

* [org.jetbrains:annotations](https://github.com/JetBrains/java-annotations) - Helper annotations for marking certain code flows
* [com.fasterxml.jackson.core:jackson-databind](https://github.com/FasterXML/jackson-databind) - For processing JSON
* [com.fasterxml.jackson.dataformat:jackson-dataformat-properties](https://github.com/FasterXML/jackson-dataformats-text/) - For processing Java properties

# Getting Started

## [AbstractModSdkScript](AbstractModSdkScript.java)

The `AbstractModSdkScript` contains some of the required functionality to make the Mod SDK work.
You have to extend from it to make use of it.

```java
public class YourModScript extends AbstractModSdkScript {

    public final static INFO MOD_INFO = new INFO("Your Mod", "Your mod description.");

    @Override
    public CharSequence name() {
        return MOD_INFO.name;
    }

    @Override
    public CharSequence desc() {
        return MOD_INFO.desc;
    }

    @Override
    protected void registerPhases(PhaseManager phaseManager) {
        // here you can register classes to "phases" e.g. "on game saved"
        // this is useful for encapsulating your code into different classes
        phaseManager.register(Phase.ON_GAME_SAVED, new MyGameSaver());
    }
}

public class MyGameSaver implements Phases {

    private final static Logger log = Loggers.getLogger(MyGameSaver.class);

    @Override
    public void onGameSaved(Path saveFilePath) {
        log.info("Game saved at %s", saveFilePath);
    }
}
```

## [ModSdkModule](ModSdkModule.java)

The `ModSdkModule` contains several tools for you to help you modding. See the code documentation of the class to get an overview.
For example the [Notificator](ui/Notificator.java):

```java
public class YourModScript extends AbstractModSdkScript {
    @Override
    public void initSettlementUiPresent() {
        super.initSettlementUiPresent();
        
        // will display a notification box with a message
        ModSdkModule.notificator().notify("Hello World!");
    }
}
```

It caches [Singleton](https://www.baeldung.com/java-singleton) instances of various services and other classes. 
And provides a `Factory` for building these classes. I would recommend that you also add your own kind of `YourModModule` with a similiar structure.
This way you can easily wire up the components of the Mod SDK for your needs and also have a single point to look for your implementations.
For example providing your own [internationalization](i18n):

```java
public class YourModModule {
    
    // This @Getter is "lombok" magic. It will generate a lazy getter, which will only create this instance, when the getter is called the first time.
    // It will then serve the same instance instead of creating a new one everytime 
    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static I18nMessageBundle i18nMessages = new I18nMessageBundle("your-mod-i18n", ModSdkModule.gameApis().lang());

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static I18n i18n = new I18n(i18nMessages());
}

public class YourModScript extends AbstractModSdkScript {

    private final I18nTranslator i18n = YourModModule.i18n().get(YourModScript.class);

    @Override
    public void initSettlementUiPresent() {
        super.initSettlementUiPresent();
        
        // will display notification box with a translated message
        ModSdkModule.notificator().notify(i18n.t("YourModScript.notify.helloWorld"));
    }
}
```

Try to separate your code into domains ([SoC](https://www.geeksforgeeks.org/separation-of-concerns-soc/)). Like everything config related comes into one package `config`. 
And use [Dependency Injection](https://www.vogella.com/tutorials/DependencyInjection/article.html) where possible. This way you keep your code organized and reusable.
It makes it also easier to test when you can simulate certain behavior you outsourced into another class.









