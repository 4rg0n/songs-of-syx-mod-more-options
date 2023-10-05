package com.github.argon.sos.moreoptions.ui.builder.element;

import com.github.argon.sos.moreoptions.game.ui.Checkbox;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.UiBuilder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import snake2d.util.gui.GuiSection;

@RequiredArgsConstructor
public class CheckboxBuilder implements UiBuilder<GuiSection, Checkbox> {

    private final Definition definition;

    public BuildResult<GuiSection, Checkbox> build() {
        Checkbox checkbox = new Checkbox(definition.isEnabled());
        GuiSection section = new GuiSection(checkbox);

        return BuildResult.<GuiSection, Checkbox>builder()
            .result(section)
            .interactable(checkbox)
            .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        @lombok.Setter
        @Accessors(fluent = true)
        private Definition definition;

        public BuildResult<GuiSection, Checkbox> build() {
            assert definition != null : "definition must not be null";

            return new CheckboxBuilder(definition).build();
        }
    }

    @Data
    @lombok.Builder
    public static class Definition {
        @lombok.Builder.Default
        private boolean enabled = true;
    }
}
