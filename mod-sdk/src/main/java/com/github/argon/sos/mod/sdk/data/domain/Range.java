package com.github.argon.sos.mod.sdk.data.domain;

import com.github.argon.sos.mod.sdk.game.util.UiMapper;
import com.github.argon.sos.mod.sdk.game.ui.Slider;
import lombok.*;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Range {
    @Builder.Default
    private int value = 0;
    @Builder.Default
    private int min = 0;
    @Builder.Default
    private int max = 10000;
    @Builder.Default
    private ApplyMode applyMode = ApplyMode.MULTI;
    @Builder.Default
    private DisplayMode displayMode = DisplayMode.PERCENTAGE;

    public enum DisplayMode {
        NONE,
        ABSOLUTE,
        PERCENTAGE;
    }

    public enum ApplyMode {
        ADD,
        MULTI,
        PERCENT;
    }

    public Range clone() {
        return Range.builder()
            .displayMode(displayMode)
            .applyMode(applyMode)
            .max(max)
            .min(min)
            .value(value)
            .build();
    }

    public static Range fromSlider(Slider slider) {
        return Range.builder()
            .value(slider.getValue())
            .max(slider.getMax())
            .min(slider.getMin())
            .displayMode(UiMapper.toDisplayMode(slider.getValueDisplay()))
            .applyMode(UiMapper.toApplyMode(slider.getValueDisplay()))
            .build();
    }
}
