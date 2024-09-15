# Data

Various data structures.

## [CacheValue](CacheValue.java)

Holds a value for a certain time until it fetches a new one.

```java
public class YourModScript extends AbstractModSdkScript {
    private final static Logger log = Loggers.getLogger(YourModScript.class);
    
    // hold value for 1000ms then call provider function to get a new one
    private final CacheValue<String> stringCache = new CacheValue<>(1000, () -> "test " + System.currentTimeMillis());

    @Override
    public void onGameUpdate(double time) {
        log.info("CacheValue: %s", stringCache.get());
    }
}
```

## [TreeNode](TreeNode.java)

Represents a tree like structure, which can be iterated though as a flat list.
A folder structure for example is a tree:

```
Mod Name            
├── V66             
|   ├── assets      
|   ├── campaigns   
|   ├── examples    
|   ├── saves       
|   └── script      
└── _Info.txt     
```

```java
public class YourModScript extends AbstractModSdkScript {
    private final static Logger log = Loggers.getLogger(YourModScript.class);

    @Override
    public void initBeforeGameCreated() {
        TreeNode<String> modFolder = new TreeNode<>("Mod Name");
        TreeNode<String> v66Folder = modFolder.node("V66");
        v66Folder.node("assets");
        v66Folder.node("campaigns");
        v66Folder.node("examples");
        v66Folder.node("saves");
        v66Folder.node("script");
        modFolder.node("_Info.txt");

        for (TreeNode<String> node : modFolder) {
            log.info("Node: %s", node.get());
        }
    }
}
```





