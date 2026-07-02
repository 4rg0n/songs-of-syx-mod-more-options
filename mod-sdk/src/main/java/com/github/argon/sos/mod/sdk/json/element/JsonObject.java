package com.github.argon.sos.mod.sdk.json.element;

import com.github.argon.sos.mod.sdk.json.JsonPath;
import com.github.argon.sos.mod.sdk.util.ClassUtil;
import lombok.EqualsAndHashCode;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a complex object in json.
 */
@EqualsAndHashCode
public class JsonObject implements JsonElement {

    private final Map<String, JsonTuple> map = new HashMap<>();

    /**
     * Returns the entries in the objects as a collection of {@link JsonTuple}s.
     *
     * @return collection of json tuples
     */
    public Collection<JsonTuple> entries() {
        return map.values();
    }

    /**
     * Returns the {@link JsonTuple} for the given key.
     *
     * @param key of the element
     * @return json tuple
     */
    public JsonTuple getTuple(String key) {
        return map.get(key);
    }

    /**
     * Returns the value of the {@link JsonTuple} for the given key.
     *
     * @param key of the element
     * @return value of the json tuple
     */
    public JsonElement get(String key) {
        return map.get(key).getValue();
    }

    /**
     * Adds a new element to the entries
     *
     * @param element to add
     */
    public void put(JsonElement element) {
        put((JsonTuple) element);
    }

    /**
     * Adds a new {@link JsonTuple} to the entries
     *
     * @param tuple to add
     */
    public void put(JsonTuple tuple) {
        map.put(tuple.getKey(), tuple);
    }

    /**
     * Adds a new {@link JsonElement} with given key to the entries
     *
     * @param key to use for the element
     * @param element to add
     */
    public void put(String key, JsonElement element) {
        map.put(key, new JsonTuple(key, element));
    }

    /**
     * Adds a new {@link JsonElement} with given enum key to the entries
     *
     * @param key to use for the element
     * @param element to add
     */
    public void put(Enum<?> key, JsonElement element) {
        map.put(key.name(), JsonTuple.of(key.name(), element));
    }

    /**
     * Checks whether the object has a given key.
     *
     * @param key to check
     * @return whether object has key or not
     */
    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    /**
     * Returns a list with all keys of the object.
     *
     * @return keys of the object
     */
    public List<String> keys() {
        return new ArrayList<>(map.keySet());
    }

    /**
     * Returns a list with all values of the object
     *
     * @return values of object
     */
    public List<JsonElement> values() {
        return entries().stream()
            .map(JsonTuple::getValue)
            .toList();
    }

    /**
     * Gets a value by given key and cast to given class.
     *
     * @param key of the element / value
     * @param clazz to cast the element to
     * @return cast element
     * @param <T> type of the element
     */
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

    /**
     * Extracts a json element from the object by given json path.
     *
     * @param jsonPath pointing to the element to extract
     * @param clazz of the json element to extract
     * @return extracted json element
     * @param <T> type of the element to extract
     */
    public <T extends JsonElement> Optional<T> extract(String jsonPath, Class<T> clazz) {
        return extract(JsonPath.of(jsonPath), clazz);
    }

    /**
     * Extracts a json element from the object by given json path.
     *
     * @param jsonPath pointing to the element to extract
     * @param clazz of the json element to extract
     * @return extracted json element
     * @param <T> type of the element to extract
     */
    public <T extends JsonElement> Optional<T> extract(JsonPath jsonPath, Class<T> clazz) {
        return jsonPath.of(this, clazz);
    }

    /**
     * Transforms the elements in the object to a string
     *
     * @return elements as string
     */
    @Override
    public String toString() {
        return map.keySet().stream()
            .map(key -> key + ": " + map.get(key).getValue())
            .collect(Collectors.joining(", ", "{", "}"));
    }

    /**
     * Creates a new {@link JsonObject} with the given key and element.
     *
     * @param key to use
     * @param element to use
     * @return created json object
     */
    public static JsonObject of(String key, JsonElement element) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(key, element);
        return jsonObject;
    }

    /**
     * Creates a new {@link JsonObject} from a map with keys and elements.
     *
     * @param elements to use
     * @return created json object
     */
    public static JsonObject of(Map<String, JsonElement> elements) {
        JsonObject jsonObject = new JsonObject();
        elements.forEach(jsonObject::put);
        return jsonObject;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonObject copy() {
        Map<String, JsonElement> clonedElements = map.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().copy()
            ));

        return JsonObject.of(clonedElements);
    }

    /**
     * Checks whether the object has entries.
     *
     * @return whether the object has entries
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }
}
