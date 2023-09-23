package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.moreoptions.game.ui.Slider;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import snake2d.util.gui.GuiSection;
import util.data.INT;
import util.gui.misc.GText;

import java.util.HashMap;
import java.util.Map;

public class SliderBuilder {
    @Getter
    private final Map<String, Slider> sliders = new HashMap<>();

    public GuiSection build(Map<String, SliderDescription> sliderDescriptions) {
        sliders.clear();

        GuiSection nameSection = new GuiSection();
        GuiSection sliderSection = new GuiSection();

        sliderDescriptions.forEach((key, description) -> {
            int min = description.getMin();
            int max = description.getMax();
            int width = (Math.abs(min) + Math.abs(max)) * 2;

            INT.IntImp sliderValue = new INT.IntImp(min, max);
            sliderValue.set(description.getValue());
            Slider slider = new Slider(sliderValue, width, description.isInput(), description.getValueDisplay());

            if (description.getDescription() != null) {
                slider.hoverInfoSet(description.getDescription());
            }

            nameSection.addDown(5, new GText(UI.FONT().M, description.getTitle()));
            sliderSection.addDown(5, slider);

            sliders.put(key, slider);
        });

        GuiSection section = new GuiSection();

        section.addRight(0, nameSection);
        section.addRight(10, sliderSection);

        return section;
    }

    @Data
    @Builder
    public static class SliderDescription {
        private String title;
        private String description;
        private int min;

        private int max;

        private int value;

        @Builder.Default
        private boolean input = true;

        @Builder.Default
        private Slider.ValueDisplay valueDisplay = Slider.ValueDisplay.NONE;
    }
}
