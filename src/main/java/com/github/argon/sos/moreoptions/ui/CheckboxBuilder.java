package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.moreoptions.Dictionary;
import com.github.argon.sos.moreoptions.game.ui.Checkbox;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GText;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class CheckboxBuilder {

    @Getter
    private final Map<String, Checkbox> checkboxes = new TreeMap<>();

    private final Dictionary dictionary = Dictionary.getInstance();

    /**
     * Builds a section with a list of checkboxes with titles according to the given {@link Definition}s
     */
    public GuiSection build(Map<String, Definition> checkboxDescriptions) {
        checkboxes.clear();
        GuiSection nameSection = new GuiSection();
        GuiSection checkSection = new GuiSection();

        // map and order entries by dictionary title
        Map<String, Dictionary.Entry> translatedEntries = checkboxDescriptions.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> dictionary.get(entry.getKey())));
        Map<String, Dictionary.Entry> sortedTranslatedEntries = translatedEntries.entrySet()
            .stream().sorted(Comparator.comparing(entry -> entry.getValue().getTitle()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        sortedTranslatedEntries.forEach((key, dictEntry) -> {
            Definition definition = checkboxDescriptions.get(key);
            Checkbox checkbox = new Checkbox(definition.isEnabled());
            GuiSection text = new GuiSection();
            text.addRight(0, new GText(UI.FONT().S, dictEntry.getTitle()));

            nameSection.addDown(5, text);
            checkSection.addDown(5, checkbox);

            if (dictEntry.getDescription() != null) {
                checkbox.hoverInfoSet(dictEntry.getDescription());
                text.hoverInfoSet(dictEntry.getDescription());
            }

            checkboxes.put(key, checkbox);
        });

        GuiSection section = new GuiSection();

        section.addRight(0, nameSection);
        section.addRight(10, checkSection);

        return section;
    }

    @Data
    @Builder
    public static class Definition {
        private String title;
        private String description;
        private boolean enabled;
    }
}
