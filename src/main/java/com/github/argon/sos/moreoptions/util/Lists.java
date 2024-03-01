package com.github.argon.sos.moreoptions.util;

import snake2d.util.sets.LIST;

import java.util.*;

public class Lists {
    public static <E> List<E> of() {
        return Collections.emptyList();
    }

    public static <E> List<E> of(E e1) {
        return Collections.singletonList(e1);
    }

    public static <E> List<E> of(E e1, E e2) {
        ArrayList<E> list = new ArrayList<>();
        list.add(e1);
        list.add(e2);

        return Collections.unmodifiableList(list);
    }

    public static <E> List<E> of(E e1, E e2, E e3) {
        ArrayList<E> list = new ArrayList<>();
        list.add(e1);
        list.add(e2);
        list.add(e3);

        return Collections.unmodifiableList(list);
    }

    public static <E> List<E> of(E e1, E e2, E e3, E e4) {
        ArrayList<E> list = new ArrayList<>();
        list.add(e1);
        list.add(e2);
        list.add(e3);
        list.add(e4);

        return Collections.unmodifiableList(list);
    }

    public static <E> List<E> of(E e1, E e2, E e3, E e4, E e5) {
        ArrayList<E> list = new ArrayList<>();
        list.add(e1);
        list.add(e2);
        list.add(e3);
        list.add(e4);
        list.add(e5);

        return Collections.unmodifiableList(list);
    }

    public static <E> List<E> of(E e1, E e2, E e3, E e4, E e5, E e6) {
        ArrayList<E> list = new ArrayList<>();
        list.add(e1);
        list.add(e2);
        list.add(e3);
        list.add(e4);
        list.add(e5);
        list.add(e6);

        return Collections.unmodifiableList(list);
    }

    public static <E> List<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7) {
        ArrayList<E> list = new ArrayList<>();
        list.add(e1);
        list.add(e2);
        list.add(e3);
        list.add(e4);
        list.add(e5);
        list.add(e6);
        list.add(e7);

        return Collections.unmodifiableList(list);
    }

    public static <E> List<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8) {
        ArrayList<E> list = new ArrayList<>();
        list.add(e1);
        list.add(e2);
        list.add(e3);
        list.add(e4);
        list.add(e5);
        list.add(e6);
        list.add(e7);
        list.add(e8);

        return Collections.unmodifiableList(list);
    }

    public static <E> List<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9) {
        ArrayList<E> list = new ArrayList<>();
        list.add(e1);
        list.add(e2);
        list.add(e3);
        list.add(e4);
        list.add(e5);
        list.add(e6);
        list.add(e7);
        list.add(e8);
        list.add(e9);

        return Collections.unmodifiableList(list);
    }

    public static <E> List<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9, E e10) {
        ArrayList<E> list = new ArrayList<>();
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

        return Collections.unmodifiableList(list);
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

    public static <T> List<T> listFromLIST(LIST<T> gameLIST) {
        List<T> races = new ArrayList<>();

        for (T race : gameLIST) {
            races.add(race);
        }

        return races;
    }

    public static <T> LIST<T> toGameLIST(List<T> gameLIST) {
        return new snake2d.util.sets.ArrayList<T>(gameLIST);
    }

    private static <E> List<E> listFromArray(E[] elements) {
        return Arrays.asList(elements);
    }

    public static boolean compare(List<String> list, List<String> toCompare) {
        if (list.size() != toCompare.size()) {
            return false;
        }

        return new HashSet<>(list).containsAll(toCompare);
    }

    public static boolean compare(List<String> list, Set<String> toCompare) {
        if (list.size() != toCompare.size()) {
            return false;
        }

        return toCompare.containsAll(list);
    }
}
