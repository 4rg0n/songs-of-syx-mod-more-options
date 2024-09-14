package com.github.argon.sos.mod.sdk.data;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Will hold a value for a certain amount of time.
 * When the time expired, it will fetch a new value from the supplier.
 *
 * @param <Value> type to hold
 */
@RequiredArgsConstructor
public class CacheValue<Value> {
    private final long lifetimeMillis;
    private final Supplier<Value> valueSupplier;
    private long updateTimeMillis = System.currentTimeMillis();

    @Nullable
    private Value cachedValue;

    /**
     * @return the cached value
     */
    public Value get() {
        if (cachedValue == null || expired()) {
            return readValue();
        } else {
            return cachedValue;
        }
    }

    /**
     * @return whether the cache is expired
     */
    public boolean expired() {
        return System.currentTimeMillis() - updateTimeMillis >= lifetimeMillis;
    }

    /**
     * Clear the cached value and force the cache to fetch a new value
     */
    public void evict() {
        cachedValue = null;
    }

    /**
     * Only clear when cache is expired
     */
    public void evictExpired() {
        if (expired()) {
            evict();
        }
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
