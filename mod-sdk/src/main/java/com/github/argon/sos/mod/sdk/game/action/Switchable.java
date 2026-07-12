package com.github.argon.sos.mod.sdk.game.action;

/**
 * For switching ui elements by hiding one and displaying another
 *
 * @param <Key> type of the key identifying an ui element to switch to
 */
public interface Switchable<Key> {
    /**
     * Switches an ui element by hiding the current one and displaying the one by given key.
     *
     * @param key of the ui element to switch to
     */
    default void doSwitch(Key key) {}

    /**
     * Optional action to be executed when an ui element is switched.
     *
     * @param switchAction to be executed when switching
     */
    default void switchAction(Action<Key> switchAction) {
        throw new UnsupportedOperationException("Method is not implemented");
    }
}
