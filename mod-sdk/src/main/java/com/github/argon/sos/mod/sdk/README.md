# Content

* [Config](config/README.md)
* [Data](data/README.md)
* [File](file/README.md)
* [Game](game/README.md)
* [I18n](i18n/README.md)
* [Json](json/README.md)
* [Log](log/README.md)
* [Metric](metric/README.md)
* [Phase](phase/README.md)
* [Properties](properties/README.md)
* [Ui](ui/README.md)
* [Util](util/README.md)

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





