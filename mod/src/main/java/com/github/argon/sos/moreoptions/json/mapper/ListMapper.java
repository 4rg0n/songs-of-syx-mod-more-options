package com.github.argon.sos.moreoptions.json.mapper;

import com.github.argon.sos.moreoptions.json.JsonMapper;
import com.github.argon.sos.moreoptions.json.element.JsonArray;
import com.github.argon.sos.mod.sdk.util.ClassUtil;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.argon.sos.mod.sdk.util.TypeUtil.isAssignableFrom;

public class ListMapper implements Mapper<JsonArray> {
    @Override
    public boolean supports(Class<?> clazz) {
        if (clazz == null || clazz.isArray()) {
            return false;
        }

        return ClassUtil.instanceOf(clazz, Collection.class);
    }

    @Override
    public Object mapJson(JsonArray json, TypeInfo<?> typeInfo) {
        TypeInfo<?> elementType;
        Type type = typeInfo.getType();
        Type[] genericTypes = typeInfo.getGenericTypes();

        if (genericTypes.length == 0) {
            throw new JsonMapperException("Type " + type.getTypeName() + " has no generic for mapping entry");
        }

        elementType = TypeInfo.get(genericTypes[0]);

        if (isAssignableFrom(type, LinkedList.class)) {
            return json.getElements().stream()
                .map(jsonElement -> JsonMapper.mapJson(jsonElement, elementType))
                .distinct()
                .collect(Collectors.toCollection(LinkedList::new));
        } if (isAssignableFrom(type, TreeSet.class)) {
            return json.getElements().stream()
                .map(jsonElement -> JsonMapper.mapJson(jsonElement, elementType))
                .collect(Collectors.toCollection(TreeSet::new));
        } else if (isAssignableFrom(type, Set.class)) {
            return json.getElements().stream()
                .map(jsonElement -> JsonMapper.mapJson(jsonElement, elementType))
                .collect(Collectors.toSet());
        } else if (isAssignableFrom(type, List.class) || isAssignableFrom(type, ArrayList.class)) {
            return json.getElements().stream()
                .map(jsonElement ->  JsonMapper.mapJson(jsonElement, elementType))
                .collect(Collectors.toList());
        } else {
            throw new JsonMapperException("Can not map " + JsonArray.class.getSimpleName() + " to type " + type.getTypeName());
        }
    }

    @Override
    public JsonArray mapObject(Object object, TypeInfo<?> typeInfo) {
        if (!(object instanceof Collection)) {
            throw new JsonMapperException("Can not map " + object.getClass().getTypeName() + " to " + JsonArray.class.getSimpleName());
        }

        TypeInfo<?> elementType;
        Type type = typeInfo.getType();
        Type[] genericTypes = typeInfo.getGenericTypes();

        if (genericTypes.length == 0) {
            throw new JsonMapperException("Type " + type.getTypeName() + " has no generic for mapping entry");
        }

        elementType = TypeInfo.get(genericTypes[0]);
        Collection<?> collection = (Collection<?>) object;
        JsonArray jsonArray = new JsonArray();

        collection.forEach(entry -> {
            jsonArray.add(JsonMapper.mapObject(entry, elementType));
        });

        return jsonArray;
    }
}
