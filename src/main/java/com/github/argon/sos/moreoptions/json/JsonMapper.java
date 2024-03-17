package com.github.argon.sos.moreoptions.json;

import com.github.argon.sos.moreoptions.json.element.JsonElement;
import com.github.argon.sos.moreoptions.json.element.JsonNull;
import com.github.argon.sos.moreoptions.json.element.JsonObject;
import com.github.argon.sos.moreoptions.json.mapper.JsonMapperException;
import com.github.argon.sos.moreoptions.json.mapper.Mapper;
import com.github.argon.sos.moreoptions.json.mapper.Mappers;
import com.github.argon.sos.moreoptions.json.mapper.TypeInfo;
import com.github.argon.sos.moreoptions.json.mapper.jsone.JsonEMapper;
import com.github.argon.sos.moreoptions.json.mapper.jsone.JsonEMappers;
import snake2d.util.file.JsonE;

/**
 * For mapping objects into and from a {@link JsonElement}.
 * Also supports mapping {@link JsonElement} into the games {@link JsonE}.
 * See {@link this#mapJson(JsonObject)}
 */
public class JsonMapper {

    private final static Mappers mappers = Mappers.getDefault();
    private final static JsonEMappers JSON_E_MAPPERS = JsonEMappers.getDefault();

    public static <T> T mapJson(Json json, Class<T> clazz) {
        return mapJson(json.getRoot(), TypeInfo.get(clazz));
    }

    /**
     * Maps a {@link JsonElement} to a given class containing data from the json.
     * The class has to have setter methods.
     *
     * <pre>{@code
     *     MyPojo pojo = JsonMapper.mapJson(jsonObject, MyPojo.class);
     * }</pre>
     *
     * @param json to map
     * @param clazz of the mapped result
     * @return instance of the desired class with inserted values from json
     * @param <T> type of the mapped result
     */
    public static <T> T mapJson(JsonElement json, Class<T> clazz) {
        return mapJson(json, TypeInfo.get(clazz));
    }

    /**
     * Maps {@link JsonElement} to a given class via {@link TypeInfo} containing data from the json.
     *
     * <pre>{@code
     *     JsonArray jsonArray = new JsonArray();
     *     jsonArray.add(new JsonLong(1L));
     *     jsonArray.add(new JsonLong(2L));
     *     jsonArray.add(new JsonLong(3L));
     *     // you will need a {@link TypeInfo} with the type info as generic
     *     List<Integer> integerList = JsonMapper.mapJson(jsonArray, new TypeInfo<List<Integer>>(){});
     * }</pre>
     *
     * @param json to map
     * @param typeInfo containing type information from the class to map
     * @return instance of the desired class with inserted values from json
     * @param <T> type of the mapped result
     */
    public static <T> T mapJson(JsonElement json, TypeInfo<T> typeInfo) {
        Class<? super T> typeClass = typeInfo.getTypeClass();

        @SuppressWarnings("rawtypes")
        Mapper mapper = mappers.findOne(typeClass)
            .orElseThrow(() -> new JsonMapperException("No mapper found for json element " + typeClass.getTypeName()));

        try {
            //noinspection unchecked
            return (T) mapper.mapJson(json, typeInfo);
        } catch (RuntimeException e) {
            throw new JsonMapperException(
                "Could not map " + json.getClass().getSimpleName() + " to " +
                typeClass.getTypeName() + " with mapper " + mapper.getClass().getSimpleName(), e);
        }
    }

    /**
     * Maps a {@link JsonObject} to the games {@link JsonE}.
     *
     * <pre>{@code
     *     JsonObject jsonObject = new JsonObject();
     *     jsonObject.put("1", new JsonLong(1L));
     *     jsonObject.put("2", new JsonLong(2L));
     *     jsonObject.put("3", new JsonLong(3L));
     *     JsonE jsonE = JsonMapper.mapJsonE(jsonObject);
     * }</pre>
     *
     * @param jsonObject to map
     * @return games json format
     */
    public static JsonE mapJson(JsonObject jsonObject) {
        JsonE json = new JsonE();

        jsonObject.getMap().forEach((key, jsonElement) -> {
            JsonMapper.mapIntoJsonE(json, key, jsonElement);
        });

        return json;
    }

    /**
     * Maps an object to a {@link JsonElement} containing data from the object.
     * <pre>{@code
     *     MyPojo pojo = new MyPojo();
     *     pojo.setName("name");
     *     pojo.setAmount(1);
     *     JsonObject jsonObject = (JsonObject) JsonMapper.mapObject(pojo);
     * }</pre>
     *
     * @param object to map
     * @return json element containing data from given object
     */
    public static JsonElement mapObject(Object object) {
        if (object == null) {
            return new JsonNull();
        }

        TypeInfo<?> typeInfo = TypeInfo.get(object.getClass());
        try {
            return mapObject(object, typeInfo);
        } catch (RuntimeException e) {
            throw new JsonMapperException("Could not map " + object.getClass().getTypeName() + " to " + JsonElement.class.getSimpleName(), e);
        }
    }

    /**
     * Maps an object to a {@link JsonElement} containing data from the object.
     *
     * <pre>{@code
     *     Map<String, List<String>> map = new HashMap();
     *     map.put("test", Arrays.asList("test", "test"));
     *     // you will need a {@link TypeInfo} with the type info as generic
     *     JsonObject jsonObject = (JsonObject) JsonMapper.mapObject(map, new TypeInfo<Map<String, List<String>>>(){});
     * }</pre>
     *
     * @param object to map
     * @param typeInfo containing type information from the class to map
     * @return json element containing data from given object
     */
    public static JsonElement mapObject(Object object, TypeInfo<?> typeInfo) {
        if (object == null || typeInfo == null) {
            return new JsonNull();
        }
        Class<?> typeClass = typeInfo.getTypeClass();

        Mapper<?> mapper = mappers.findOne(typeClass)
            .orElseThrow(() -> new JsonMapperException("No mapper found for object " + typeClass.getTypeName()));

        try {
            return mapper.mapObject(object, typeInfo);
        } catch (RuntimeException e) {
            throw new JsonMapperException(
                "Could not map " + typeClass.getTypeName() +
                " to JsonElement with mapper " + mapper.getClass().getSimpleName(), e);
        }
    }

    /**
     * Maps and inserts a {@link JsonElement} into a {@link JsonE}.
     *
     * @param json to set the mapped {@link JsonElement} into
     * @param key where to set
     * @param jsonElement to set into given games json
     */
    public static void mapIntoJsonE(JsonE json, String key, JsonElement jsonElement) {
        Class<? extends JsonElement> jsonElementClass = jsonElement.getClass();

        @SuppressWarnings("rawtypes")
        JsonEMapper jsonEMapper = JSON_E_MAPPERS.findOne(jsonElementClass)
            .orElseThrow(() -> new JsonMapperException("No mapper found for json element " + jsonElementClass.getSimpleName()));

        try {
            //noinspection unchecked
            jsonEMapper.mapJsonE(json, key, jsonElement);
        } catch (RuntimeException e) {
            throw new JsonMapperException(
                "Could not map " + jsonElementClass.getSimpleName() +
                    " to " + JsonE.class.getSimpleName() + " with mapper " + jsonEMapper.getClass().getSimpleName(), e);
        }
    }
}
