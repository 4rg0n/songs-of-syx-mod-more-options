package com.github.argon.sos.mod.sdk.json.mapper;

import com.github.argon.sos.mod.sdk.json.JsonMapper;
import com.github.argon.sos.mod.sdk.json.annotation.JsonIgnore;
import com.github.argon.sos.mod.sdk.json.annotation.JsonProperty;
import com.github.argon.sos.mod.sdk.json.element.JsonElement;
import com.github.argon.sos.mod.sdk.json.element.JsonObject;
import com.github.argon.sos.mod.sdk.json.util.JsonUtil;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.util.ClassUtil;
import com.github.argon.sos.mod.sdk.util.MethodUtil;
import com.github.argon.sos.mod.sdk.util.ReflectionUtil;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * For mapping {@link JsonObject} to a java pojo and visa versa.
 */
public class ObjectMapper implements Mapper<JsonObject> {

    private final static Logger log = Loggers.getLogger(ObjectMapper.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz != null
            && !clazz.isEnum()
            && !clazz.isArray()
            && !Arrays.asList(
                    Void.class,
                    Boolean.class,
                    boolean.class,
                    Double.class,
                    Float.class,
                    BigDecimal.class,
                    double.class,
                    float.class,
                    Integer.class,
                    Byte.class,
                    Short.class,
                    Long.class,
                    BigInteger.class,
                    int.class,
                    short.class,
                    byte.class,
                    long.class
                ).contains(clazz)
            && !ClassUtil.instanceOf(clazz, Collection.class)
            && !ClassUtil.instanceOf(clazz, Map.class)
            && !ClassUtil.instanceOf(clazz, CharSequence.class);
    }


    /**
     * Maps a {@link JsonObject} to the given type / pojo if possible.
     *
     * @param jsonObject json to map
     * @param typeInfo to map the json into
     * @return mapped java object
     * @throws JsonMapperException when given type can not be mapped.
     */
    @Override
    public Object mapJson(JsonObject jsonObject, TypeInfo<?> typeInfo) {
        Class<?> clazz = typeInfo.getTypeClass();

        if (!ReflectionUtil.hasNoArgsConstructor(clazz)) {
            throw new JsonMapperException("Class " + clazz.getCanonicalName() + " needs a no argument constructor");
        }

        Object instance;
        try {
            instance = clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new JsonMapperException("Can not create instance of class " + clazz.getCanonicalName(), e);
        }

        for (Method method : clazz.getDeclaredMethods()) {
            if (!MethodUtil.isSetterMethod(method)) {
                continue;
            }

            // get field for setter
            String fieldName = MethodUtil.extractSetterGetterFieldName(method);
            Field field = ReflectionUtil.getDeclaredField(fieldName, clazz)
                .orElse(null);

            if (field == null) {
                log.trace("No field %s available for setter method %s in %s. Skipping.", fieldName, method.getName(), clazz.getCanonicalName());
                continue;
            }

            // ignore field?
            JsonIgnore ignoreFile = ReflectionUtil.getAnnotation(field, JsonIgnore.class).orElse(null);
            if (ignoreFile != null) {
                log.trace("Ignore field %s for setting");
                continue;
            }

            // read json key from annotation or generate from method name
            String jsonKey = ReflectionUtil.getAnnotation(field, JsonProperty.class)
                .map(JsonProperty::key)
                .orElse(JsonUtil.toJsonEKey(method));

            if (!jsonObject.containsKey(jsonKey)) {
                log.debug("Json does not contain key %s for %s", jsonKey, method.getName());
                continue;
            }

            Type fieldType = field.getGenericType();
            TypeInfo<?> fieldTypeInfo = TypeInfo.get(fieldType);
            JsonElement jsonElement = jsonObject.get(jsonKey);

            Object mappedObject;

            try {
                mappedObject = JsonMapper.mapJson(jsonElement, fieldTypeInfo);
            } catch (JsonMapperException e) {
                log.error("Could not map field %s#%s", clazz.getSimpleName(), fieldName);
                throw e;
            }

            try {
                // call setter method
                ReflectionUtil.invokeMethodOneArgument(method, instance, mappedObject);
            } catch (Exception e) {
                log.warn("Could not invoke method for setting %s into %s.%s",
                    mappedObject.getClass().getSimpleName(), instance.getClass().getSimpleName(), method.getName(), e);
            }
        }

        return instance;
    }

    @Override
    public JsonObject mapObject(Object object, TypeInfo<?> typeInfo) {
        Class<?> clazz = typeInfo.getTypeClass();
        MapMapper mapMapper = new MapMapper();

        if (mapMapper.supports(object.getClass())) {
            return mapMapper.mapObject(object, TypeInfo.get(typeInfo.getType()));
        } else {
            return mapObject(object, clazz);
        }
    }

    /**
     * Maps a java pojo to a {@link JsonObject}.
     *
     * @param object json to map
     * @param clazz of the given object
     * @return mapped json object
     */
    @NotNull
    private JsonObject mapObject(Object object, Class<?> clazz) {
        JsonObject jsonObject = new JsonObject();
        for (Method method : clazz.getDeclaredMethods()) {
            if (!MethodUtil.isGetterMethod(method)) {
                continue;
            }

            // get field for getter
            String fieldName = MethodUtil.extractSetterGetterFieldName(method);
            Field field = ReflectionUtil.getDeclaredField(fieldName, clazz)
                .orElse(null);

            if (field == null) {
                log.trace("No field %s available for getter method %s in %s. Skipping.", fieldName, method.getName(), clazz.getCanonicalName());
                continue;
            }

            // ignore field?
            JsonIgnore ignoreFile = ReflectionUtil.getAnnotation(field, JsonIgnore.class).orElse(null);
            if (ignoreFile != null) {
                log.trace("Ignore field %s for getting");
                continue;
            }

            // read json key from annotation or generate from method name
            String jsonKey = ReflectionUtil.getAnnotation(field, JsonProperty.class)
                .map(JsonProperty::key)
                .orElse(JsonUtil.toJsonEKey(method));

            Type fieldType = field.getGenericType();
            TypeInfo<?> fieldTypeInfo = TypeInfo.get(fieldType);

            // call getter method
            Object returnedObject = ReflectionUtil.invokeMethod(method, object);
            jsonObject.put(jsonKey, JsonMapper.mapObject(returnedObject, fieldTypeInfo));
        }

        return jsonObject;
    }

}
