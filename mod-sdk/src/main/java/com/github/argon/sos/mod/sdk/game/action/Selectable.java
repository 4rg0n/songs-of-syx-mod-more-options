package com.github.argon.sos.mod.sdk.game.action;


import java.util.List;

/**
 * For selecting ui elements in another ui element like rows in a table
 *
 * @param <Key> type of the key used to identify the element to select
 * @param <Element> type of selected element
 */
public interface Selectable<Key, Element> {
    /**
     * Selects multiple ui elements by the given keys
     *
     * @param keys to select
     */
    default void select(List<Key> keys) {};

    /**
     * Returns all selected elements.
     *
     * @return the selected elements
     */
    List<Element> getSelection();

    /**
     * Optional action to be executed when an element is selected.
     *
     * @param selectAction to be executed when selecting
     */
    default void selectAction(BiAction<Key, Element> selectAction) {
        throw new UnsupportedOperationException("Method is not implemented");
    }
}
