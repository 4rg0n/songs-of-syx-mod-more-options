package com.github.argon.sos.mod.sdk.json.mapper;

import com.github.argon.sos.mod.sdk.json.JsonMapper;
import com.github.argon.sos.mod.sdk.json.element.JsonObject;
import com.github.argon.sos.mod.sdk.util.ClassCastUtil;
import com.github.argon.sos.mod.sdk.util.ClassUtil;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static com.github.argon.sos.mod.sdk.util.TypeUtil.getRawType;
import static com.github.argon.sos.mod.sdk.util.TypeUtil.isAssignableFrom;

public class MapMapper implements Mapper<JsonObject> {
    @Override
    public boolean supports(Class<?> clazz) {
        return ClassUtil.instanceOf(clazz, Map.class);
    }

    @Override
    public Object mapJson(JsonObject json, TypeInfo<?> typeInfo) {
        Type type = typeInfo.getType();

        if (!isAssignableFrom(type, Map.class)) {
            throw new JsonMapperException("Can not map " + JsonObject.class.getSimpleName() + " to type " + type.getTypeName());
        }

        Type[] genericTypes = typeInfo.getGenericTypes();
        if (genericTypes.length != 2) {
            throw new JsonMapperException("Type " + type.getTypeName() + " should have two generics");
        }

        Type keyType = genericTypes[0];
        Type valueType = genericTypes[1];
        TypeInfo<?> valueTypeInfo = TypeInfo.get(valueType);

        if (isAssignableFrom(keyType, CharSequence.class)) {
            // do string key mapping
            HashMap<String, Object> map = new HashMap<>();
            json.entries().forEach(tuple -> {
                map.put(tuple.getKey(), JsonMapper.mapJson(tuple.getValue(), valueTypeInfo));
            });

            return map;
        } else if (isAssignableFrom(keyType, Enum.class)) {
            // do enum key mapping
            HashMap<Enum<?>, Object> map = new HashMap<>();
            json.entries().forEach(tuple -> {
                Enum<?> keyEnum = ClassCastUtil.toEnum(tuple.getKey(), getRawType(keyType));
                map.put(keyEnum, JsonMapper.mapJson(tuple.getValue(), valueTypeInfo));
            });

            return map;
        } else {
            throw new JsonMapperException("Only Strings and Enums are supported as key in " + type.getTypeName());
        }

    }

    @Override
    public JsonObject mapObject(Object object, TypeInfo<?> typeInfo) {
        Type type = typeInfo.getType();

        if (!isAssignableFrom(type, Map.class)) {
            throw new JsonMapperException("Can not map" + type.getTypeName() + " to " + JsonObject.class.getSimpleName());
        }

        Type[] genericTypes = typeInfo.getGenericTypes();
        if (genericTypes.length != 2) {
            throw new JsonMapperException("Type " + type.getTypeName() + " should have two generics");
        }

        Type valueType = genericTypes[1];
        TypeInfo<?> valueTypeInfo = TypeInfo.get(valueType);

        JsonObject jsonObject = new JsonObject();
        Map<?, Object> map = (Map<?, Object>) object;

        map.forEach((mapKey, mapValue) -> {
            if (mapKey instanceof Enum) {
                jsonObject.put((Enum<?>) mapKey, JsonMapper.mapObject(mapValue, valueTypeInfo));
            } else {
                jsonObject.put((String) mapKey, JsonMapper.mapObject(mapValue, valueTypeInfo));
            }
        });

        return jsonObject;
    }
}
