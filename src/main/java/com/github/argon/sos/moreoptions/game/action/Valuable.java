package com.github.argon.sos.moreoptions.game.action;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * For ui elements where you can get and set values
 *
 * @param <Value> type of set and returned value
 */
public interface Valuable<Value> {
    @Nullable
    Value getValue();
    void setValue(Value value);

    default void valueChangeAction(Consumer<Value> valueChangeAction) {
        throw new UnsupportedOperationException("Method is not implemented");
    }

    default void valueSupplier(Supplier<Value> valueSupplier) {
        throw new UnsupportedOperationException("Method is not implemented");
    }

    default void valueConsumer(Consumer<Value> valueConsumer) {
        throw new UnsupportedOperationException("Method is not implemented");
    }
}
