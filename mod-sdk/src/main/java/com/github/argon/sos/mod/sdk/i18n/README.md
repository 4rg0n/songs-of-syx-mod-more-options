# I18n

The games translation system doesn't work very well for code mods. 
So this uses good old [Resource Bundles](https://www.baeldung.com/java-resourcebundle) to read translations.
These are just `*.properties` files following a specific naming schema. 
You will have one file for each language and one default language without any prefix:

```
your-mod-i18n.properties
your-mod-i18n_de.properties
```

They must be in the `src/main/resources` folder to be readable.

`your-mod-i18n.properties`
```properties
YourModScript.helloMessage=Hello World!
```

`your-mod-i18n_de.properties`
```properties
YourModScript.helloMessage=Hallo Welt!
```

```java
public class YourModScript extends AbstractModSdkScript {

    private final static I18nTranslator i18n = YourModModule.i18n().get(YourModScript.class);

    @Override
    protected void registerPhases(PhaseManager phaseManager) {
        phaseManager
            .register(Phase.INIT_BEFORE_GAME_CREATED, ModModule.i18nMessages());
    }

    @Override
    public void initSettlementUiPresent() {
        super.initSettlementUiPresent();
        // will show a translated "Hello World!" notification
        ModSdkModule.notificator().notify(i18n.t("YourModScript.helloMessage"));
    }
}

public class YourModModule {

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static I18nMessageBundle i18nMessages = new I18nMessageBundle("your-mod-i18n", ModSdkModule.gameApis().lang());

    @Getter(lazy = true)
    @Accessors(fluent = true)
    private final static I18n i18n = new I18n(i18nMessages());
}
```

