# File

For reading and writing into files. Classes here implement the [IOService](IOService.java) interface and can be exchanged.
So it's possible for other services to read from different sources.

## [ResourceService](ResourceService.java)

The `ResourceService` can be used to read files from within the `*.jar` file. 
By default, everything from within the `src/main/resources` folder will be copied into the packaged `*.jar` file.

```java
public class YourModScript extends AbstractModSdkScript {
    private final static Logger log = Loggers.getLogger(YourModScript.class);

    @Override
    public void initBeforeGameCreated() {
        // the file must be in src/main/resources/text.txt
        String content = ModSdkModule.resourceService().read(Paths.get("text.txt"));
        log.info("Content:\n%s", content);
    }
}
```

## [FileService](FileService.java)

The `FileService` is used for reading and writing files anywhere on the operating system the game has access to.

```java
public class YourModScript extends AbstractModSdkScript {
    private final static Logger log = Loggers.getLogger(YourModScript.class);

    @Override
    public void initBeforeGameCreated() {
        Path license = Paths.get("C:/Program Files (x86)/Steam/steamapps/common/Songs of Syx/info/License.txt");
        
        // read the content from the SoS License.txt
        String content = ModSdkModule.fileService().read(license);
        log.info("Content:\n%s", content);

        // overwrite the content from the SoS License.txt
        ModSdkModule.fileService().write(license, "(╯°□°)╯︵ ┻━┻");
    }
}
```



