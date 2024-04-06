package menu.json;

import init.paths.PATH;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class JsonUi {
    public static JsonUiBuilder builder(PATH path) {
        return new JsonUiBuilder(path);
    }

    public static class JsonUiBuilder extends JsonUiSection.JsonUiSectionBuilder {
        private final PATH path;

        public JsonUiBuilder(PATH path) {
            this.path = path;
            this.folderPath(path);
        }

        public JsonUiBuilder template(String fileName, Consumer<JsonUiTemplate> templateConsumer) {
            template(path.get(fileName),  templateConsumer);
            return this;
        }

        public JsonUiBuilder templates(Consumer<JsonUiTemplate> templateConsumer) {
            templates(path, "", templateConsumer);
            return this;
        }

        public JsonUiBuilder templates(String startsWith, Consumer<JsonUiTemplate> templateConsumer) {
            List<Path> paths = Arrays.stream(path.getFiles())
                .map(path::get)
                .collect(Collectors.toList());

            templates(paths, startsWith, templateConsumer);
            return this;
        }
    }
}
