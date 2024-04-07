package menu.json;

import com.github.argon.sos.moreoptions.game.ui.ColumnRow;
import com.github.argon.sos.moreoptions.game.ui.Table;
import init.paths.PATH;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import menu.json.tab.MultiTab;
import menu.json.tab.SimpleTab;
import org.jetbrains.annotations.Nullable;
import snake2d.util.sprite.text.StringInputSprite;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Builder
@RequiredArgsConstructor
public class JsonUiSection {

    private final List<JsonUiTemplate> jsonUiTemplates;
    
    private final PATH folderPath;

    public Table<Void> table(int height) {
        return table(height, null);
    }

    public Table<Void> table(int height, @Nullable StringInputSprite search) {
        Map<String, List<ColumnRow<Void>>> rowMap = jsonUiTemplates.stream().collect(Collectors.toMap(
            JsonUiTemplate::getFileName,
            JsonUiTemplate::toColumnRows,
            (e1, e2) -> {throw new RuntimeException("TODO");},
            LinkedHashMap::new
        ));
        boolean displaySearch = (search == null);

        Table.TableBuilder<Void> builder = Table.builder();
        return builder
            .displaySearch(displaySearch)
            .search(search)
            .displayHeight(height)
            .rowPadding(3)
            .columnMargin(5)
            .rowsCategorized(rowMap)
            .highlight(true)
            .evenOdd(true)
            .scrollable(true)
            .build();
    }
    
    public MultiTab<SimpleTab> folder(int height) {
        List<SimpleTab> tabs = jsonUiTemplates.stream()
            .map((JsonUiTemplate jsonUiTemplate) -> file(jsonUiTemplate, height))
            .collect(Collectors.toList());

        return new MultiTab<>(folderPath, height, tabs);
    }

    private SimpleTab file(JsonUiTemplate jsonUiTemplate, int height) {
        return new SimpleTab(jsonUiTemplate.getPath(), height, jsonUiTemplate);
    }

    public static class JsonUiSectionBuilder {
        protected List<JsonUiTemplate> jsonUiTemplates = new ArrayList<>();

        private PATH folderPath;
        
        public JsonUiSectionBuilder templates(PATH path, Consumer<JsonUiTemplate> templateConsumer) {
            return templates(path, "", templateConsumer);
        }

        public JsonUiSectionBuilder templates(PATH path, String startsWith, Consumer<JsonUiTemplate> templateConsumer) {
            this.folderPath = path;
            List<Path> paths = Arrays.stream(path.getFiles())
                .map(path::get)
                .collect(Collectors.toList());

            return templates(paths, startsWith, templateConsumer);
        }

        public JsonUiSectionBuilder template(Path path, Consumer<JsonUiTemplate> templateConsumer) {
           return template(path, "", templateConsumer);
        }

        public JsonUiSectionBuilder template(Path path, String startsWith, Consumer<JsonUiTemplate> templateConsumer) {
            if (!startsWith.isEmpty() && !path.getFileName().toString().startsWith(startsWith)) {
                return this;
            }

            JsonUiTemplate jsonUiTemplate = JsonUiTemplate.from(path);
            templateConsumer.accept(jsonUiTemplate);
            this.jsonUiTemplates.add(jsonUiTemplate);

            return this;
        }

        public JsonUiSectionBuilder templates(List<Path> paths, Consumer<JsonUiTemplate> templateConsumer) {
            return templates(paths, "", templateConsumer);
        }

        public JsonUiSectionBuilder templates(List<Path> paths, String startsWith, Consumer<JsonUiTemplate> templateConsumer) {
            paths.forEach(path -> template(path, startsWith, templateConsumer));
            return this;
        }

        public JsonUiSectionBuilder template(JsonUiTemplate jsonUiTemplate) {
            this.jsonUiTemplates.add(jsonUiTemplate);

            return this;
        }

        public JsonUiSectionBuilder templates(List<JsonUiTemplate> jsonUiFactories) {
            this.jsonUiTemplates.addAll(jsonUiFactories);

            return this;
        }
    }
}
