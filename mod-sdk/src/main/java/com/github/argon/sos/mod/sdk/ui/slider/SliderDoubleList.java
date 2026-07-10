package com.github.argon.sos.mod.sdk.ui.slider;

import com.github.argon.sos.mod.sdk.game.action.Action;
import com.github.argon.sos.mod.sdk.game.action.Valuable;
import lombok.Builder;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

/**
 * A button, which opens a list with {@link Slider}s containing {@link Double} values.
 */
public class SliderDoubleList extends AbstractSliderList<Double> implements Valuable<List<Double>> {

    /**
     * Creates a new {@link SliderDoubleList}.
     *
     * @param label of the button
     * @param description of the button when hovered
     * @param sliders of sliders to display
     * @param height available for the sliders
     * @param elementSupplier used for creating the sliders with given values
     * @param clickAction for the button
     */
    @Builder
    public SliderDoubleList(CharSequence label, @Nullable CharSequence description, List<Slider> sliders, int height, Function<@Nullable Double, Slider> elementSupplier, @Nullable Action<AbstractSliderList<Double>> clickAction) {
        super(label, description, sliders, height, elementSupplier, clickAction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Double> getValue() {
        return uiList.getElements().stream()
            .map(Slider::getValueD)
            .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(List<Double> doubles) {
        for (int i = 0; i < doubles.size(); i++) {
            Double doubleValue = doubles.get(i);
            Slider slider = uiList.getElements().get(i);
            slider.setValueD(doubleValue);
        }
    }
}
