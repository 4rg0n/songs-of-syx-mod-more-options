package com.github.argon.sos.moreoptions.util;


import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * For accessing game classes and properties, which are normally not public available
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReflectionUtil {

    private final static Logger log = Loggers.getLogger(ReflectionUtil.class);

    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    private static final Type[] NO_TYPES = {};

    public static void setField(String fieldName, Object instance, Object newValue) throws NoSuchFieldException, IllegalAccessException {
        Field field = instance.getClass().getDeclaredField(fieldName);
        setField(field, instance, newValue);
    }

    public static void setField(Field field, Object instance, Object newValue) throws NoSuchFieldException, IllegalAccessException {
        field.setAccessible(true);
        try {
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
        } catch (Exception e) {
            log.debug("Could not manipulate modifiers of field %s in %s", field.getName(), instance.getClass().getName(), e);
        }

        field.set(instance, newValue);
    }

    public static <T> Optional<T> getDeclaredField(String fieldName, Object instance)  {
        try {
            Field field = instance.getClass().getDeclaredField(fieldName);
            return getDeclaredFieldValue(field, instance);
        } catch (NoSuchFieldException e) {
            log.error("Field %s does not exist", fieldName, e);
            return Optional.empty();
        }
    }

    public static <T> Optional<T> getDeclaredFieldValue(Field field, Object instance) {
        field.setAccessible(true);

        try {
            //noinspection unchecked
            return Optional.ofNullable((T) field.get(instance));
        } catch (Exception e) {
            log.error("Can not access field %s in %s.",
                    field.getName(), instance.getClass().getSimpleName(), e);
            return Optional.empty();
        }
    }

    public static <T> Optional<T> getDeclaredFieldValue(String fieldName, Object instance) {

        //noinspection unchecked
        return getDeclaredField(fieldName, instance.getClass()).map(field ->
            (T) getDeclaredFieldValue(field, instance).orElse(null)
        );
    }

    public static <T> Optional<T> getDeclaredFieldValue(String fieldName, Class<?> clazz) {

        return getDeclaredField(fieldName, clazz).map(field -> {
                field.setAccessible(true);
                try {
                    //noinspection unchecked
                    return (T) field.get(null);
                } catch (Exception e) {
                    log.error("Can not access field %s in %s.",
                        field.getName(), clazz.getSimpleName(), e);
                    return null;
                }
            }
        );
    }

    public static Optional<Field> getDeclaredField(String fieldName, Class<?> clazz) {
        try {
            return Optional.of(clazz.getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            log.warn("Can not access field %s in %s.", fieldName, clazz.getSimpleName(), e);
            return Optional.empty();
        }
    }

    public static List<Field> getDeclaredFields(Class<?> fieldClazz, Class<?> clazz) {
        List<Field> fields = new ArrayList<>();

        for (Field declaredField : clazz.getDeclaredFields()) {
            if (declaredField.getType().equals(fieldClazz)) {
                fields.add(declaredField);
            }
        }

        return fields;
    }

    public static <T> List<T> getDeclaredFieldValues(Class<?> fieldClazz, Object instance) {
        return ReflectionUtil.getDeclaredFields(
                fieldClazz,
                instance.getClass())
            .stream().map(field ->
                ReflectionUtil.<T>getDeclaredFieldValue(field, instance)
                    .orElse(null))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    public static <T> Map<Field, T> getDeclaredFieldValuesMap(Class<?> fieldClazz, Object instance) {
        Map<Field, T> fieldValues = new HashMap<>();

        ReflectionUtil.getDeclaredFields(
                fieldClazz,
                instance.getClass())
            .forEach(field -> {
                T value = ReflectionUtil.<T>getDeclaredFieldValue(field, instance)
                    .orElse(null);
                fieldValues.put(field, value);
            });

        return fieldValues;
    }

    public static <T> Map<Field, T> getDeclaredFieldValuesMap(Class<?> fieldClazz, Class<?> clazz) {
        Map<Field, T> fieldValues = new HashMap<>();

        ReflectionUtil.getDeclaredFields(
                fieldClazz,
                clazz)
            .forEach(field -> {
                T value = ReflectionUtil.<T>getDeclaredFieldValue(field, clazz)
                    .orElse(null);
                fieldValues.put(field, value);
            });

        return fieldValues;
    }

    public static Optional<Field> getField(String fieldName, Class<?> clazz) {
        try {
            return Optional.of(clazz.getField(fieldName));
        } catch (NoSuchFieldException e) {
            log.warn("Can not access field %s in %s.", fieldName, clazz.getSimpleName(), e);
            return Optional.empty();
        }
    }

    public static <T extends Annotation> Optional<T> getAnnotation(Field field, Class<T> annotationClass) {
        field.setAccessible(true);

        if (!field.isAnnotationPresent(annotationClass)) {
            return Optional.empty();
        }

        return Optional.of(field.getAnnotation(annotationClass));
    }

    public static <T extends Annotation> Optional<T> getAnnotation(Class<?> clazz, Class<T> annotationClass) {
        if (!clazz.isAnnotationPresent(annotationClass)) {
            return Optional.empty();
        }

        return Optional.of(clazz.getAnnotation(annotationClass));
    }


    public static @Nullable Object invokeMethod(Method method, Object target) {
        return invokeMethod(method, target, EMPTY_OBJECT_ARRAY);
    }

    public static @Nullable Object invokeMethod(String methodName, Class<?> target, Object instance,  Object... args) {
        Method method = null;
        Class<?>[] paramTypes = Arrays.stream(args).map(Object::getClass).toArray(Class[]::new);

        try {
            method = target.getDeclaredMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            handleReflectionException(e);
        }

        return invokeMethod(method, instance, args);
    }

    public static @Nullable Object invokeMethodOneArgument(Method method, Object instance, Object arg) {
        return invokeMethod(method, instance, arg);
    }

    public static @Nullable Object invokeMethod(@Nullable Method method, Object instance, Object... args) {
        if (method == null) {
            log.trace("No method to invoke. Returning null");
            return null;
        }

        try {
            method.setAccessible(true);
            return method.invoke(instance, args);
        }
        catch (Exception ex) {
            handleReflectionException(ex);
        }
        throw new IllegalStateException("Should never get here");
    }

    public static void handleReflectionException(Exception ex) {
        if (ex instanceof NoSuchMethodException) {
            throw new IllegalStateException("Method not found: " + ex.getMessage());
        }
        if (ex instanceof IllegalAccessException) {
            throw new IllegalStateException("Could not access method or field: " + ex.getMessage());
        }
        if (ex instanceof InvocationTargetException) {
            handleInvocationTargetException((InvocationTargetException) ex);
        }
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        }
        throw new UndeclaredThrowableException(ex);
    }

    public static void handleInvocationTargetException(InvocationTargetException ex) {
        rethrowRuntimeException(ex.getTargetException());
    }

    public static void rethrowRuntimeException(Throwable ex) {
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        }
        if (ex instanceof Error) {
            throw (Error) ex;
        }
        throw new UndeclaredThrowableException(ex);
    }

    public static Optional<Class<?>> getGenericClass(String fieldName, Class<?> clazz) {
        return getDeclaredField(fieldName, clazz)
            .flatMap(ReflectionUtil::getGenericClass);
    }

    public static Optional<Class<?>> getGenericClass(Field field) {
        List<Class<?>> classes = getGenericClasses(field);

        if (classes.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(classes.get(0));
    }

    public static List<Class<?>> getGenericClasses(Field field) {
        Type[] genericTypes = getGenericTypes(field);
        List<Class<?>> classes = new ArrayList<>();

        for (Type typeArgument : genericTypes) {
            classes.add((Class<?>) typeArgument);
        }

        return classes;
    }

    public static Type[] getGenericTypes(Field field) {
        Type fieldGenericType = field.getGenericType();
        return getGenericTypes(fieldGenericType);
    }

    public static Type[] getGenericTypes(Type type) {
        if (!(type instanceof ParameterizedType)) {
            return NO_TYPES;
        }

        return getGenericTypes((ParameterizedType) type);
    }

    public static Type[] getGenericTypes(ParameterizedType parameterizedType) {
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        if (typeArguments.length == 0) {
            return NO_TYPES;
        }

        return typeArguments;
    }

    public static boolean hasNoArgsConstructor(Class<?> clazz) {
        return Stream.of(clazz.getConstructors())
            .anyMatch((c) -> c.getParameterCount() == 0);
    }
}
