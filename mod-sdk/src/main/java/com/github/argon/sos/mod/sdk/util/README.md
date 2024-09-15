# Util

Contains static classes with helper methods. See the code documentation of the classes itself.
Here are some useful examples:

```java
public class YourModScript extends AbstractModSdkScript {

    private final static Logger log = Loggers.getLogger(AbstractModSdkScript.class);

    @Override
    public void initBeforeGameCreated() {
        super.initBeforeGameCreated();

        // create simple lists
        List<String> list = Lists.of("foo", "bar", "blub");
        // create simple maps
        Map<String, Integer> map = Maps.of(
            "key1", 1,
            "key2", 2,
            "key3", 3);
        // create simple sets
        Set<String> set = Sets.of("foo", "bar", "blub");
        
        // read a value from a private inaccessible "name" field
        String name = ReflectionUtil.getDeclaredFieldValue("name", new FooClass())
            .map(String.class::cast)
            .orElse(null);
        log.info("Name: %s", name);
    }

    public static class FooClass {
        private String name = "test";
    }
}
```
