package menu.json.factory;

import com.github.argon.sos.moreoptions.json.element.JsonArray;
import com.github.argon.sos.moreoptions.json.element.JsonElement;
import com.github.argon.sos.moreoptions.json.element.JsonObject;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import menu.ui.UiFactory;
import snake2d.util.gui.renderable.RENDEROBJ;

import java.util.List;

@Getter
@Builder
public class JsonUiElementList<Value extends JsonElement, Element extends RENDEROBJ> implements JsonUiElement<JsonArray, Element> {
    private final static Logger log = Loggers.getLogger(UiFactory.class);

    @Singular
    private List<JsonUiElement<Value, Element>> elements;

    @Getter
    private final String jsonPath;

    public boolean isDirty() {
        return elements.stream().anyMatch(JsonUiElement::isDirty);
    }

    public JsonArray getValue() {
        JsonArray jsonArray = new JsonArray();

        for (JsonUiElement<Value, Element> jsonUiElement : elements) {
            jsonArray.add(jsonUiElement.getValue());
        }

        return jsonArray;
    }

    public void setValue(JsonArray jsonArray) {
        List<JsonElement> values = jsonArray.getElements();

        int size = values.size();
        if (size > elements.size()) {
            size = elements.size();
        }

        for (int i = 0, elementsSize = size; i < elementsSize; i++) {
            JsonElement value = values.get(i);
            JsonUiElement<Value, Element> jsonUiElement = elements.get(i);

            try {
                //noinspection unchecked
                jsonUiElement.setValue(((Value) value));
            } catch (Exception e) {
                log.warn("Could not set value into json ui element %s",
                    jsonUiElement.getJsonPath(), e);
            }
        }
    }

//    public List<ColumnRow<JsonUiElement<? extends JsonElement, ? extends RENDEROBJ>>> toColumnRows() {
//        return elements.stream()
//            .map(JsonUiElement::toColumnRow)
//            .collect(Collectors.toList());
//    }

    public void writeInto(JsonObject config) {
        elements.forEach(element -> element.writeInto(config));
    }

    public void reset() {
        elements.forEach(JsonUiElement::reset);
    }

    @Override
    public boolean isOrphan() {
        return elements.stream().anyMatch(JsonUiElement::isOrphan);
    }
}
