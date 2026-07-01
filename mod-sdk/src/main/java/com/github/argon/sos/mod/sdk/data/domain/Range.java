package com.github.argon.sos.mod.sdk.data.domain;

import com.github.argon.sos.mod.sdk.game.util.UiMapper;
import com.github.argon.sos.mod.sdk.ui.Slider;
import lombok.*;

/**
 * Data object which defines a range of values with min and max
 */
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

    /**
     * How the range value shall be displayed in the ui
     */
    public enum DisplayMode {
        NONE,
        ABSOLUTE,
        PERCENTAGE;
    }

    /**
     * How the range value shall be applied
     */
    public enum ApplyMode {
        ADD,
        MULTI,
        PERCENT;
    }

    /**
     * Crates a copy of this range
     *
     * @return new copied instance
     */
    @Override
    public Range clone() {
        return Range.builder()
            .displayMode(displayMode)
            .applyMode(applyMode)
            .max(max)
            .min(min)
            .value(value)
            .build();
    }

    /**
     * Reads the values from a {@link Slider} and creates a {@link Range} from it
     *
     * @param slider to extract values from
     * @return range with extracted values
     */
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
