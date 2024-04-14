package menu.json.factory;

import com.github.argon.sos.moreoptions.game.Wildcard;
import com.github.argon.sos.moreoptions.json.element.JsonElement;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import snake2d.util.gui.renderable.RENDEROBJ;

import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUiElementListBuilder<Value extends JsonElement, Element extends RENDEROBJ> {

    private final String jsonPath;
    private final Function<String, JsonUiElementSingle<Value, Element>> elementProvider;

    public JsonUiElementList<Value, Element> asTuples(String name, int amount) {
        JsonUiElementList.JsonUiElementListBuilder<Value, Element> builder = JsonUiElementList.builder();
        builder.jsonPath(jsonPath);

        for (int i = 0; i < amount; i++) {
            String jsonTuplePath = jsonTuplePath(jsonPath, i, name);
            builder.element(elementProvider.apply(jsonTuplePath));
        }

        return builder.build();
    }

    /**
     * Creates a list of {@link JsonUiElementSingle}s and wraps it into {@link JsonUiElementList}.
     * It will therefor extend the json path by the names of the given list.
     */
    public JsonUiElementList<Value, Element> asObject(List<String> names, boolean asWildcards) {
        JsonUiElementList.JsonUiElementListBuilder<Value, Element> builder = JsonUiElementList.builder();
        builder.jsonPath(jsonPath);
        
        if (asWildcards) {
            Wildcard.from(names).forEach(name -> {
                String jsonObjectPath = jsonObjectPath(jsonPath, name);
                builder.element(elementProvider.apply(jsonObjectPath));
            });
        } else {
            names.forEach(name -> {
                String jsonObjectPath = jsonObjectPath(jsonPath, name);
                builder.element(elementProvider.apply(jsonObjectPath));
            });
        }

        return builder.build();
    }

    public JsonUiElementList<Value, Element> asArray(int amount) {
        JsonUiElementList.JsonUiElementListBuilder<Value, Element> builder = JsonUiElementList.builder();
        builder.jsonPath(jsonPath);

        for (int i = 0; i < amount; i++) {
            String jsonArrayPath = jsonArrayPath(jsonPath, i);
            builder.element(elementProvider.apply(jsonArrayPath));
        }

        return builder.build();
    }
    
    public JsonUiElementSingle<Value, Element> asElement() {
        return elementProvider.apply(jsonPath);
    }
    
    public static <Value extends JsonElement, Element extends RENDEROBJ> JsonUiElementListBuilder<Value, Element> of(String jsonPath, Function<String, JsonUiElementSingle<Value, Element>> elementProvider) {
        return new JsonUiElementListBuilder<>(jsonPath, elementProvider);
    }

    private String jsonTuplePath(String jsonPath, int index, String name) {
        return jsonObjectPath(jsonArrayPath(jsonPath, index), name);
    }

    private String jsonArrayPath(String jsonPath, int index) {
        return jsonPath + "[" + index + "]";
    }

    private String jsonObjectPath(String jsonPath, String name) {
        return jsonPath + "." + name;
    }
}
