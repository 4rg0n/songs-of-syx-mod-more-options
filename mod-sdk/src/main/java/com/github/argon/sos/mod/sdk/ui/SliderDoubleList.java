package com.github.argon.sos.mod.sdk.ui;

import com.github.argon.sos.mod.sdk.game.action.Action;
import lombok.Builder;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class SliderDoubleList extends AbstractSliderList<Double> {

    @Builder
    public SliderDoubleList(CharSequence label, @Nullable CharSequence description, List<Slider> sliders, int height, Function<@Nullable Double, Slider> elementSupplier, @Nullable Action<AbstractSliderList<Double>> clickAction) {
        super(label, description, sliders, height, elementSupplier, clickAction);
    }

    public List<Double> getValue() {
        return uiList.getElements().stream()
            .map(Slider::getValueD)
            .toList();
    }
}
