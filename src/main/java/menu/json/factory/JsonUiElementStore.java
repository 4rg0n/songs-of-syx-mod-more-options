package menu.json.factory;

import com.github.argon.sos.moreoptions.game.action.Resettable;
import com.github.argon.sos.moreoptions.json.element.JsonElement;
import com.github.argon.sos.moreoptions.json.element.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import snake2d.util.gui.renderable.RENDEROBJ;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public class JsonUiElementStore implements Resettable {
    /**
     * Contains all {@link JsonUiElement}s created through this factory.
     * {@link LinkedHashMap} keeps order of added json ui elements. So they will be displayed in the order they were added.
     */
    protected final Set<JsonUiElement<? extends JsonElement, ? extends RENDEROBJ>> all = new LinkedHashSet<>();
    protected final JsonObject config;

    public JsonObject persist() {
        getAll().stream()
            .filter(element -> element.getJsonPath() != null)
            .forEach(element -> element.writeInto(config));
        return config;
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
    public List<JsonUiElement<?, ?>> getOrphans() {
        return all.stream()
            .filter(JsonUiElement::orphan)
            .collect(Collectors.toList());
    }
    
    public <Value extends JsonElement, Element extends RENDEROBJ> JsonUiElement<Value, Element> store(JsonUiElement<Value, Element> jsonUiElement) {
        all.add(jsonUiElement);
        return jsonUiElement;
    }

    public <Value extends JsonElement, Element extends RENDEROBJ> Products<Value, Element> list(Supplier<JsonUiElement<Value, Element>>... elementSuppliers) {
        return Products.of(this, elementSuppliers);
    }

    public <Value extends JsonElement, Element extends RENDEROBJ> Product<Value, Element> element(Supplier<JsonUiElement<Value, Element>> elementSupplier) {
        return new Product<>(this, elementSupplier);
    }

    public <Value extends JsonElement, Element extends RENDEROBJ> Product<Value, Element> element(JsonUiElement<Value, Element> element) {
        return new Product<>(this, () -> element);
    }

    public <Value extends JsonElement, Element extends RENDEROBJ> ProductArray<Value, Element> elements(Supplier<JsonUiElementArray<Value, Element>> elementsSupplier) {
        return new ProductArray<>(this, elementsSupplier);
    }

    @RequiredArgsConstructor
    public static class Products<Value extends JsonElement, Element extends RENDEROBJ> {
        private final List<Product<Value, Element>> products;

        @Getter(lazy = true)
        private final List<JsonUiElement<Value, Element>> elements = products.stream()
            .map(Product::getElement)
            .collect(Collectors.toList());

        public static <Value extends JsonElement, Element extends RENDEROBJ> Products<Value, Element> of(JsonUiElementStore store, Supplier<JsonUiElement<Value, Element>>... elementSuppliers) {
            List<Product<Value, Element>> products = Stream.of(elementSuppliers)
                .map(suppler -> new Product<>(store, suppler))
                .collect(Collectors.toList());

            return new Products<>(products);
        }

        public List<JsonUiElement<Value, Element>> store() {
            return products.stream()
                .map(Product::store)
                .collect(Collectors.toList());
        }
    }

    @RequiredArgsConstructor
    public static class Product<Value extends JsonElement, Element extends RENDEROBJ> {
        private final JsonUiElementStore store;
        private final Supplier<JsonUiElement<Value, Element>> elementSupplier;

        @Getter(lazy = true)
        private final JsonUiElement<Value, Element> element = elementSupplier.get();

        private boolean added = false;

        public JsonUiElement<Value, Element> store() {
            // only add once o.o
            if (!added) {
                added = true;
                return store.store(getElement());
            } else {
                return getElement();
            }
        }
    }

    @RequiredArgsConstructor
    public static class ProductArray<Value extends JsonElement, Element extends RENDEROBJ> {
        private final JsonUiElementStore store;
        private final Supplier<JsonUiElementArray<Value, Element>> elementSupplier;

        @Getter(lazy = true)
        private final JsonUiElementArray<Value, Element> element = elementSupplier.get();

        private boolean added = false;

        public JsonUiElementArray<Value, Element> store() {
            // only add once o.o
            JsonUiElementArray<Value, Element> element = getElement();
            if (!added) {
                added = true;

                element.getElements().forEach(store::store);
            }

            return element;
        }
    }
}
