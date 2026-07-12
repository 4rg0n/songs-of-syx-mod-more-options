package com.github.argon.sos.mod.sdk.game.action;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * For ui elements where you can get and set values
 *
 * @param <Value> type of set and returned value
 */
public interface Valuable<Value> {
    /**
     * Returns the value stored in a component.
     *
     * @return the stored value
     */
    @Nullable
    Value getValue();

    /**
     * Sets a new value into the component
     *
     * @param value to set
     */
    void setValue(Value value);

    /**
     * Optional action to be executed when a stored value changes.
     *
     * @param valueChangeAction to execute
     */
    default void valueChangeAction(Consumer<Value> valueChangeAction) {
        throw new UnsupportedOperationException("Method is not implemented");
    }

    /**
     * Optional action to read the value from.
     *
     * @param valueSupplier to execute
     */
    default void valueSupplier(Supplier<Value> valueSupplier) {
        throw new UnsupportedOperationException("Method is not implemented");
    }

    /**
     * Optional action to write the value into.
     *
     * @param valueConsumer to execute
     */
    default void valueConsumer(Consumer<Value> valueConsumer) {
        throw new UnsupportedOperationException("Method is not implemented");
    }
}
