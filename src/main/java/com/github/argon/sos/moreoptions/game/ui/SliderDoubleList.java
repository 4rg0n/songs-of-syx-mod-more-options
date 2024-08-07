package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.action.Action;
import lombok.Builder;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SliderDoubleList extends AbstractSliderList<Double> {

    @Builder
    public SliderDoubleList(CharSequence label, @Nullable CharSequence description, List<Slider> sliders, int height, Function<@Nullable Double, Slider> elementSupplier, @Nullable Action<AbstractSliderList<Double>> clickAction) {
        super(label, description, sliders, height, elementSupplier, clickAction);
    }

    @Override
    protected SliderDoubleList element() {
        return this;
    }


    public List<Double> getValue() {
        return uiList.getElements().stream()
            .map(Slider::getValueD)
            .collect(Collectors.toList());
    }
}
