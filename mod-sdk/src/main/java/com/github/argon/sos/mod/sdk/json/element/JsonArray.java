package com.github.argon.sos.mod.sdk.json.element;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Representation of an array in json.
 * It can hold multiple other {@link JsonElement}s.
 */
@Getter
@EqualsAndHashCode
public class JsonArray implements JsonElement {
    private final List<JsonElement> elements;

    /**
     * Creates a new {@link JsonArray} with an empty elements list.
     */
    public JsonArray() {
        this.elements = new ArrayList<>();
    }

    /**
     * Creates a new {@link JsonArray} with given elements.
     *
     * @param elements to add
     */
    public JsonArray(JsonElement[] elements) {
        this.elements = List.of(elements);
    }

    /**
     * Creates a new {@link JsonArray} with given elements.
     *
     * @param elements to add
     */
    public JsonArray(Collection<? extends JsonElement> elements) {
        this.elements = new ArrayList<>(elements);
    }

    /**
     * Returns the json element with the given index
     *
     * @param index of the element
     * @return json element
     */
    public JsonElement get(int index) {
        return elements.get(index);
    }

    /**
     * Adds an element at the end of the array.
     *
     * @param jsonElement to add
     * @return whether the array changed after adding
     */
    public boolean add(JsonElement jsonElement) {
        return elements.add(jsonElement);
    }

    /**
     * Adds an element at the given index.
     *
     * @param index to add the element into
     * @param element to add
     */
    public void add(int index, JsonElement element) {
        elements.add(index, element);
    }

    /**
     * Transforms the elements in the list to a {@link String}.
     *
     * @return elements as string
     */
    @Override
    public String toString() {
        return elements.toString();
    }

    /**
     * Returns the size of the elements in the array.
     *
     * @return size of elements
     */
    public int size() {
        return elements.size();
    }

    /**
     * Casts the elements to a list with the given class type.
     *
     * @param clazz to cast to
     * @return list with cast elements
     * @param <T> type to cast to
     */
    public <T extends JsonElement> List<T> as(Class<T> clazz) {
        return elements.stream()
            .filter(clazz::isInstance)
            .map(clazz::cast)
            .toList();
    }

    /**
     * Creates a new {@link JsonArray} from the given collection of elements.
     *
     * @param elements to create the json array from
     * @return created json array
     */
    public static JsonArray of(Collection<JsonElement> elements) {
        return new JsonArray(elements);
    }

    /**
     * Creates a new {@link JsonArray} from the given elements.
     *
     * @param elements to create the json array from
     * @return created json array
     */
    public static JsonArray of(JsonElement... elements) {
        return new JsonArray(elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonArray copy() {
        List<JsonElement> copiedElements = elements.stream()
            .map(JsonElement::copy)
            .toList();

        return JsonArray.of(copiedElements);
    }
}
