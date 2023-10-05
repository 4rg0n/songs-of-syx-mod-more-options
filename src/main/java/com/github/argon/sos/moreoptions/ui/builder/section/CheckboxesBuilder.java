package com.github.argon.sos.moreoptions.ui.builder.section;

import com.github.argon.sos.moreoptions.Dictionary;
import com.github.argon.sos.moreoptions.game.ui.Checkbox;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.UiBuilder;
import com.github.argon.sos.moreoptions.ui.builder.element.LabeledCheckboxBuilder;
import com.github.argon.sos.moreoptions.ui.builder.element.TableBuilder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import snake2d.util.gui.GuiSection;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CheckboxesBuilder implements UiBuilder<GuiSection, Map<String, Checkbox>> {

    private final Map<String, LabeledCheckboxBuilder.Definition> definitions;

    private final int displayHeight;

    /**
     * Builds a section with a list of checkboxes with titles according to the given {@link LabeledCheckboxBuilder.Definition}s
     */
    public BuildResult<GuiSection, Map<String, Checkbox>> build() {
        Map<String, Checkbox> elements = new HashMap<>();
        
        // map and order entries by dictionary title
        Map<String, LabeledCheckboxBuilder.Definition> definitions = this.definitions.entrySet()
            .stream().sorted(Comparator.comparing(entry -> entry.getValue().getLabelDefinition().getTitle()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        List<List<? extends GuiSection>> rows = new ArrayList<>();
        definitions.forEach((key, definition) -> {
            BuildResult<List<GuiSection>, Checkbox> buildResult = LabeledCheckboxBuilder.builder()
                .definition(definition)
                .build();
            elements.put(key, buildResult.getInteractable());
            rows.add(buildResult.getResult());
        });

        GuiSection table = TableBuilder.builder()
            .evenOdd(true)
            .displayHeight(displayHeight)
            .rows(rows)
            .build()
            .getResult();

        return BuildResult.<GuiSection, Map<String, Checkbox>>builder()
            .result(table)
            .interactable(elements)
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

        public BuildResult<GuiSection, Map<String, Checkbox>> build() {
            assert definitions != null : "definitions must not be null";
            assert displayHeight > 0 : "displayHeight must be greater than 0";

            return new CheckboxesBuilder(definitions, displayHeight).build();
        }
    }
}
