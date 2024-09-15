package com.github.argon.sos.moreoptions.ui.json;

import com.github.argon.sos.mod.sdk.ui.ColumnRow;
import com.github.argon.sos.mod.sdk.ui.Table;
import com.github.argon.sos.mod.sdk.json.element.JsonElement;
import com.github.argon.sos.moreoptions.ui.json.factory.JsonUiElementSingle;
import com.github.argon.sos.moreoptions.ui.json.factory.JsonUiTemplate;
import com.github.argon.sos.moreoptions.ui.json.tab.FilesTab;
import com.github.argon.sos.moreoptions.ui.json.tab.MultiTab;
import com.github.argon.sos.moreoptions.ui.json.tab.SimpleTab;
import init.paths.PATH;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import snake2d.util.gui.renderable.RENDEROBJ;
import snake2d.util.sprite.text.StringInputSprite;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Builder
public class JsonUi {
    @Getter
    private final List<JsonUiTemplate> templates;

    @Nullable
    private final PATH rootPath;

    public static JsonUiBuilder builder() {
        return new JsonUiBuilder();
    }

    public static JsonUiBuilder builder(PATH path) {
        return builder()
            .rootPath(path)
            .pin(path);
    }

    public Table<JsonUiElementSingle<? extends JsonElement, ? extends RENDEROBJ>> table(int height) {
        return table(height, null);
    }

    public Table<JsonUiElementSingle<? extends JsonElement, ? extends RENDEROBJ>> table(int height, @Nullable StringInputSprite search) {
        Map<String, List<ColumnRow<JsonUiElementSingle<? extends JsonElement, ? extends RENDEROBJ>>>> rowMap = new LinkedHashMap<>();

        for (JsonUiTemplate template : templates) {
            rowMap.put(template.getFileName(), template.toColumnRows());
        }

        Table.TableBuilder<JsonUiElementSingle<? extends JsonElement, ? extends RENDEROBJ>> builder = Table.builder();
        return builder
            .displaySearch(search == null)
            .search(search)
            .displayHeight(height)
            .rowPadding(3)
            .columnMargin(5)
            .rowsCategorized(rowMap)
            .evenOdd(false)
            .highlight(true)
            .scrollable(true)
            .build();
    }

    public MultiTab<SimpleTab> multiTab(int height) {
        List<SimpleTab> tabs = templates.stream()
            .map((JsonUiTemplate jsonUiTemplate) -> tab(jsonUiTemplate, height))
            .collect(Collectors.toList());

        Objects.requireNonNull(rootPath);
        return new MultiTab<>(rootPath, height, tabs);
    }

    public FilesTab<SimpleTab> filesTab(int height) {
        List<SimpleTab> tabs = templates.stream()
            .map((JsonUiTemplate jsonUiTemplate) -> tab(jsonUiTemplate, height))
            .collect(Collectors.toList());

        Objects.requireNonNull(rootPath);
        return new FilesTab<>(rootPath, height, tabs);
    }

    private SimpleTab tab(JsonUiTemplate jsonUiTemplate, int height) {
        Path path = jsonUiTemplate.getPath();
        try {
            return new SimpleTab(path, height, jsonUiTemplate);
        } catch (Exception e) {
            throw new JsonUiException("Could not create tab for path " + path, e);
        }
    }

    public static class JsonUiBuilder {
        protected List<JsonUiTemplate> templates = new ArrayList<>();

        @Nullable
        private PATH pinnedPath;

        public JsonUiBuilder pin(PATH path) {
            pinnedPath = path;
            return this;
        }

        public JsonUiBuilder templates(JsonUi jsonUi) {
            templates.addAll(jsonUi.getTemplates());
            return this;
        }

        public JsonUiBuilder templates(PATH path, Consumer<JsonUiTemplate> templateConsumer) {
            return templates(path, "", templateConsumer);
        }

        public JsonUiBuilder templates(PATH path, String startsWith, Consumer<JsonUiTemplate> templateConsumer) {
            List<Path> paths = Arrays.stream(path.getFiles())
                .map(path::get)
                .collect(Collectors.toList());

            return templates(paths, startsWith, templateConsumer);
        }

        public JsonUiBuilder template(Path path, Consumer<JsonUiTemplate> templateConsumer) {
            return template(path, "", templateConsumer);
        }

        public JsonUiBuilder template(Path path, String startsWith, Consumer<JsonUiTemplate> templateConsumer) {
            if (!startsWith.isEmpty() && !path.getFileName().toString().startsWith(startsWith)) {
                return this;
            }

            JsonUiTemplate jsonUiTemplate = JsonUiTemplate.from(path);
            templateConsumer.accept(jsonUiTemplate);
            this.templates.add(jsonUiTemplate);

            return this;
        }

        public JsonUiBuilder templates(List<Path> paths, Consumer<JsonUiTemplate> templateConsumer) {
            return templates(paths, "", templateConsumer);
        }

        public JsonUiBuilder templates(List<Path> paths, String startsWith, Consumer<JsonUiTemplate> templateConsumer) {
            paths.forEach(path -> template(path, startsWith, templateConsumer));
            return this;
        }

        public JsonUiBuilder template(JsonUiTemplate jsonUiTemplate) {
            this.templates.add(jsonUiTemplate);

            return this;
        }

        public JsonUiBuilder templates(List<JsonUiTemplate> jsonUiFactories) {
            this.templates.addAll(jsonUiFactories);

            return this;
        }

        public JsonUiBuilder template(String fileName, Consumer<JsonUiTemplate> templateConsumer) {
            if (pinnedPath == null) {
                return this;
            }

            template(pinnedPath.get(fileName), templateConsumer);
            return this;
        }

        public JsonUiBuilder templates(Consumer<JsonUiTemplate> templateConsumer) {
            if (pinnedPath == null) {
                return this;
            }

            templates(pinnedPath, "", templateConsumer);
            return this;
        }

        public JsonUiBuilder templates(String startsWith, Consumer<JsonUiTemplate> templateConsumer) {
            if (pinnedPath == null) {
                return this;
            }

            List<Path> paths = Arrays.stream(pinnedPath.getFiles())
                .map(pinnedPath::get)
                .collect(Collectors.toList());

            templates(paths, startsWith, templateConsumer);
            return this;
        }
    }
}
