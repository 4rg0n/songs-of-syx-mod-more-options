# Mod SDK

![Mod SDK Logo](../../../../../../../../../doc/img/mod-sdk-logo.png)

**But what is it?**
It provides some pre-build components and patterns to make it easier writing a Java code mod.

## DISCLAIMER for V0

Version 0 is the first release for testing the waters and gathering feedback. APIs and classes are not stable yet and may change.

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
You **must** extend from it to make use of it. 
This class is some kind of **Main** entry point for your mod. It implements the game `script.SCRIPT` interface.
The game will scan for classes having this interface, instantiate them and execute certain methods of it.

Theoretically it is possible to have multiple classes implementing the `script.SCRIPT` interface in your mod.
I didn't test this use case yet though :x

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
    
    /*
        The game will go through different "Phases" when it is booting and running.
        Some resources might be just available after a certain phase has passed. 
        So you have to design your code around this. The PhaseManager helps you to initialize 
        your code or data on certain events.
     */
    @Override
    protected void registerPhases(PhaseManager phaseManager) {
        // here you can register classes to "phases" e.g. "on game saved"
        // this is useful for encapsulating your code into different classes
        phaseManager.register(Phase.ON_GAME_SAVED, new MyGameSaver());
    }
    
    /*
        OPTIONAL:
        This will force the game to enable and initialize the mod.
        Without it, the user will need to manually enable the "Script" when starting a new game.
     */
    @Override
    public boolean forceInit() {
        return true;
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









