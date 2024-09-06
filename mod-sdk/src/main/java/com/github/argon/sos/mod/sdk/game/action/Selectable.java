package com.github.argon.sos.mod.sdk.game.action;


import java.util.List;

/**
 * For selecting ui elements in another ui element like rows in a table
 *
 * @param <Key> type of the key used to identify the element to select
 * @param <Element> type of selected element
 */
public interface Selectable<Key, Element> {
    default void select(List<Key> keys) {};

    List<Element> getSelection();

    default void selectAction(BiAction<Key, Element> toggleAction) {
        throw new UnsupportedOperationException("Method is not implemented");
    }
}
