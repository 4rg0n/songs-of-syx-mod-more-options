package com.github.argon.sos.mod.sdk.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import snake2d.util.misc.CLAMP;

/**
 * A container for an {@link Integer} value with min and max
 */
@RequiredArgsConstructor
public class IntegerValue {

    @Getter
    private int value;
    @Getter
    private final int min;
    @Getter
    private final int max;

    /**
     * Creates a new {@link IntegerValue} with {@link Integer#MIN_VALUE} as min,
     * {@link Integer#MAX_VALUE} as max and 0.0 as value.
     */
    public IntegerValue() {
        this.min = Integer.MIN_VALUE;
        this.max = Integer.MAX_VALUE;
        this.value = CLAMP.i(this.value, min, max);
    }

    /**
     * Creates a new {@link IntegerValue}
     *
     * @param value the actual integer value
     * @param min allowed min
     * @param max allowed max
     */
    public IntegerValue(int value, int min, int max) {
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
    public int setValue(int value) {
        this.value = CLAMP.i(value, min, max);
        return this.value;
    }

    /**
     * Will set the value to 0
     */
    public void clear() {
        value = 0;
    }

    /**
     * Will calculate a distance
     * on how far the integer is from {@code getMin()},
     * if the value is negative and how far it is from {@code getMax()}, if the value is positive.
     *
     * @return the distance to max / min, where 1.0 means it is at max / min.
     */
    public double getValueDistance() {
        if (getValue() < 0) {
            if (getMin() == 0) {
                return 0;
            }

            return getValue() / (double) getMin();
        } else if (getValue() > 0) {
            if (getMax() == 0) {
                return 0;
            }

            return getValue() / (double) getMax();
        } else {
            return 0;
        }
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
    public int inc(int value) {
        return setValue(getValue() + value);
    }
}
