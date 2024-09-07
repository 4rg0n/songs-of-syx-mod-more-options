package com.github.argon.sos.mod.sdk.json.mapper;

import com.github.argon.sos.mod.sdk.util.ReflectionUtil;
import com.github.argon.sos.mod.sdk.util.TypeUtil;
import lombok.Getter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Holds type the information of an object such as e.g. List<String>
 * Usage:
 * <pre>{@code
 *     // for simple classes
 *     TypeInfo.get(String.class);
 *
 *     // for types coming from e.g. a java.lang.reflect.Field definition
 *     Type type = field.getGenericType();
 *     TypeInfo.get(type)
 *
 *     // for providing the information manually
 *     new TypeInfo<List<String>>(){};
 * }</pre>
 *
 * @param <T> type information
 */
public class TypeInfo<T> {
    @Getter
    private final Type type;

    private final Class<? super T> rawType;

    private TypeInfo(Type type) {
        this.type = type;
        //noinspection unchecked
        this.rawType = (Class<? super T>) TypeUtil.getRawType(this.type);
    }

    protected TypeInfo() {
        this.type = getOwnTypeArgument();
        //noinspection unchecked
        this.rawType = (Class<? super T>) TypeUtil.getRawType(this.type);
    }

    public Class<? super T> getTypeClass() {
        return rawType;
    }

    public Type[] getGenericTypes() {
        return ReflectionUtil.getGenericTypes(type);
    }

    public static TypeInfo<?> get(Type type) {
        return new TypeInfo<>(type);
    }

    public static <T> TypeInfo<T> get(Class<T> type) {
        return new TypeInfo<>(type);
    }

    /**
     * Used when instantiating via {@code new TypeInfo<List<String>>(){}}to get the tye information from between <...>
     *
     * @return the type information provided via the generic
     */
    private Type getOwnTypeArgument() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof ParameterizedType) {
            ParameterizedType parameterized = (ParameterizedType) superclass;
            if (parameterized.getRawType() == TypeInfo.class) {
                return parameterized.getActualTypeArguments()[0];
            }
        }
        else if (superclass == TypeInfo.class) {
            throw new IllegalStateException(
                "You have to create the TypeInfo with type information e.g.: TypeInfo<List<String>>(){}");
        }

        throw new IllegalStateException(
            "You can not extend from TypeInfo and use it. Getting type information only works in the superclass.");
    }
}
