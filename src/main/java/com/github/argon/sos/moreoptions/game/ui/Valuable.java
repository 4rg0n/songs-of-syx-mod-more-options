package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.BiAction;

import java.util.function.Supplier;

/**
 * For ui elements where you can get and set values
 *
 * @param <Value> type of set and returned value
 * @param <Element> type of source object triggered the setting or getting
 */
public interface Valuable<Value, Element> {
    Value getValue();
    void setValue(Value value);

    default void valueGetAction(BiAction<Value, Element> valueGetAction) {
        throw new UnsupportedOperationException("Method is not implemented");
    }

    default void beforeValueSetAction(BiAction<Value, Element> beforeValueSetAction) {
        throw new UnsupportedOperationException("Method is not implemented");
    }

    default void afterValueSetAction(BiAction<Value, Element> afterValueSetAction) {
        throw new UnsupportedOperationException("Method is not implemented");
    }

    default void valueSupplier(Supplier<Value> valueSupplier) {
        throw new UnsupportedOperationException("Method is not implemented");
    }
}
