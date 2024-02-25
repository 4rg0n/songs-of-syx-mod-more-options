package com.github.argon.sos.moreoptions.ui.builder.element;

import com.github.argon.sos.moreoptions.Dictionary;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.Translatable;
import com.github.argon.sos.moreoptions.ui.builder.UiBuilder;
import com.github.argon.sos.moreoptions.util.Lists;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import snake2d.util.gui.GuiSection;

import java.util.List;

/**
 * @param <E> element next to the label
 */
@RequiredArgsConstructor
public class LabeledBuilder<E extends GuiSection> implements UiBuilder<List<GuiSection>, E> {
    private final Definition<E> definition;

    public BuildResult<List<GuiSection>, E> build() {
        if (definition.getLabelWidth() > 0) {
            definition.getLabelDefinition().setMaxWidth(definition.getLabelWidth());
        }

        GuiSection label = LabelBuilder.builder()
            .translate(definition.getLabelDefinition())
            .build().getResult();
        label.pad(10, 5);

        List<GuiSection> row = Lists.of(label, definition.getElement());

        return BuildResult.<List<GuiSection>, E>builder()
            .result(row)
            .interactable(definition.getElement())
            .build();
    }

    public static <T extends GuiSection> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<E extends GuiSection> {

        @lombok.Setter
        @Accessors(fluent = true)
        private Definition<E> definition;

        public Builder<E> translate(Definition<E> definition) {
            Dictionary dictionary = Dictionary.getInstance();
            dictionary.translate(definition.getLabelDefinition());

            return definition(definition);
        }

        public BuildResult<List<GuiSection>, E> build() {
            assert definition != null : "definition must not be null";

            return new LabeledBuilder<>(definition).build();
        }
    }

    @Data
    @lombok.Builder
    public static class Definition<E extends GuiSection> implements Translatable {

        private LabelBuilder.Definition labelDefinition;

        private E element;

        @lombok.Builder.Default
        private int labelWidth = 0;

        @Override
        public String getKey() {
            return labelDefinition.getKey();
        }

        @Override
        public boolean isTranslate() {
            return labelDefinition.isTranslate();
        }

        @Override
        public void setTitle(String title) {
            labelDefinition.setTitle(title);
        }

        @Override
        public void setDescription(String description) {
            labelDefinition.setDescription(description);
        }
    }
}
