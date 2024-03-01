package com.github.argon.sos.moreoptions.json.mapper;

import com.github.argon.sos.moreoptions.json.JsonMapper;
import com.github.argon.sos.moreoptions.json.annotation.JsonProperty;
import com.github.argon.sos.moreoptions.json.element.JsonElement;
import com.github.argon.sos.moreoptions.json.element.JsonObject;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.ClassUtil;
import com.github.argon.sos.moreoptions.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static com.github.argon.sos.moreoptions.json.util.JsonUtil.toJsonKey;
import static com.github.argon.sos.moreoptions.util.MethodUtil.*;
import static com.github.argon.sos.moreoptions.util.ReflectionUtil.getAnnotation;
import static com.github.argon.sos.moreoptions.util.ReflectionUtil.invokeMethodOneArgument;

public class ObjectMapper implements Mapper<JsonObject> {

    private final static Logger log = Loggers.getLogger(ObjectMapper.class);

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

    @Override
    public Object mapJson(JsonObject jsonObject, TypeInfo<?> typeInfo) {
        Class<?> clazz = typeInfo.getTypeClass();

        if (!ReflectionUtil.hasNoArgsConstructor(clazz)) {
            throw new JsonMapperException("Class " + clazz.getCanonicalName() + " needs a no argument constructor");
        }

        Object instance;
        try {
            instance = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new JsonMapperException("Can not create instance of class " + clazz.getCanonicalName(), e);
        }

        for (Method method : clazz.getDeclaredMethods()) {
            if (!isSetterMethod(method)) {
                continue;
            }

            // get field for setter
            String fieldName = extractSetterGetterFieldName(method);
            Field field = ReflectionUtil.getDeclaredField(fieldName, clazz)
                .orElse(null);

            if (field == null) {
                log.debug("No field %s available for setter method %s in %s. Skipping.", fieldName, method.getName(), clazz.getCanonicalName());
                continue;
            }


            // read json key from annotation or generate from method name
            String jsonKey = getAnnotation(field, JsonProperty.class)
                .map(JsonProperty::key)
                .orElse(toJsonKey(method));

            if (!jsonObject.getMap().containsKey(jsonKey)) {
                log.debug("Json does not contain key %s for %s", jsonKey, method.getName());
                continue;
            }

            Type fieldType = field.getGenericType();
            TypeInfo<?> fieldTypeInfo = TypeInfo.get(fieldType);
            JsonElement jsonElement = jsonObject.getMap().get(jsonKey);

            Object mappedObject = JsonMapper.mapJson(jsonElement, fieldTypeInfo);
            // call setter method
            invokeMethodOneArgument(method, instance, mappedObject);
        }

        return instance;
    }

    @Override
    public JsonObject mapObject(Object object, TypeInfo<?> typeInfo) {
        Class<?> clazz = typeInfo.getTypeClass();

        JsonObject jsonObject = new JsonObject();
        for (Method method : clazz.getDeclaredMethods()) {
            if (!isGetterMethod(method)) {
                continue;
            }

            // get field for getter
            String fieldName = extractSetterGetterFieldName(method);
            Field field = ReflectionUtil.getDeclaredField(fieldName, clazz)
                .orElse(null);

            if (field == null) {
                log.debug("No field %s available for getter method %s in %s. Skipping.", fieldName, method.getName(), clazz.getCanonicalName());
                continue;
            }

            // read json key from annotation or generate from method name
            String jsonKey = getAnnotation(field, JsonProperty.class)
                .map(JsonProperty::key)
                .orElse(toJsonKey(method));

            Type fieldType = field.getGenericType();
            TypeInfo<?> fieldTypeInfo = TypeInfo.get(fieldType);

            // call getter method
            Object returnedObject = ReflectionUtil.invokeMethod(method, object);
            jsonObject.put(jsonKey, JsonMapper.mapObject(returnedObject, fieldTypeInfo));
        }

        return jsonObject;
    }
}
