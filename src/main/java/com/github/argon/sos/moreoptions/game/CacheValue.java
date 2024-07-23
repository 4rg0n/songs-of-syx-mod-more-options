package com.github.argon.sos.moreoptions.game;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class CacheValue<Value> {
    private final long lifetimeMillis;
    private final Supplier<Value> valueSupplier;
    private long updateTimeMillis = System.currentTimeMillis();

    @Nullable
    private Value cachedValue;

    public Value get() {
        if (cachedValue == null || expired()) {
            return readValue();
        } else {
            return cachedValue;
        }
    }

    public boolean expired() {
        return System.currentTimeMillis() - updateTimeMillis >= lifetimeMillis;
    }

    public void evict() {
        cachedValue = null;
    }

    private Value readValue() {
        Value value = valueSupplier.get();
        cachedValue = value;
        updateTimeMillis = System.currentTimeMillis();

        return value;
    }

    public static <Value> CacheValue<Value> of(long lifetimeMillis, Supplier<Value> valueSupplier) {
        return new CacheValue<>(lifetimeMillis, valueSupplier);
    }
}
