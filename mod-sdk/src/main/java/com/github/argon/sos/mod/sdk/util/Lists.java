package com.github.argon.sos.mod.sdk.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import snake2d.util.sets.LIST;

import java.util.*;

/**
 * For creating simple lists with just one line of code:
 * <pre>
 *     Lists.of("", "", "");
 * </pre>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Lists {
    public static <E> List<E> of() {
        return Collections.emptyList();
    }

    public static <E> List<E> of(Collection<E> e1) {
        return new ArrayList<>(e1);
    }

    public static <E> List<E> ofSingle(E e1) {
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
                return Lists.ofSingle(elements[0]);
            case 2:
                return Lists.of(elements[0], elements[1]);
            default:
                return listFromArray(elements);
        }
    }

    /**
     * For transforming a Game {@link snake2d.util.sets.ArrayList} to a Java {@link ArrayList}
     */
    public static <T> List<T> fromGameLIST(LIST<T> gameLIST) {
        List<T> list = new ArrayList<>();

        for (int i = 0; i < gameLIST.size(); i++) {
            T element = gameLIST.get(i);
            list.add(element);
        }

        return list;
    }

    /**
     * For transforming a Java {@link List} to a Game {@link snake2d.util.sets.ArrayList}
     */
    public static <T> LIST<T> toGameLIST(List<T> list) {
        return new snake2d.util.sets.ArrayList<T>(list);
    }

    /**
     * For transforming an array to a {@link List}
     */
    private static <E> List<E> listFromArray(E[] elements) {
        return Arrays.asList(elements);
    }

    /**
     * @return whether both lists contain the same elements in the same order
     */
    public static boolean compare(List<String> list, List<String> toCompare) {
        if (list.size() != toCompare.size()) {
            return false;
        }

        return new HashSet<>(list).containsAll(toCompare);
    }

    /**
     * @return whether elements from the list are all present in the set
     */
    public static boolean compare(List<String> list, Set<String> toCompare) {
        if (list.size() != toCompare.size()) {
            return false;
        }

        return toCompare.containsAll(list);
    }

    /**
     * Copies content from a given index, to a given index into a new list.
     *
     * @param list to copy values from
     * @param from start index
     * @param to end index
     * @return new list with values or en empty list if the range is off
     */
    public static <T> List<T> slice(List<T> list, int from, int to) {
        List<T> copy = new ArrayList<>();
        int max = list.size() - 1;

        if (from > max) {
            return Lists.of();
        }

        if (to > max) {
            to = max;
        }

        for (int i = from; i <= to; i++) {
            copy.add(list.get(i));
        }

        return copy;
    }
}
