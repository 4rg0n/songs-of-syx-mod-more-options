package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.moreoptions.game.ui.Checkbox;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GText;

import java.util.Map;
import java.util.TreeMap;

public class CheckboxBuilder {

    @Getter
    private Map<String, Checkbox> checkboxes = new TreeMap<>();

    public GuiSection build(Map<String, CheckboxDescription> checkboxDescriptions) {
        checkboxes.clear();
        GuiSection nameSection = new GuiSection();
        GuiSection checkSection = new GuiSection();

        checkboxDescriptions.forEach((key, description) -> {
            Checkbox checkbox = new Checkbox(description.isEnabled());
            if (description.getDescription() != null) {
                checkbox.hoverInfoSet(description.getDescription());
            }

            nameSection.addDown(5, new GText(UI.FONT().S, description.getTitle()));
            checkSection.addDown(5, checkbox);

            checkboxes.put(key, checkbox);
        });

        GuiSection section = new GuiSection();

        section.addRight(0, nameSection);
        section.addRight(10, checkSection);

        return section;
    }

    @Data
    @Builder
    public static class CheckboxDescription {
        private String title;
        private String description;
        private boolean enabled;
    }
}
