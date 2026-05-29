package com.github.argon.sos.mod.sdk.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import snake2d.util.misc.CLAMP;

@RequiredArgsConstructor
public class IntegerValue {

    @Getter
    public int value;
    @Getter
    public final int min;
    @Getter
    public final int max;

    public IntegerValue() {
        this.min = Integer.MIN_VALUE;
        this.max = Integer.MAX_VALUE;
        this.value = CLAMP.i(this.value, min, max);
    }

    public IntegerValue(int value, int min, int max) {
        this(min, max);
        this.value = value;
    }

    public int setValue(int value) {
        this.value = CLAMP.i(value, min, max);
        return this.value;
    }

    public void clear() {
        value = 0;
    }

    public boolean isMax() {
        return getValue() >= max;
    }

    public boolean isMin() {
        return getValue() <= min;
    }

    public int inc(int value) {
        return setValue(getValue() + value);
    }
}
