package com.github.argon.sos.moreoptions.ui.builder.element;

import com.github.argon.sos.moreoptions.game.ui.Checkbox;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.UiBuilder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
public class CheckboxBuilder implements UiBuilder<Checkbox, Checkbox> {

    private final Definition definition;

    public BuildResult<Checkbox, Checkbox> build() {
        Checkbox checkbox = new Checkbox(definition.isEnabled());

        return BuildResult.<Checkbox, Checkbox>builder()
            .result(checkbox)
            .element(BuildResult.NO_KEY, checkbox)
            .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        @lombok.Setter
        @Accessors(fluent = true)
        private Definition definition;

        public CheckboxBuilder build() {
            assert definition != null : "definition must not be null";

            return new CheckboxBuilder(definition);
        }
    }

    @Data
    @lombok.Builder
    public static class Definition {
        @lombok.Builder.Default
        private boolean enabled = true;
    }
}
