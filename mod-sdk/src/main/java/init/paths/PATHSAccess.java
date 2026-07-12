package init.paths;

import snake2d.util.sets.LIST;

import java.nio.file.Path;

public class PATHSAccess {
    public static LIST<Path> getPaths (){
        return PATHS.i.paths;
    }
}
