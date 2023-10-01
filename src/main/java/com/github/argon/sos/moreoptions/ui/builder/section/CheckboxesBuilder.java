package com.github.argon.sos.moreoptions.ui.builder.section;

import com.github.argon.sos.moreoptions.Dictionary;
import com.github.argon.sos.moreoptions.game.ui.Checkbox;
import com.github.argon.sos.moreoptions.game.ui.GridRow;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.UiBuilder;
import com.github.argon.sos.moreoptions.ui.builder.element.LabeledCheckboxBuilder;
import com.github.argon.sos.moreoptions.ui.builder.element.ScrollableBuilder;
import com.github.argon.sos.moreoptions.util.UiUtil;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import util.gui.table.GScrollRows;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CheckboxesBuilder implements UiBuilder<GuiSection, Checkbox> {

    private final Map<String, LabeledCheckboxBuilder.Definition> definitions;

    private final int displayHeight;

    /**
     * Builds a section with a list of checkboxes with titles according to the given {@link LabeledCheckboxBuilder.Definition}s
     */
    public BuildResult<GuiSection, Checkbox> build() {
        Map<String, Checkbox> elements = new HashMap<>();
        
        // map and order entries by dictionary title
        Map<String, LabeledCheckboxBuilder.Definition> definitions = this.definitions.entrySet()
            .stream().sorted(Comparator.comparing(entry -> entry.getValue().getLabelDefinition().getTitle()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        List<GridRow> gridRows = new ArrayList<>();

        definitions.forEach((key, definition) -> {
            BuildResult<GridRow, Checkbox> buildResult = LabeledCheckboxBuilder.builder().definition(definition).build().build();
            Checkbox checkbox = buildResult.getElement().orElseThrow(() -> new RuntimeException(""));
            elements.put(key, checkbox);
            gridRows.add(buildResult.getResult());
        });

        int maxWidth = UiUtil.getMaxColumnWidth(gridRows);
        int maxHeight = UiUtil.getMaxColumnHeight(gridRows);

        for (int i = 0; i < gridRows.size(); i++) {
            GridRow row = gridRows.get(i);
            row.initGrid(maxWidth, maxHeight);

            if (i % 2 == 0) {
                row.background(COLOR.WHITE15);
            }
        }

        // build scrollable ui elements
        GScrollRows gScrollRows = ScrollableBuilder.builder()
            .height(displayHeight)
            .rows(gridRows)
            .build().build().getResult();
        GuiSection section = new GuiSection();
        section.add(gScrollRows.view());

        return BuildResult.<GuiSection, Checkbox>builder()
            .result(section)
            .elements(elements)
            .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        @lombok.Setter
        @Accessors(fluent = true)
        private Map<String, LabeledCheckboxBuilder.Definition> definitions;

        @lombok.Setter
        @Accessors(fluent = true)
        private int displayHeight = 100;

        public CheckboxesBuilder.Builder translate(Map<String, LabeledCheckboxBuilder.Definition> definitions) {
            Dictionary dictionary = Dictionary.getInstance();
            dictionary.translate(definitions.values());

            return definitions(definitions);
        }

        public CheckboxesBuilder build() {
            assert definitions != null : "definitions must not be null";
            assert displayHeight > 0 : "displayHeight must be greater than 0";

            return new CheckboxesBuilder(definitions, displayHeight);
        }
    }
}
