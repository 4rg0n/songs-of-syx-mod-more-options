package com.github.argon.sos.moreoptions.ui.json.factory.builder;

import com.github.argon.sos.mod.sdk.game.asset.GameWildcard;
import com.github.argon.sos.mod.sdk.json.element.JsonElement;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import com.github.argon.sos.moreoptions.ui.json.factory.JsonUiElementList;
import com.github.argon.sos.moreoptions.ui.json.factory.JsonUiElementSingle;
import snake2d.util.gui.renderable.RENDEROBJ;

import java.util.List;
import java.util.function.Function;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUiElementListBuilder<Value extends JsonElement, Element extends RENDEROBJ> {

    private final String jsonPath;
    private final Function<String, JsonUiElementSingle<Value, Element>> elementProvider;

    public JsonUiElementList<Value, Element> asTuples(String name, int amount) {
        JsonUiElementList.JsonUiElementListBuilder<Value, Element> builder = JsonUiElementList.builder();
        builder
            .jsonPath(jsonPath);

        for (int i = 0; i < amount; i++) {
            builder.element(asTupleElement(i, name));
        }

        return builder.build();
    }

    /**
     * Creates a list of {@link JsonUiElementSingle}s and wraps it into {@link JsonUiElementList}.
     * It will therefor extend the json path by the names of the given list.
     */
    public JsonUiElementList<Value, Element> asObject(List<String> names, boolean asWildcards) {
        JsonUiElementList.JsonUiElementListBuilder<Value, Element> builder = JsonUiElementList.builder();
        builder
            .jsonPath(jsonPath);
        
        if (asWildcards) {
            GameWildcard.from(names).forEach(name -> {
                builder.element(asObjectElement(name));
            });
        } else {
            names.forEach(name -> {
                builder.element(asObjectElement(name));
            });
        }

        return builder.build();
    }

    public JsonUiElementList<Value, Element> asArray(int amount) {
        JsonUiElementList.JsonUiElementListBuilder<Value, Element> builder = JsonUiElementList.builder();
        builder
            .jsonPath(jsonPath);

        for (int i = 0; i < amount; i++) {
            builder.element(asArrayElement(i));
        }

        return builder.build();
    }

    public JsonUiElementSingle<Value, Element> asTupleElement(int index, String name) {
        String jsonTuplePath = jsonTuplePath(jsonPath, index, name);
        return elementProvider.apply(jsonTuplePath);
    }

    public JsonUiElementSingle<Value, Element> asObjectElement(String name) {
        String jsonObjectPath = jsonObjectPath(jsonPath, name);
        return elementProvider.apply(jsonObjectPath);
    }

    public JsonUiElementSingle<Value, Element> asArrayElement(int index) {
        String jsonArrayPath = jsonArrayPath(jsonPath, index);
        return elementProvider.apply(jsonArrayPath);
    }
    
    public JsonUiElementSingle<Value, Element> asElement() {
        return elementProvider.apply(jsonPath);
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
