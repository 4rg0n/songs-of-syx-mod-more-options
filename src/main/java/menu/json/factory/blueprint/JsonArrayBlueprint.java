package menu.json.factory.blueprint;

import com.github.argon.sos.moreoptions.json.element.JsonElement;
import lombok.Builder;
import menu.json.factory.JsonUiElementSingle;
import menu.json.factory.builder.JsonUiElementListBuilder;
import org.jetbrains.annotations.Nullable;
import snake2d.util.gui.renderable.RENDEROBJ;

import java.util.function.Function;


public class JsonArrayBlueprint<Value extends JsonElement, Element extends RENDEROBJ> implements JsonUiElementBlueprint<Void, Integer, Value, Element> {
    @Nullable
    private Integer limit;
    private final JsonUiElementListBuilder<Value, Element> listBuilder;

    private int amount;

    @Builder
    private JsonArrayBlueprint(
        @Nullable Integer limit,
        String jsonPath,
        Function<String, JsonUiElementSingle<Value, Element>> elementProvider
    ) {
        this.limit = limit;
        this.listBuilder = JsonUiElementListBuilder.<Value, Element>builder()
            .jsonPath(jsonPath)
            .elementProvider(elementProvider)
            .build();
    }

    @Nullable
    @Override
    public JsonUiElementSingle<Value, Element> get(Void key) {
        if (limit != null && amount >= limit) {
            return null;
        }
        JsonUiElementSingle<Value, Element> element = listBuilder.asArrayElement(amount);
        amount++;

        return element;
    }

    public void reset(Integer amount) {
        this.amount = this.amount - amount;

        if (this.amount < 0) {
            this.amount = 0;
        }
    }

    @Override
    public void limit(Integer maxAmount) {
        this.limit = maxAmount;
    }
}
