package com.github.argon.sos.mod.sdk.json.mapper;

import com.github.argon.sos.mod.sdk.json.JsonMapper;
import com.github.argon.sos.mod.sdk.json.element.JsonArray;
import com.github.argon.sos.mod.sdk.util.ClassUtil;
import com.github.argon.sos.mod.sdk.util.TypeUtil;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * For mapping {@link JsonArray} to a java {@link List}, {@link LinkedList} {@link Set}, or {@link TreeSet} and visa versa.
 */
public class ListMapper implements Mapper<JsonArray> {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class<?> clazz) {
        if (clazz == null || clazz.isArray()) {
            return false;
        }

        return ClassUtil.instanceOf(clazz, Collection.class);
    }

    /**
     * Maps a {@link JsonArray} to a {@link List}, {@link LinkedList} {@link Set}, or {@link TreeSet}.
     *
     * @param json json to map
     * @param typeInfo to map the json into
     * @return mapped java object
     * @throws JsonMapperException when given type can not be mapped.
     */
    @Override
    public Object mapJson(JsonArray json, TypeInfo<?> typeInfo) {
        TypeInfo<?> elementType;
        Type type = typeInfo.getType();
        Type[] genericTypes = typeInfo.getGenericTypes();

        if (genericTypes.length == 0) {
            throw new JsonMapperException("Type " + type.getTypeName() + " has no generic for mapping entry");
        }

        elementType = TypeInfo.get(genericTypes[0]);

        if (TypeUtil.isAssignableFrom(type, LinkedList.class)) {
            return json.getElements().stream()
                .map(jsonElement -> JsonMapper.mapJson(jsonElement, elementType))
                .distinct()
                .collect(Collectors.toCollection(LinkedList::new));
        } if (TypeUtil.isAssignableFrom(type, TreeSet.class)) {
            return json.getElements().stream()
                .map(jsonElement -> JsonMapper.mapJson(jsonElement, elementType))
                .collect(Collectors.toCollection(TreeSet::new));
        } else if (TypeUtil.isAssignableFrom(type, Set.class)) {
            return json.getElements().stream()
                .map(jsonElement -> JsonMapper.mapJson(jsonElement, elementType))
                .collect(Collectors.toSet());
        } else if (TypeUtil.isAssignableFrom(type, List.class) || TypeUtil.isAssignableFrom(type, ArrayList.class)) {
            return json.getElements().stream()
                .map(jsonElement ->  JsonMapper.mapJson(jsonElement, elementType))
                .toList();
        } else {
            throw new JsonMapperException("Can not map " + JsonArray.class.getSimpleName() + " to type " + type.getTypeName());
        }
    }

    /**
     * Maps a {@link Collection} to a {@link JsonArray}.
     *
     * @param object to map
     * @param typeInfo information about the type of the given object
     * @return mapped array json element
     * @throws JsonMapperException when given type can not be mapped.
     */
    @Override
    public JsonArray mapObject(Object object, TypeInfo<?> typeInfo) {
        if (!(object instanceof Collection<?> collection)) {
            throw new JsonMapperException("Can not map " + object.getClass().getTypeName() + " to " + JsonArray.class.getSimpleName());
        }

        TypeInfo<?> elementType;
        Type type = typeInfo.getType();
        Type[] genericTypes = typeInfo.getGenericTypes();

        if (genericTypes.length == 0) {
            throw new JsonMapperException("Type " + type.getTypeName() + " has no generic for mapping entry");
        }

        elementType = TypeInfo.get(genericTypes[0]);
        JsonArray jsonArray = new JsonArray();

        collection.forEach(entry -> {
            jsonArray.add(JsonMapper.mapObject(entry, elementType));
        });

        return jsonArray;
    }
}
