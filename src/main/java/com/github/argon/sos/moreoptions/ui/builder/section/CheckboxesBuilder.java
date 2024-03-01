package com.github.argon.sos.moreoptions.ui.builder.section;

import com.github.argon.sos.moreoptions.Dictionary;
import com.github.argon.sos.moreoptions.game.ui.Checkbox;
import com.github.argon.sos.moreoptions.game.ui.ColumnRow;
import com.github.argon.sos.moreoptions.game.ui.Table;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.UiBuilder;
import com.github.argon.sos.moreoptions.ui.builder.element.LabeledCheckboxBuilder;
import com.github.argon.sos.moreoptions.ui.builder.element.TableBuilder;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import snake2d.util.sprite.text.StringInputSprite;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CheckboxesBuilder implements UiBuilder<Table, Map<String, Checkbox>> {

    private final Map<String, LabeledCheckboxBuilder.Definition> definitions;

    private final int displayHeight;

    private final boolean evenWidth;

    @Nullable
    private final StringInputSprite search;

    /**
     * Builds a section with a list of checkboxes with titles according to the given {@link LabeledCheckboxBuilder.Definition}s
     */
    public BuildResult<Table, Map<String, Checkbox>> build() {
        Map<String, Checkbox> elements = new HashMap<>();
        
        // map and order entries by dictionary title
        Map<String, LabeledCheckboxBuilder.Definition> definitions = this.definitions.entrySet()
            .stream().sorted(Comparator.comparing(entry -> entry.getValue().getLabelDefinition().getTitle()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        List<ColumnRow> rows = new ArrayList<>();
        definitions.forEach((key, definition) -> {
            BuildResult<ColumnRow, Checkbox> buildResult = LabeledCheckboxBuilder.builder()
                .definition(definition)
                .build()
                .toColumnRow();
            ColumnRow columnRow = buildResult.getResult();

            if (search != null) {
                columnRow.setSearchTerm(key);
            }

            elements.put(key, buildResult.getInteractable());
            rows.add(columnRow);
        });

        Table table = TableBuilder.builder()
            .evenOdd(true)
            .evenColumnWidth(evenWidth)
            .displayHeight(displayHeight)
            .search(search)
            .columnRows(rows)
            .build()
            .getResult();

        return BuildResult.<Table, Map<String, Checkbox>>builder()
            .result(table)
            .interactable(elements)
            .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Setter
    public static class Builder {

        @Accessors(fluent = true)
        private Map<String, LabeledCheckboxBuilder.Definition> definitions;

        @Accessors(fluent = true)
        private int displayHeight = 100;

        @Accessors(fluent = true)
        private boolean evenWidth = false;

        @Accessors(fluent = true)
        private StringInputSprite search;

        public CheckboxesBuilder.Builder translate(Map<String, LabeledCheckboxBuilder.Definition> definitions) {
            Dictionary dictionary = Dictionary.getInstance();
            dictionary.translate(definitions.values());

            return definitions(definitions);
        }

        public BuildResult<Table, Map<String, Checkbox>> build() {
            assert definitions != null : "definitions must not be null";
            assert displayHeight > 0 : "displayHeight must be greater than 0";

            return new CheckboxesBuilder(definitions, displayHeight, evenWidth, search).build();
        }
    }
}
