package com.github.argon.sos.moreoptions.json.element;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@EqualsAndHashCode
public class JsonArray implements JsonElement {
    private final List<JsonElement> elements = new ArrayList<>();

    public JsonElement get(int index) {
        return elements.get(index);
    }

    public boolean add(JsonElement jsonElement) {
        return elements.add(jsonElement);
    }

    public void add(int index, JsonElement element) {
        elements.add(index, element);
    }

    @Override
    public String toString() {
        return elements.toString();
    }

    public int size() {
        return elements.size();
    }

    public <T extends JsonElement> List<T> as(Class<T> clazz) {
        return elements.stream()
            .filter(clazz::isInstance)
            .map(clazz::cast)
            .collect(Collectors.toList());
    }
    public static JsonArray of(Collection<JsonElement> elements) {
        return of(elements.toArray(new JsonElement[0]));
    }

    public static JsonArray of(JsonElement... elements) {
        JsonArray jsonArray = new JsonArray();

        for (JsonElement jsonElement : elements) {
            jsonArray.add(jsonElement);
        }

        return jsonArray;
    }
}
