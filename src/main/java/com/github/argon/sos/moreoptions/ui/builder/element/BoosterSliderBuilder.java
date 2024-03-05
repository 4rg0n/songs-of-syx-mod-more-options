package com.github.argon.sos.moreoptions.ui.builder.element;

import com.github.argon.sos.moreoptions.i18n.Dictionary;
import com.github.argon.sos.moreoptions.game.booster.MoreOptionsBoosters;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.game.ui.Tabulator;
import com.github.argon.sos.moreoptions.game.ui.UiInfo;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.Translatable;
import com.github.argon.sos.moreoptions.ui.builder.UiBuilder;
import com.github.argon.sos.moreoptions.util.Lists;
import com.github.argon.sos.moreoptions.util.Maps;
import game.boosting.Boostable;
import init.race.RACES;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GUI_BOX;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;
import util.gui.misc.GText;

import java.util.List;

@RequiredArgsConstructor
public class BoosterSliderBuilder implements UiBuilder<List<GuiSection>, Tabulator<String, Slider, Integer>> {
    private final Definition definition;

    public BuildResult<List<GuiSection>, Tabulator<String, Slider, Integer>> build() {
        if (definition.getLabelWidth() > 0) {
            definition.getLabelDefinition().setMaxWidth(definition.getLabelWidth());
        }
        Boostable boostable = definition.getBoosters().getAdd().getOrigin();
        LabelBuilder.Definition labelDefinition = definition.getLabelDefinition();

        // Label
        GText text = new GText(labelDefinition.getFont(), labelDefinition.getTitle());
        GuiSection label = new GuiSection(){
            @Override
            public void hoverInfoGet(GUI_BOX text) {
                text.title(boostable.name);
                text.text(boostable.desc);
                text.NL(8);
                boostable.hoverDetailed(text, RACES.clP(null, null), null, true);
            }
        };
        label.addRight(0, text);
        if (labelDefinition.getMaxWidth() > 0) {
            text.setMaxWidth(labelDefinition.getMaxWidth());
            label.body().setWidth(labelDefinition.getMaxWidth());
        }
        label.pad(10, 5);

        // Sliders
        Slider additiveSlider = SliderBuilder.builder()
            .definition(definition.getSliderAddDefinition())
            .build().getResult();
        additiveSlider.pad(10, 5);
        Slider multiSlider = SliderBuilder.builder()
            .definition(definition.getSliderMultiDefinition())
            .build().getResult();
        multiSlider.pad(10, 5);

        // Icon
        GuiSection icon = new GuiSection();
        if (labelDefinition.getDescription() != null) {
            icon.hoverInfoSet(labelDefinition.getDescription());
        }
        icon.add(new RENDEROBJ.Sprite(boostable.icon));

        // Booster toggle
        Tabulator<String, Slider, Integer> tabulator = new Tabulator<>(Maps.ofLinked(
            UiInfo.<String>builder()
                .key("add")
                .title("Add")
                .description("Adds to the booster value.")
                .build(), additiveSlider,
            UiInfo.<String>builder()
                .key("multi")
                .title("Perc")
                .description("Regulates the percentage of the booster value. Values under 100% will lower the effect.")
                .build(), multiSlider
        ), DIR.W, 0, 0, false, true);

        tabulator.tab(definition.getActiveKey());
        List<GuiSection> row = Lists.of(
            label,
            icon,
            tabulator
        );

        return BuildResult.<List<GuiSection>, Tabulator<String, Slider, Integer>>builder()
            .result(row)
            .interactable(tabulator)
            .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Setter
    public static class Builder {

        @Accessors(fluent = true)
        private Definition definition;

        public Builder translate(Definition definition) {
            Dictionary dictionary = Dictionary.getInstance();
            dictionary.translate(definition.getLabelDefinition());

            return definition(definition);
        }

        public BuildResult<List<GuiSection>, Tabulator<String, Slider, Integer>> build() {
            assert definition != null : "definition must not be null";

            return new BoosterSliderBuilder(definition).build();
        }
    }

    @Data
    @lombok.Builder
    public static class Definition implements Translatable<String> {

        private LabelBuilder.Definition labelDefinition;
        private SliderBuilder.Definition sliderMultiDefinition;
        private SliderBuilder.Definition sliderAddDefinition;

        @lombok.Builder.Default
        private int labelWidth = 0;

        private String activeKey;

        private MoreOptionsBoosters boosters;

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
