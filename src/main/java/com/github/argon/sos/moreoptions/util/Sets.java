package com.github.argon.sos.moreoptions.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Sets {
    public static <E> Set<E> of() {
        return Collections.emptySet();
    }

    public static <E> Set<E> of(E e1) {
        return Collections.singleton(e1);
    }

    public static <E> Set<E> of(E e1, E e2) {
        HashSet<E> list = new HashSet<>();
        list.add(e1);
        list.add(e2);

        return Collections.unmodifiableSet(list);
    }

    public static <E> Set<E> of(E e1, E e2, E e3) {
        HashSet<E> list = new HashSet<>();
        list.add(e1);
        list.add(e2);
        list.add(e3);

        return Collections.unmodifiableSet(list);
    }

    public static <E> Set<E> of(E e1, E e2, E e3, E e4) {
        HashSet<E> list = new HashSet<>();
        list.add(e1);
        list.add(e2);
        list.add(e3);
        list.add(e4);

        return Collections.unmodifiableSet(list);
    }

    public static <E> Set<E> of(E e1, E e2, E e3, E e4, E e5) {
        HashSet<E> list = new HashSet<>();
        list.add(e1);
        list.add(e2);
        list.add(e3);
        list.add(e4);
        list.add(e5);

        return Collections.unmodifiableSet(list);
    }

    public static <E> Set<E> of(E e1, E e2, E e3, E e4, E e5, E e6) {
        HashSet<E> list = new HashSet<>();
        list.add(e1);
        list.add(e2);
        list.add(e3);
        list.add(e4);
        list.add(e5);
        list.add(e6);

        return Collections.unmodifiableSet(list);
    }

    public static <E> Set<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7) {
        HashSet<E> list = new HashSet<>();
        list.add(e1);
        list.add(e2);
        list.add(e3);
        list.add(e4);
        list.add(e5);
        list.add(e6);
        list.add(e7);

        return Collections.unmodifiableSet(list);
    }

    public static <E> Set<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8) {
        HashSet<E> list = new HashSet<>();
        list.add(e1);
        list.add(e2);
        list.add(e3);
        list.add(e4);
        list.add(e5);
        list.add(e6);
        list.add(e7);
        list.add(e8);

        return Collections.unmodifiableSet(list);
    }

    public static <E> Set<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9) {
        HashSet<E> list = new HashSet<>();
        list.add(e1);
        list.add(e2);
        list.add(e3);
        list.add(e4);
        list.add(e5);
        list.add(e6);
        list.add(e7);
        list.add(e8);
        list.add(e9);

        return Collections.unmodifiableSet(list);
    }

    public static <E> Set<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9, E e10) {
        HashSet<E> list = new HashSet<>();
        list.add(e1);
        list.add(e2);
        list.add(e3);
        list.add(e4);
        list.add(e5);
        list.add(e6);
        list.add(e7);
        list.add(e8);
        list.add(e9);
        list.add(e10);

        return Collections.unmodifiableSet(list);
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <E> Set<E> of(E... elements) {
        switch (elements.length) { // implicit null check of elements
            case 0:
                return Sets.of();
            case 1:
                return Sets.of(elements[0]);
            case 2:
                return Sets.of(elements[0], elements[1]);
            default:
                return setFromArray(elements);
        }
    }

    public static <T> SortedSet<T> sort(Set<T> set) {
        return set.stream()
            .sorted()
            .collect(Collectors.toCollection(TreeSet::new));
    }

    private static <E> Set<E> setFromArray(E[] elements) {
        return new HashSet<>(Arrays.asList(elements));
    }
}
