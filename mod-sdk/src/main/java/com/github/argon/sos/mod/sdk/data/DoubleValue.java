package com.github.argon.sos.mod.sdk.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import snake2d.util.misc.CLAMP;

@RequiredArgsConstructor
public class DoubleValue {

    @Getter
    public double value;
    @Getter
    public final double min;
    @Getter
    public final double max;

    public DoubleValue() {
        this.min = Double.MIN_VALUE;
        this.max = Double.MAX_VALUE;
        this.value = CLAMP.d(this.value, min, max);
    }

    public DoubleValue(double value, double min, double max) {
        this(min, max);
        this.value = value;
    }

    public double setValue(double value) {
        this.value = CLAMP.d(value, min, max);
        return this.value;
    }

    public void clear() {
        value = 0.0;
    }

    public boolean isMax() {
        return getValue() >= max;
    }

    public boolean isMin() {
        return getValue() <= min;
    }

    public double inc(double value) {
        return setValue(getValue() + value);
    }
}
