package com.github.argon.sos.moreoptions.game.ui;

import com.github.argon.sos.moreoptions.game.BiAction;
import snake2d.util.gui.renderable.RENDEROBJ;

/**
 * For ui elements where you can get and set values
 *
 * @param <Value> type of set and returned value
 * @param <Element> type of source object triggered the setting or getting
 */
public interface Valuable<Value, Element> extends RENDEROBJ {
    Value getValue();
    void setValue(Value value);

    default void onGetValue(BiAction<Value, Element> getValueAction) {
        throw new UnsupportedOperationException("Method is not implemented");
    }

    default void onBeforeSetValue(BiAction<Value, Element> beforeSetValueAction) {
        throw new UnsupportedOperationException("Method is not implemented");
    }

    default void onAfterSetValue(BiAction<Value, Element> afterSetValueAction) {
        throw new UnsupportedOperationException("Method is not implemented");
    }
}
