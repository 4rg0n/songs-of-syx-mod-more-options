package com.github.argon.sos.mod.sdk.game.action;

/**
 * Used to initialize an element and its content.
 *
 * @param <Value> optional value used for the initialization
 */
public interface Initializable<Value> {

    /**
     * Initializes the element
     */
    default void init() {};

    /**
     * Initializes the element
     *
     * @param value to initialize the element with
     */
    default void init(Value value) {};

    /**
     * Optional action to be executed when initializing.
     *
     * @param initAction to be executed when initializing
     */
    default void initAction(VoidAction initAction) {
        throw new UnsupportedOperationException("Method is not implemented");
    }
}
