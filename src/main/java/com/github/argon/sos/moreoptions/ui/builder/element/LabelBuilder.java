package com.github.argon.sos.moreoptions.ui.builder.element;

import com.github.argon.sos.moreoptions.Dictionary;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.Translatable;
import com.github.argon.sos.moreoptions.ui.builder.UiBuilder;
import init.sprite.UI.UI;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import snake2d.util.gui.GuiSection;
import snake2d.util.sprite.text.Font;
import util.gui.misc.GText;

@RequiredArgsConstructor
public class LabelBuilder implements UiBuilder<GuiSection, GText> {

    private final Definition definition;

    public BuildResult<GuiSection, GText> build() {
        GText text = new GText(definition.getFont(), definition.getTitle());

        GuiSection section = new GuiSection();
        section.addRight(0, text);

        if (definition.getDescription() != null) {
            section.hoverInfoSet(definition.getDescription());
        }

        return BuildResult.<GuiSection, GText>builder()
            .result(section)
            .element(BuildResult.NO_KEY, text)
            .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        @lombok.Setter
        @Accessors(fluent = true)
        private Definition definition;

        public Builder translate(Definition definition) {
            Dictionary dictionary = Dictionary.getInstance();
            Definition translate = dictionary.translate(definition);

            return definition(translate);
        }

        public LabelBuilder build() {
            assert definition != null : "definition must not be null";

            return new LabelBuilder(definition);
        }


    }

    @Data
    @lombok.Builder
    public static class Definition implements Translatable {
        private String key;
        private String title;

        private String description;

        @lombok.Builder.Default
        private boolean translate = true;

        @lombok.Builder.Default
        private Font font = UI.FONT().M;
    }
}
