package com.github.argon.sos.moreoptions.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Lists {
    public static <E> List<E> of() {
        return Collections.emptyList();
    }

    public static <E> List<E> of(E e1) {
        return Collections.singletonList(e1);
    }

    public static <E> List<E> of(E e1, E e2) {
        return Collections.unmodifiableList(Arrays.asList(e1, e2));
    }

    public static <E> List<E> of(E e1, E e2, E e3) {
        return Collections.unmodifiableList(Arrays.asList(e1, e2, e3));
    }

    public static <E> List<E> of(E e1, E e2, E e3, E e4) {
        return Collections.unmodifiableList(Arrays.asList(e1, e2, e3, e4));
    }

    public static <E> List<E> of(E e1, E e2, E e3, E e4, E e5) {
        return Collections.unmodifiableList(Arrays.asList(e1, e2, e3, e4, e5));
    }

    public static <E> List<E> of(E e1, E e2, E e3, E e4, E e5, E e6) {
        return Collections.unmodifiableList(Arrays.asList(e1, e2, e3, e4, e5, e6));
    }

    public static <E> List<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7) {
        return Collections.unmodifiableList(Arrays.asList(e1, e2, e3, e4, e5, e6, e7));
    }

    public static <E> List<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8) {
        return Collections.unmodifiableList(Arrays.asList(e1, e2, e3, e4, e5, e6, e7, e8));
    }

    public static <E> List<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9) {
        return Collections.unmodifiableList(Arrays.asList(e1, e2, e3, e4, e5, e6, e7, e8, e9));
    }

    public static <E> List<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9, E e10) {
         return Collections.unmodifiableList(Arrays.asList(e1, e2, e3, e4, e5, e6, e7, e8, e9, e10));
    }

     @SafeVarargs
    @SuppressWarnings("varargs")
    public static <E> List<E> of(E... elements) {
        switch (elements.length) { // implicit null check of elements
            case 0:
                return Lists.of();
            case 1:
                return Lists.of(elements[0]);
            case 2:
                return Lists.of(elements[0], elements[1]);
            default:
                return listFromArray(elements);
        }
    }

    private static <E> List<E> listFromArray(E[] elements) {
        return Arrays.asList(elements);
    }
}
