package menu.json.factory;

import com.github.argon.sos.moreoptions.game.action.Resettable;
import com.github.argon.sos.moreoptions.json.element.JsonElement;
import com.github.argon.sos.moreoptions.json.element.JsonObject;
import com.github.argon.sos.mod.sdk.util.Lists;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import snake2d.util.gui.renderable.RENDEROBJ;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JsonUiElementStore implements Resettable {
    /**
     * Contains all {@link JsonUiElementSingle}s created through this factory.
     * {@link LinkedHashMap} keeps order of added json ui elements. So they will be displayed in the order they were added.
     */
    protected final Set<JsonUiElement<? extends JsonElement, ? extends RENDEROBJ>> all = new LinkedHashSet<>();
    @Getter
    protected final JsonObject config;

    public Set<JsonUiElementSingle<? extends JsonElement, ? extends RENDEROBJ>> getAll() {
        return all.stream().flatMap(jsonUiElement -> {
            // todo recursion?!
                if (jsonUiElement instanceof JsonUiElementSingle) {
                    return Lists.of(jsonUiElement).stream();
                } else if (jsonUiElement instanceof JsonUiElementList) {
                    return ((JsonUiElementList<? extends JsonElement, ? extends RENDEROBJ>) jsonUiElement).getElements().stream();
                } else {
                    return Lists.of().stream();
                }
            })
            .filter(JsonUiElementSingle.class::isInstance)
            .map(object -> (JsonUiElementSingle<? extends JsonElement, ? extends RENDEROBJ>) object)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Sets all ui elements to their initial values
     */
    @Override
    public void reset() {
        all.forEach(JsonUiElement::reset);
    }

    /**
     * @return whether any ui element has a changed value
     */
    public boolean isDirty() {
        return all.stream()
            .anyMatch(JsonUiElement::isDirty);
    }

    /**
     * @return elements without json present in the config
     */
    public List<JsonUiElementSingle<? extends JsonElement, ? extends RENDEROBJ>> getOrphans() {
        return getAll().stream()
            .filter(JsonUiElement::isOrphan)
            .collect(Collectors.toList());
    }

    public <Json extends JsonUiElement<Value, Element>, Value extends JsonElement, Element extends RENDEROBJ> Json store(Json jsonUiElement) {
        all.add(jsonUiElement);
        return jsonUiElement;
    }

    public <Json extends JsonUiElement<Value, Element>, Value extends JsonElement, Element extends RENDEROBJ> Storeable<Json, Value, Element> supplier(Supplier<Json> elementSupplier) {
        return new Storeable<>(this, elementSupplier);
    }

    public <Json extends JsonUiElement<Value, Element>, Value extends JsonElement, Element extends RENDEROBJ> Storeable<Json, Value, Element> element(Json element) {
        return new Storeable<>(this, () -> element);
    }

    @RequiredArgsConstructor
    public static class Storeable<Json extends JsonUiElement<Value, Element>, Value extends JsonElement, Element extends RENDEROBJ> {
        private final JsonUiElementStore store;
        private final Supplier<Json> elementSupplier;

        @Getter(lazy = true)
        private final Json element = elementSupplier.get();

        private boolean added = false;

        public Json store() {
            // only add once o.o
            if (!added) {
                added = true;
                return store.store(getElement());
            } else {
                return getElement();
            }
        }
    }
}
