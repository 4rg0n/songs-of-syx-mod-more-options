package com.github.argon.sos.moreoptions.ui.builder.element;

import com.github.argon.sos.moreoptions.i18n.Dictionary;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.Translatable;
import com.github.argon.sos.moreoptions.ui.builder.UiBuilder;
import com.github.argon.sos.moreoptions.util.UiUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.experimental.Accessors;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @param <E> element next to the label
 */
@RequiredArgsConstructor
public class LabeledBuilder<E extends RENDEROBJ> implements UiBuilder<List<GuiSection>, List<E>> {
    private final Definition<E> definition;

    public BuildResult<List<GuiSection>, List<E>> build() {
        if (definition.getLabelWidth() > 0) {
            definition.getLabelDefinition().setMaxWidth(definition.getLabelWidth());
        }

        GuiSection label = LabelBuilder.builder()
            .translate(definition.getLabelDefinition())
            .build().getResult();
        label.pad(10, 5);

        List<GuiSection> row = new ArrayList<>();
        row.add(label);
        row.addAll(definition.getElements().stream()
                .map(UiUtil::toGuiSection)
                .collect(Collectors.toList()));

        return BuildResult.<List<GuiSection>, List<E>>builder()
            .result(row)
            .interactable(definition.getElements())
            .build();
    }

    public static <T extends RENDEROBJ> Builder<T> builder() {
        return new Builder<>();
    }

    @Setter
    public static class Builder<E extends RENDEROBJ> {

        @Accessors(fluent = true)
        private Definition<E> definition;

        public Builder<E> translate(Definition<E> definition) {
            Dictionary dictionary = Dictionary.getInstance();
            dictionary.translate(definition.getLabelDefinition());

            return definition(definition);
        }

        public BuildResult<List<GuiSection>, List<E>> build() {
            assert definition != null : "definition must not be null";

            return new LabeledBuilder<>(definition).build();
        }
    }

    @Data
    @lombok.Builder
    public static class Definition<E extends RENDEROBJ> implements Translatable<String> {

        private LabelBuilder.Definition labelDefinition;

        @Singular
        private List<E> elements;

        @lombok.Builder.Default
        private int labelWidth = 0;

        @Override
        public String getKey() {
            return labelDefinition.getKey();
        }

        @Override
        public boolean isTranslatable() {
            return labelDefinition.isTranslatable();
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
