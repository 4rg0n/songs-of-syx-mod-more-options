package com.github.argon.sos.moreoptions.ui.builder.element;

import com.github.argon.sos.moreoptions.config.MoreOptionsV2Config;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.UiBuilder;
import com.github.argon.sos.moreoptions.util.UiUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Singular;
import lombok.experimental.Accessors;
import snake2d.util.color.COLOR;
import util.data.INT;

import java.util.Map;

@RequiredArgsConstructor
public class SliderBuilder implements UiBuilder<Slider, Slider> {

    private final Definition definition;

    @Override
    public BuildResult<Slider, Slider> build() {
        int min = definition.getMin();
        int max = definition.getMax();
        int sliderWidth = (Math.abs(min) + Math.abs(max)) * 2;

        if (definition.getMaxWidth() > 0) {
            sliderWidth = definition.getMaxWidth();
        }

        INT.IntImp sliderValue = new INT.IntImp(min, max);
        sliderValue.set(definition.getValue());
        Slider slider = new Slider(sliderValue, sliderWidth,
            definition.isInput(),
            definition.isLockScroll(),
            definition.getValueDisplay(),
            definition.getThresholds());

        return BuildResult.<Slider, Slider>builder()
            .result(slider)
            .interactable(slider)
            .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        @lombok.Setter
        @Accessors(fluent = true)
        private Definition definition;

        public BuildResult<Slider, Slider> build() {
            assert definition != null : "definition must not be null";

            return new SliderBuilder(definition).build();
        }
    }

    @Data
    @lombok.Builder
    public static class Definition {
        @lombok.Builder.Default
        private int min = 0;

        @lombok.Builder.Default
        private int max = 100;

        private int value;

        private int maxWidth;

        @lombok.Builder.Default
        private boolean input = true;

        @lombok.Builder.Default
        private boolean lockScroll = true;

        @Singular
        private Map<Integer, COLOR> thresholds;

        @lombok.Builder.Default
        private Slider.ValueDisplay valueDisplay = Slider.ValueDisplay.PERCENTAGE;

        public static DefinitionBuilder buildFrom(MoreOptionsV2Config.Range range) {
            return SliderBuilder.Definition.builder()
                .min(range.getMin())
                .max(range.getMax())
                .value(range.getValue())
                .valueDisplay(UiUtil.fromDisplayMode(range.getDisplayMode()));
        }
    }
}
