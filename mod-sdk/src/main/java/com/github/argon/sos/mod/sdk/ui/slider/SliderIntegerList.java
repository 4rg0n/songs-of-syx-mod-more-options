package com.github.argon.sos.mod.sdk.ui.slider;

import com.github.argon.sos.mod.sdk.game.action.Action;
import com.github.argon.sos.mod.sdk.game.action.Valuable;
import lombok.Builder;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

/**
 * A button, which opens a list with {@link Slider}s containing {@link Integer} values.
 */
public class SliderIntegerList extends AbstractSliderList<Integer> implements Valuable<List<Integer>> {

    /**
     * Creates a new {@link SliderIntegerList}.
     *
     * @param label of the button
     * @param description of the button when hovered
     * @param sliders of sliders to display
     * @param height available for the sliders
     * @param elementSupplier used for creating the sliders with given values
     * @param clickAction for the button
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getValue() {
        return uiList.getElements().stream()
            .map(Slider::getValue)
            .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(List<Integer> integers) {
        for (int i = 0; i < integers.size(); i++) {
            Integer integerValue = integers.get(i);
            Slider slider = uiList.getElements().get(i);
            slider.setValue(integerValue);
        }
    }
}
