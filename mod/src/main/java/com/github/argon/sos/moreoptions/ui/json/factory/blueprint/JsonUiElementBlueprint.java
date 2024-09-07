package com.github.argon.sos.moreoptions.ui.json.factory.blueprint;

import com.github.argon.sos.mod.sdk.json.element.JsonElement;
import com.github.argon.sos.moreoptions.ui.json.factory.JsonUiElementSingle;
import snake2d.util.gui.renderable.RENDEROBJ;

public interface JsonUiElementBlueprint<Key, Limit, Value extends JsonElement, Element extends RENDEROBJ>  {
    JsonUiElementSingle<Value, Element> get(Key key);
    void reset(Limit limit);
    void limit(Limit limit);
}
