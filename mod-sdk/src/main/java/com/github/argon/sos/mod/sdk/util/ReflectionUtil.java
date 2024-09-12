package com.github.argon.sos.mod.sdk.util;


import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * For accessing Game and Java {@link Class}es and {@link Field}s, which are normally not public available
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReflectionUtil {

    private final static Logger log = Loggers.getLogger(ReflectionUtil.class);

    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    private static final Type[] NO_TYPES = {};

    /**
     * @param fieldName of the field to look for
     * @param clazz containing the field
     * @return found field matching the given name
     */
    public static Optional<Field> getField(String fieldName, Class<?> clazz) {
        try {
            return Optional.of(clazz.getField(fieldName));
        } catch (NoSuchFieldException e) {
            log.warn("Can not access field %s in %s.", fieldName, clazz.getSimpleName(), e);
            return Optional.empty();
        }
    }

    /**
     * Sets a given value into a {@link Field}
     *
     * @param fieldName to set the value in
     * @param instance containing the field
     * @param newValue to set into the field
     *
     * @throws IllegalAccessException when the field can not be accessed e.g. when it is final
     * @throws NoSuchFieldException when the field does not exist
     */
    public static void setField(String fieldName, Object instance, Object newValue) throws IllegalAccessException, NoSuchFieldException {
        Field field = instance.getClass().getDeclaredField(fieldName);
        setField(field, instance, newValue);
    }

    /**
     * Sets a given value into a {@link Field}
     *
     * @param field to set the value in
     * @param instance containing the field
     * @param newValue to set into the field
     *
     * @throws IllegalAccessException when the field can not be accessed e.g. when it is final
     */
    public static void setField(Field field, Object instance, Object newValue) throws IllegalAccessException {
        if (Modifier.isFinal(field.getModifiers())) {
            throw new IllegalAccessException(String.format("Cannot set values into FINAL field %s in %s", field.getName(), instance.getClass().getName()));
        }

        boolean accessible = field.isAccessible();
        field.setAccessible(true);

        try {
            field.set(instance, newValue);
        } catch (IllegalAccessException e) {
            log.warn("Could not set value into field %s in %s", field.getName(), instance.getClass().getName());
            throw e;
        }

        // reset access
        field.setAccessible(accessible);
    }

    /**
     * @param fieldName of the field to look for
     * @param instance containing the field
     * @return the found field
     */
    public static Optional<Field> getDeclaredField(String fieldName, Object instance)  {
        try {
            Field field = instance.getClass().getDeclaredField(fieldName);
            return Optional.of(field);
        } catch (NoSuchFieldException e) {
            log.warn("Field %s does not exist", fieldName, e);
            return Optional.empty();
        }
    }

    /**
     * @return all fields with a given annotation attached to it
     */
    public static <T extends Annotation> List<Field> getDeclaredFieldsWithAnnotation(Class<T> annotationClass, Class<?> clazz)  {
        return Arrays.stream(clazz.getDeclaredFields())
            .filter(field -> field.isAnnotationPresent(annotationClass))
            .collect(Collectors.toList());
    }

    /**
     * @param fieldName of the field to look for
     * @param clazz containing the field
     * @return the found field
     */
    public static Optional<Field> getDeclaredField(String fieldName, Class<?> clazz) {
        try {
            return Optional.of(clazz.getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            log.warn("Can not access field %s in %s.", fieldName, clazz.getSimpleName(), e);
            return Optional.empty();
        }
    }

    /**
     * @param fieldClazz of the field to look for
     * @param clazz containing the field
     * @return a list of fields matching the given fieldClass
     */
    public static List<Field> getDeclaredFields(Class<?> fieldClazz, Class<?> clazz) {
        List<Field> fields = new ArrayList<>();

        for (Field declaredField : clazz.getDeclaredFields()) {
            if (declaredField.getType().equals(fieldClazz)) {
                fields.add(declaredField);
            }
        }

        return fields;
    }

    /**
     * @param field to get the value from
     * @param instance containing the field
     * @return the value of a {@link Field} if present
     */
    public static <T> Optional<T> getDeclaredFieldValue(Field field, Object instance) {
        boolean accessible = field.isAccessible();
        field.setAccessible(true);

        try {
            //noinspection unchecked
            return Optional.ofNullable((T) field.get(instance));
        } catch (Exception e) {
            log.error("Can not access field %s in %s.",
                    field.getName(), instance.getClass().getSimpleName(), e);
            return Optional.empty();
        } finally {
            field.setAccessible(accessible);
        }
    }

    /**
     * @param fieldName of the field to get the value from
     * @param instance containing the field
     * @return the value of a {@link Field} if present
     */
    public static <T> Optional<T> getDeclaredFieldValue(String fieldName, Object instance) {

        //noinspection unchecked
        return getDeclaredField(fieldName, instance.getClass()).map(field ->
            (T) getDeclaredFieldValue(field, instance).orElse(null)
        );
    }

    /**
     * @param fieldName of the field to get the value from
     * @param clazz containing the field
     * @return the value of a {@link Field} if present
     */
    public static <T> Optional<T> getDeclaredFieldValue(String fieldName, Class<?> clazz) {

        return getDeclaredField(fieldName, clazz).map(field -> {
            if (!Modifier.isStatic(field.getModifiers())) {
                log.error("Can not access non static field %s in class %s.",
                    field.getName(), clazz.getSimpleName());
                return null;
            }

            boolean accessible = field.isAccessible();
            field.setAccessible(true);
                try {
                    //noinspection unchecked
                    return (T) field.get(null);
                } catch (Exception e) {
                    log.error("Can not access field %s in class %s.",
                        field.getName(), clazz.getSimpleName(), e);
                    return null;
                } finally {
                    field.setAccessible(accessible);
                }
            }
        );
    }

    /**
     * @param fieldClazz of the field to look for
     * @param instance containing the field
     * @return a list of field values from fields matching the given fieldClass
     */
    public static <T> List<T> getDeclaredFieldValues(Class<T> fieldClazz, Object instance) {
        return ReflectionUtil.getDeclaredFields(
                fieldClazz,
                instance.getClass())
            .stream().map(field ->
                ReflectionUtil.<T>getDeclaredFieldValue(field, instance)
                    .orElse(null))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    /**
     * Field values structured as a map.
     *
     * @param fieldClazz of the field to look for
     * @param instance containing the field
     * @return a map of field names with values from fields matching the given fieldClass
     */
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

    /**
     * Field values structured as a map.
     *
     * @param fieldClazz of the field to look for
     * @param clazz containing the field
     * @return a map of field names with values from fields matching the given fieldClass
     */
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

    public static Optional<Method> getMethod(Class<?> clazz, String methodName, Class<?>... parameters) {
        try {
            return Optional.of(clazz.getDeclaredMethod(methodName, parameters));
        } catch (NoSuchMethodException e) {
            log.error("No method %s found in class %s matching given parameters",
                methodName, clazz.getSimpleName());
            return Optional.empty();
        }
    }

    /**
     * Read an {@link Annotation} from a {@link Field}
     *
     * @param field to read annotations from
     * @param annotationClass which annotation class to look for
     * @return found annotation matching the given class
     */
    public static <T extends Annotation> Optional<T> getAnnotation(Field field, Class<T> annotationClass) {
        boolean accessible = field.isAccessible();
        field.setAccessible(true);

        if (!field.isAnnotationPresent(annotationClass)) {
            return Optional.empty();
        }

        T annotation = field.getAnnotation(annotationClass);
        field.setAccessible(accessible);

        return Optional.of(annotation);
    }

    /**
     * Read an {@link Annotation} from a {@link Method}
     *
     * @param method to read annotations from
     * @param annotationClass which annotation class to look for
     * @return found annotation matching the given class
     */
    public static <T extends Annotation> Optional<T> getAnnotation(Method method, Class<T> annotationClass) {
        boolean accessible = method.isAccessible();
        method.setAccessible(true);

        if (!method.isAnnotationPresent(annotationClass)) {
            return Optional.empty();
        }

        T annotation = method.getAnnotation(annotationClass);
        method.setAccessible(accessible);

        return Optional.of(annotation);
    }

    /**
     * Read an {@link Annotation} from a {@link Class}
     *
     * @param clazz to read annotations from
     * @param annotationClass which annotation class to look for
     * @return found annotation matching the given class
     */
    public static <T extends Annotation> Optional<T> getAnnotation(Class<?> clazz, Class<T> annotationClass) {
        if (!clazz.isAnnotationPresent(annotationClass)) {
            return Optional.empty();
        }

        return Optional.of(clazz.getAnnotation(annotationClass));
    }

    /**
     * Executes a method without arguments on the given target
     *
     * @param method to execute
     * @param target containing the method
     * @return whatever the method returns
     */
    public static @Nullable Object invokeMethod(Method method, Object target) {
        return invokeMethod(method, target, EMPTY_OBJECT_ARRAY);
    }

    /**
     * Executes a method with multiple arguments on the given target
     *
     * @param methodName of the method to execute
     * @param instance containing the method
     * @param args arguments of the method
     * @return whatever the method returns
     */
    public static @Nullable Object invokeMethod(String methodName, Object instance, Object... args) {
        Method method = null;
        Class<?>[] paramTypes = Arrays.stream(args).map(Object::getClass).toArray(Class[]::new);

        try {
            method = instance.getClass().getDeclaredMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            handleReflectionException(e);
        }

        return invokeMethod(method, instance, args);
    }

    /**
     * Executes a method with exactly one argument on the given target
     *
     * @param method to execute
     * @param instance containing the method
     * @param arg of the method
     * @return whatever the method returns
     */
    public static @Nullable Object invokeMethodOneArgument(Method method, Object instance, Object arg) {
        return invokeMethod(method, instance, arg);
    }

    /**
     * Executes a method with exactly one argument on the given target
     *
     * @param methodName of the method to execute
     * @param instance containing the method
     * @param arg of the method
     * @return whatever the method returns
     */
    public static @Nullable Object invokeMethodOneArgument(String methodName, Object instance, Object arg) {
        return invokeMethod(methodName, instance, arg);
    }

    /**
     * Executes a method with multiple arguments on the given target
     *
     * @param method to execute
     * @param instance containing the method
     * @param args arguments of the method
     * @return whatever the method returns
     */
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

    /**
     * Returns the "Generic" class of a given field. E.g. String for a List<String>
     *
     * @param fieldName to get the generic from
     * @param clazz containing the field
     * @return the found generic
     */
    public static Optional<Class<?>> getGenericClass(String fieldName, Class<?> clazz) {
        return getDeclaredField(fieldName, clazz)
            .flatMap(ReflectionUtil::getGenericClass);
    }

    /**
     * Returns the "Generic" class of a given field. E.g. String for a List<String>
     *
     * @param field to get the generic from
     * @return the found generic
     */
    public static Optional<Class<?>> getGenericClass(Field field) {
        List<Class<?>> classes = getGenericClasses(field);

        if (classes.size() != 1) {
            return Optional.empty();
        }

        return Optional.of(classes.get(0));
    }

    /**
     * Returns the "Generic" classes of a given field. E.g. String and Integer for a Map<String, Integer>
     *
     * @param field to get the generic from
     * @return the found generics
     */
    public static List<Class<?>> getGenericClasses(Field field) {
        Type[] genericTypes = getGenericTypes(field);
        List<Class<?>> classes = new ArrayList<>();

        for (Type typeArgument : genericTypes) {
            classes.add((Class<?>) typeArgument);
        }

        return classes;
    }

    /**
     * Returns the {@link Type} of a "Generic" in a given field
     *
     * @param field to read the type of the generic from
     * @return found generic
     */
    public static Type[] getGenericTypes(Field field) {
        Type fieldGenericType = field.getGenericType();
        return getGenericTypes(fieldGenericType);
    }

    /**
     * Returns the {@link Type}s of multiple "Generics" from a given {@link Type} itself.
     *
     * @param type to read the type of the generic from
     * @return found generic
     */
    public static Type[] getGenericTypes(Type type) {
        if (!(type instanceof ParameterizedType)) {
            return NO_TYPES;
        }

        return getGenericTypes((ParameterizedType) type);
    }

    /**
     * Returns the {@link Type}s of multiple "Generics" from a given {@link ParameterizedType} itself.
     *
     * @param parameterizedType to read the type of the generic from
     * @return found generic
     */
    public static Type[] getGenericTypes(ParameterizedType parameterizedType) {
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        if (typeArguments.length == 0) {
            return NO_TYPES;
        }

        return typeArguments;
    }

    /**
     * @return whether the given class has a constructor without any arguments
     */
    public static boolean hasNoArgsConstructor(Class<?> clazz) {
        return Stream.of(clazz.getConstructors())
            .anyMatch((c) -> c.getParameterCount() == 0);
    }

    private static void handleReflectionException(Exception ex) {
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

    private static void handleInvocationTargetException(InvocationTargetException ex) {
        rethrowRuntimeException(ex.getTargetException());
    }

    private static void rethrowRuntimeException(Throwable ex) {
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        }
        if (ex instanceof Error) {
            throw (Error) ex;
        }
        throw new UndeclaredThrowableException(ex);
    }
}
