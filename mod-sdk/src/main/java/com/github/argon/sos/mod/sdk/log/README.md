# Log

Logging can be a very helpful tool when developing and analyzing problems. The game provides only a rudimentary logger, which suits it needs.
I'm used to log with different log [Level](Level.java)s and being able to control the log levels for each logger or package.
So I've built my own logger one with hookers.

```java
public class YourModScript extends AbstractModSdkScript {
    // build a new logger
    private final static Logger log = Loggers.getLogger(YourModScript.class);

    @Override
    public void onViewSetup() {
        // Set the level of all loggers to TRACE
        // Each message starting at "trace" will be logged. So basically everything.
        Loggers.setLevels(Level.TRACE);
        // Set the level of loggers in the "your.mod" package to ERROR
        // Each message starting at "error" will be logged. So only ERROR and CRITICAL logs.
        Loggers.setLevels("your.mod", Level.ERROR);
        // Forces the logger to use another writer; default is System.out and System.err 
        // the log message format is very rudimentary... sorry ^^
        // The %s will be replaced by the following: [PREFIX|03:44:55.742]LoggerName[INFO] Log Message
        log.setWriter(new GameLOG("PREFIX", "[%s|%s]%s[%s] %s", "LoggerName"));

        // Log levels by lowest to highest
        log.trace("For logging whole data objects to follow the flow / traces: %s", Range.builder().build());
        log.debug("For logging process steps like: %s", "Saving file to the/file/path.txt");
        log.info("For logging informational stuff like the game version: %s", GAME.version());
        log.warn("When something goes wrong, but it isn't that bad and the mod can still continue.");
        log.error("When something goes wrong and you want the log to appear as an error when the game exits.",
            new RuntimeException("You can also log exceptions =)"));
        log.critical("This is really bad stuff, like an OutOfMemory error", new OutOfMemoryError("BOOM!"));
    }
}
```