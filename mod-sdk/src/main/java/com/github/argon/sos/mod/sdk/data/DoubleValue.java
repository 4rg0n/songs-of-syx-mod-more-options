package com.github.argon.sos.mod.sdk.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import snake2d.util.misc.CLAMP;

/**
 * A container for a {@link Double} value with min and max
 */
@RequiredArgsConstructor
public class DoubleValue {

    @Getter
    private double value;
    @Getter
    private final double min;
    @Getter
    private final double max;

    /**
     * Creates a new {@link DoubleValue} with {@link Double#MIN_VALUE} as min,
     * {@link Double#MAX_VALUE} as max and 0.0 as value.
     */
    public DoubleValue() {
        this.min = Double.MIN_VALUE;
        this.max = Double.MAX_VALUE;
        this.value = CLAMP.d(this.value, min, max);
    }

    /**
     * Creates a new {@link DoubleValue}
     *
     * @param value the actual double value
     * @param min allowed min
     * @param max allowed max
     */
    public DoubleValue(double value, double min, double max) {
        this(min, max);
        this.value = value;
    }

    /**
     * Sets a new value.
     * Will clamp the value according to {@code getMin()} and {@code getMax()}.
     *
     * @param value to set
     * @return set and clamped value
     */
    public double setValue(double value) {
        this.value = CLAMP.d(value, min, max);
        return this.value;
    }

    /**
     * Will set the value to 0.0
     */
    public void clear() {
        value = 0.0;
    }

    /**
     * Will check whether the current value is at max
     *
     * @return whether value is at max
     */
    public boolean isMax() {
        return getValue() >= max;
    }

    /**
     * Will check whether the current value is at min
     *
     * @return whether value is at min
     */
    public boolean isMin() {
        return getValue() <= min;
    }

    /**
     * Increases or decreases (if negative) the current value
     *
     * @param value to increase
     * @return increased or decreased value
     */
    public double inc(double value) {
        return setValue(getValue() + value);
    }
}
