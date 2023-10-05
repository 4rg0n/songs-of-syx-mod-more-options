package com.github.argon.sos.moreoptions.ui.builder.element;

import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.UiBuilder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import util.data.INT;

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
        Slider slider = new Slider(sliderValue, sliderWidth, definition.isInput(), definition.isLockScroll(), definition.getValueDisplay());

        return BuildResult.<Slider, Slider>builder()
            .result(slider)
            .element(BuildResult.NO_KEY, slider)
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

        @lombok.Builder.Default
        private Slider.ValueDisplay valueDisplay = Slider.ValueDisplay.PERCENTAGE;
    }
}
