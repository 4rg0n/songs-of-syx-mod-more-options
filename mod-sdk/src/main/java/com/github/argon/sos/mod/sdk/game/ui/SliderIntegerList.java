package com.github.argon.sos.mod.sdk.game.ui;

import com.github.argon.sos.mod.sdk.game.action.Action;
import lombok.Builder;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class SliderIntegerList extends AbstractSliderList<Integer> {

    @Builder
    public SliderIntegerList(
        CharSequence label,
        @Nullable CharSequence description,
        List<Slider> sliders,
        int height,
        Function<@Nullable Integer, Slider> elementSupplier,
        @Nullable Action<AbstractSliderList<Integer>> clickAction
    ) {
        super(label, description, sliders, height, elementSupplier, clickAction);
    }

    @Override
    protected SliderIntegerList element() {
        return this;
    }
}
