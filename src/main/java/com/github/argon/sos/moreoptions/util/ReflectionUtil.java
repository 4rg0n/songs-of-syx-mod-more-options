package com.github.argon.sos.moreoptions.util;


import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * For accessing game classes and properties, which are normally not public available
 */
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
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);

        field.set(instance, newValue);
    }

    public static <T> Optional<T> getDeclaredField(String fieldName, Object instance)  {
        try {
            Field field = instance.getClass().getDeclaredField(fieldName);
            return getDeclaredField(field, instance);
        } catch (NoSuchFieldException e) {
            log.error("Field %s does not exist", fieldName, e);
            return Optional.empty();
        }
    }

    public static <T> Optional<T> getDeclaredField(Field field, Object instance) {
        field.setAccessible(true);

        try {
            //noinspection unchecked
            return Optional.of((T) field.get(instance));
        } catch (Exception e) {
            log.error("Can not access field %s in %s.",
                    field.getName(), instance.getClass().getSimpleName(), e);
            return Optional.empty();
        }
    }

    public static Optional<Field> getDeclaredField(String fieldName, Class<?> clazz) {
        try {
            return Optional.of(clazz.getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            log.warn("Can not access field %s in %s.", fieldName, clazz.getSimpleName(), e);
            return Optional.empty();
        }
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


    public static Object invokeMethod(Method method, Object target) {
        return invokeMethod(method, target, EMPTY_OBJECT_ARRAY);
    }

    public static Object invokeMethod(String methodName, Class<?> target, Object instance,  Object... args) {
        Method method = null;
        Class<?>[] paramTypes = Arrays.stream(args).map(Object::getClass).toArray(Class[]::new);

        try {
            method = target.getDeclaredMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            handleReflectionException(e);
        }

        return invokeMethod(method, instance, args);
    }

    public static Object invokeMethodOneArgument(Method method, Object instance, Object arg) {
        return invokeMethod(method, instance, arg);
    }

    public static Object invokeMethod(Method method, Object instance, Object... args) {
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

        if (classes.size() == 0) {
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
