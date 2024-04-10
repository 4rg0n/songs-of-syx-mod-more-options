package com.github.argon.sos.moreoptions.json.element;

import com.github.argon.sos.moreoptions.json.JsonPath;
import com.github.argon.sos.moreoptions.util.ClassUtil;
import lombok.EqualsAndHashCode;

import java.util.*;
import java.util.stream.Collectors;

@EqualsAndHashCode
public class JsonObject implements JsonElement {

    private final Map<String, JsonTuple> map = new HashMap<>();

    public Collection<JsonTuple> entries() {
        return map.values();
    }
    public JsonTuple getTuple(String key) {
        return map.get(key);
    }

    public JsonElement get(String key) {
        return map.get(key).getValue();
    }

    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    public void put(JsonElement tuple) {
        put((JsonTuple) tuple);
    }

    public void put(JsonTuple tuple) {
        map.put(tuple.getKey(), tuple);
    }

    public void put(String key, JsonElement value) {
        map.put(key, new JsonTuple(key, value));
    }

    public void put(String key, JsonTuple value) {
        map.put(key, value);
    }

    public void put(Enum<?> key, JsonTuple value) {
        map.put(key.name(), value);
    }

    public void put(Enum<?> key, JsonElement value) {
        map.put(key.name(), new JsonTuple(key.name(), value));
    }

    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    public List<String> keys() {
        return new ArrayList<>(map.keySet());
    }

    public List<JsonElement> values() {
        return entries().stream()
            .map(JsonTuple::getValue)
            .collect(Collectors.toList());
    }

    public <T extends JsonElement> Optional<T> getAs(String key, Class<T> clazz) {
        JsonElement jsonElement = get(key);

        if (!ClassUtil.instanceOf(jsonElement, clazz)) {
            return Optional.empty();
        }

        try {
            return Optional.of(clazz.cast(jsonElement));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public <T extends JsonElement> Optional<T> extract(String jsonPath, Class<T> clazz) {
        return JsonPath.get(jsonPath).get(this, clazz);
    }

    public <T extends JsonElement> Optional<T> extract(JsonPath jsonPath, Class<T> clazz) {
        return jsonPath.get(this, clazz);
    }

    @Override
    public String toString() {
        return map.keySet().stream()
            .map(key -> key + ": " + map.get(key).getValue())
            .collect(Collectors.joining(", ", "{", "}"));
    }

    public static JsonObject of(String key, JsonElement element) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(key, element);
        return jsonObject;
    }

    public static JsonObject of(Map<String, JsonElement> elements) {
        JsonObject jsonObject = new JsonObject();
        elements.forEach(jsonObject::put);
        return jsonObject;
    }

    @Override
    public JsonObject copy() {
        Map<String, JsonElement> clonedElements = map.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().copy()
            ));

        return JsonObject.of(clonedElements);
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }
}
