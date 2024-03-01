package com.github.argon.sos.moreoptions.json.element;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


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

    @Override
    public String toString() {
        return elements.toString();
    }
}
