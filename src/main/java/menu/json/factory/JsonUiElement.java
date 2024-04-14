package menu.json.factory;

import com.github.argon.sos.moreoptions.json.element.JsonElement;
import com.github.argon.sos.moreoptions.json.element.JsonObject;
import snake2d.util.gui.renderable.RENDEROBJ;

public interface JsonUiElement<Value extends JsonElement, Element extends RENDEROBJ> {
    String getJsonPath();
    void setValue(Value value);
    Value getValue();
    void reset();
    void writeInto(JsonObject config);
    boolean isDirty();
    boolean isOrphan();
}
