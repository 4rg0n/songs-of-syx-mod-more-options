package com.github.argon.sos.mod.sdk.game.action;

/**
 * Toggles the selected state of an ui element
 *
 * @param <Value> type of the value passed to the toggle action
 */
public interface Toggleable<Value> {
    /**
     * Toggle selected state from selected to unselected or vice versa
     */
    default void toggle() {}

    /**
     * Optional action to be executed when an ui element is toggled.
     *
     * @param toggleAction to be executed when toggling
     */
    default void toggleAction(Action<Value> toggleAction) {
        throw new UnsupportedOperationException("Method is not implemented");
    }
}
