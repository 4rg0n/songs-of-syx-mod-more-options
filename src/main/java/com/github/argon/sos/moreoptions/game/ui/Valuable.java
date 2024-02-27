package com.github.argon.sos.moreoptions.game.ui;

import snake2d.util.gui.renderable.RENDEROBJ;

/**
 * For ui elements where you can get and set values
 *
 * @param <TValue> type of set and returned value
 * @param <TSource> type of source object triggered the setting or getting
 */
public interface Valuable<TValue, TSource> extends RENDEROBJ {
    TValue getValue();
    void setValue(TValue value);

    default void onGetValue(UIBiAction<TValue, TSource> getValueAction) {
        throw new UnsupportedOperationException("Method is not implemented");
    }

    default void onBeforeSetValue(UIBiAction<TValue, TSource> beforeSetValueAction) {
        throw new UnsupportedOperationException("Method is not implemented");
    }

    default void onAfterSetValue(UIBiAction<TValue, TSource> afterSetValueAction) {
        throw new UnsupportedOperationException("Method is not implemented");
    }
}
