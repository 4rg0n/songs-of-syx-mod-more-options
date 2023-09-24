package com.github.argon.sos.moreoptions.ui;

import com.github.argon.sos.moreoptions.game.ui.Slider;
import init.sprite.UI.UI;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import snake2d.util.gui.GuiSection;
import util.data.INT;
import util.gui.misc.GText;
import util.gui.table.GScrollRows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SliderBuilder {
    @Getter
    private final Map<String, Slider> sliders = new HashMap<>();

    public GuiSection build(Map<String, Definition> sliderDescriptions, int displayHeight) {
        sliders.clear();

        snake2d.util.sets.LinkedList<ScrollableBuilder.ColumnRow> builds = new snake2d.util.sets.LinkedList<>();

        sliderDescriptions.forEach((key, description) -> {
            int min = description.getMin();
            int max = description.getMax();
            int width = (Math.abs(min) + Math.abs(max)) * 2;

            INT.IntImp sliderValue = new INT.IntImp(min, max);
            sliderValue.set(description.getValue());
            Slider slider = new Slider(sliderValue, width, description.isInput(), description.isLockScroll(), description.getValueDisplay());

            if (description.getDescription() != null) {
                slider.hoverInfoSet(description.getDescription());
            }

            GuiSection text = new GuiSection();
            text.addRight(0, new GText(UI.FONT().M, description.getTitle()));

            List<GuiSection> container = new ArrayList<>();
            container.add(text);
            container.add(slider);

            int height = slider.body().height();
            builds.add(new ScrollableBuilder.ColumnRow(container, width, height));

            sliders.put(key, slider);
        });

        ScrollableBuilder scrollableBuilder = new ScrollableBuilder();
        GScrollRows gScrollRows = scrollableBuilder.build(builds, displayHeight);

        GuiSection section = new GuiSection();
        section.add(gScrollRows.view());

        return section;
    }

    @Data
    @Builder
    public static class Definition {
        private String title;
        private String description;

        @Builder.Default
        private int min = 0;

        @Builder.Default
        private int max = 100;

        private int value;

        @Builder.Default
        private boolean input = true;

        @Builder.Default
        private boolean lockScroll = true;

        @Builder.Default
        private Slider.ValueDisplay valueDisplay = Slider.ValueDisplay.PERCENTAGE;
    }
}
