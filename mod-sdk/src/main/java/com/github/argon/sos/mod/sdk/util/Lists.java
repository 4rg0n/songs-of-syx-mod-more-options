package com.github.argon.sos.mod.sdk.util;

import lombok.experimental.UtilityClass;
import snake2d.util.sets.LIST;

import java.util.*;

/**
 * For creating simple unmodifiable lists with just one line of code:
 * <pre>
 *     Lists.of("", "", "");
 * </pre>
 */
@UtilityClass
public class Lists {
    /**
     * Creates an unmodifiable empty {@link List}.
     * 
     * @return unmodifiable empty list
     * @param <E> type of the entries in the list
     */
    public static <E> List<E> of() {
        return Collections.emptyList();
    }

    /**
     * Creates an unmodifiable {@link List} from a {@link Collection}.
     *
     * @param entries first entry
     * @return unmodifiable list
     * @param <E> type of the entries in the list
     */
    public static <E> List<E> ofList(Collection<E> entries) {
        return Collections.unmodifiableList(Lists.Modifiable.ofList(
            entries
        ));
    }

    /**
     * Creates an unmodifiable {@link List} with one entry.
     *
     * @param e1 first entry
     * @return unmodifiable list
     * @param <E> type of the entries in the list
     */
    public static <E> List<E> of(E e1) {
        return Collections.singletonList(e1);
    }

    /**
     * Creates an unmodifiable {@link List} with two entries.
     *
     * @param e1 first entry
     * @param e2 second entry
     * @return unmodifiable list
     * @param <E> type of the entries in the list
     */
    public static <E> List<E> of(E e1, E e2) {
        return Collections.unmodifiableList(Lists.Modifiable.of(
            e1,
            e2
        ));
    }

    /**
     * Creates an unmodifiable {@link List} with three entries.
     *
     * @param e1 first entry
     * @param e2 second entry
     * @param e3 third entry
     * @return unmodifiable list
     * @param <E> type of the entries in the list
     */
    public static <E> List<E> of(E e1, E e2, E e3) {
        return Collections.unmodifiableList(Lists.Modifiable.of(
            e1,
            e2,
            e3
        ));
    }

    /**
     * Creates an unmodifiable {@link List} with four entries.
     *
     * @param e1 first entry
     * @param e2 second entry
     * @param e3 third entry
     * @param e4 fourth entry
     * @return unmodifiable list
     * @param <E> type of the entries in the list
     */
    public static <E> List<E> of(E e1, E e2, E e3, E e4) {
        return Collections.unmodifiableList(Lists.Modifiable.of(
            e1,
            e2,
            e3,
            e4
        ));
    }

    /**
     * Creates an unmodifiable {@link List} with five entries.
     *
     * @param e1 first entry
     * @param e2 second entry
     * @param e3 third entry
     * @param e4 fourth entry
     * @param e5 fifth entry
     * @return unmodifiable list
     * @param <E> type of the entries in the list
     */
    public static <E> List<E> of(E e1, E e2, E e3, E e4, E e5) {
        return Collections.unmodifiableList(Lists.Modifiable.of(
            e1,
            e2,
            e3,
            e4,
            e5
        ));
    }

    /**
     * Creates an unmodifiable {@link List} with six entries.
     *
     * @param e1 first entry
     * @param e2 second entry
     * @param e3 third entry
     * @param e4 fourth entry
     * @param e5 fifth entry
     * @param e6 sixth entry
     * @return unmodifiable list
     * @param <E> type of the entries in the list
     */
    public static <E> List<E> of(E e1, E e2, E e3, E e4, E e5, E e6) {
        return Collections.unmodifiableList(Lists.Modifiable.of(
            e1,
            e2,
            e3,
            e4,
            e5,
            e6
        ));
    }

    /**
     * Creates an unmodifiable {@link List} with seven entries.
     *
     * @param e1 first entry
     * @param e2 second entry
     * @param e3 third entry
     * @param e4 fourth entry
     * @param e5 fifth entry
     * @param e6 sixth entry
     * @param e7 seventh entry
     * @return unmodifiable list
     * @param <E> type of the entries in the list
     */
    public static <E> List<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7) {
        return Collections.unmodifiableList(Lists.Modifiable.of(
            e1,
            e2,
            e3,
            e4,
            e5,
            e6,
            e7
        ));
    }

    /**
     * Creates an unmodifiable {@link List} with eight entries.
     *
     * @param e1 first entry
     * @param e2 second entry
     * @param e3 third entry
     * @param e4 fourth entry
     * @param e5 fifth entry
     * @param e6 sixth entry
     * @param e7 seventh entry
     * @param e8 eighth entry
     * @return unmodifiable list
     * @param <E> type of the entries in the list
     */
    public static <E> List<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8) {
        return Collections.unmodifiableList(Lists.Modifiable.of(
            e1,
            e2,
            e3,
            e4,
            e5,
            e6,
            e7,
            e8
        ));
    }

    /**
     * Creates an unmodifiable {@link List} with nine entries.
     *
     * @param e1 first entry
     * @param e2 second entry
     * @param e3 third entry
     * @param e4 fourth entry
     * @param e5 fifth entry
     * @param e6 sixth entry
     * @param e7 seventh entry
     * @param e8 eighth entry
     * @param e9 ninth entry
     * @return unmodifiable list
     * @param <E> type of the entries in the list
     */
    public static <E> List<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9) {
        return Collections.unmodifiableList(Lists.Modifiable.of(
            e1,
            e2,
            e3,
            e4,
            e5,
            e6,
            e7,
            e8,
            e9
        ));
    }

    /**
     * Creates an unmodifiable {@link List} with ten entries.
     *
     * @param e1 first entry
     * @param e2 second entry
     * @param e3 third entry
     * @param e4 fourth entry
     * @param e5 fifth entry
     * @param e6 sixth entry
     * @param e7 seventh entry
     * @param e8 eighth entry
     * @param e9 ninth entry
     * @param e10 tenth entry
     * @return unmodifiable list
     * @param <E> type of the entries in the list
     */
    public static <E> List<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9, E e10) {
        return Collections.unmodifiableList(Lists.Modifiable.of(
            e1,
            e2,
            e3,
            e4,
            e5,
            e6,
            e7,
            e8,
            e9,
            e10
        ));
    }

    /**
     * Creates an unmodifiable {@link List} with N entries.
     *
     * @param entries to add
     * @return unmodifiable list
     * @param <E> type of the entries in the list
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <E> List<E> of(E... entries) {
         return switch (entries.length) { // implicit null check of entries
             case 0 -> Lists.of();
             case 1 -> Lists.of(entries[0]);
             case 2 -> Lists.of(entries[0], entries[1]);
             default -> listFromArray(entries);
         };
    }

    /**
     * For transforming a Game {@link snake2d.util.sets.ArrayList} to a Java {@link ArrayList}.
     *
     * @param gameLIST the game {@link LIST} to transform
     * @return transformed Java {@link List}
     * @param <E> type of the entries in the list
     */
    public static <E> List<E> fromGameLIST(LIST<E> gameLIST) {
        List<E> list = new ArrayList<>();

        for (int i = 0; i < gameLIST.size(); i++) {
            E element = gameLIST.get(i);
            list.add(element);
        }

        return list;
    }

    /**
     * For transforming a Java {@link List} to a Game {@link snake2d.util.sets.ArrayList}.
     *
     * @param list the Java {@link List} to transform
     * @return transformed Game {@link LIST}
     * @param <E> type of the entries in the list
     */
    public static <E> LIST<E> toGameLIST(List<E> list) {
        return new snake2d.util.sets.ArrayList<>(list);
    }

    /**
     * For transforming an array to an unmodifiable {@link List}.
     *
     * @param entries array to transform
     * @return transformed Java {@link List}
     * @param <E> type of the entries in the list
     */
    public static <E> List<E> listFromArray(E[] entries) {
        return Collections.unmodifiableList(Modifiable.listFromArray(entries));
    }

    /**
     * Compares a {@link List<String>} with another one.
     *
     * @param list list to compare
     * @param toCompare list to compare against
     * @return whether both lists contain the same entries in the same order
     */
    public static boolean compare(List<String> list, List<String> toCompare) {
        if (list.size() != toCompare.size()) {
            return false;
        }

        return new HashSet<>(list).containsAll(toCompare);
    }

    /**
     *  Compares a {@link List<String>} with a {@link Set<String>}.
     *
     * @param list list to compare
     * @param toCompare set to compare against
     * @return whether entries from the list are all present in the set
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
     * @return new {@link List} with values or en empty list if the range is off
     * @param <E> type of the entries in the list
     */
    public static <E> List<E> slice(List<E> list, int from, int to) {
        List<E> copy = new ArrayList<>();
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

    /**
     * For creating simple modifiable lists with just one line of code:
     * <pre>
     *     Lists.Modifiable.of("", "", "");
     * </pre>
     */
    public static class Modifiable {

        /**
         * Creates a new {@link Modifiable}.
         */
        public Modifiable() {
        }

        /**
         * Creates a modifiable empty {@link List}.
         *
         * @return modifiable empty list
         * @param <E> type of the entries in the list
         */
        public static <E> List<E> of() {
            return new ArrayList<>();
        }

        /**
         * Creates a modifiable {@link List} from a {@link Collection}.
         *
         * @param entries to transform
         * @return empty modifiable list
         * @param <E> type of the entries in the list
         */
        public static <E> List<E> ofList(Collection<E> entries) {
            return new ArrayList<>(entries);
        }

        /**
         * Creates a modifiable {@link List} with one entry.
         *
         * @param e1 first entry
         * @return modifiable list
         * @param <E> type of the entries in the list
         */
        public static <E> List<E> of(E e1) {
            ArrayList<E> list = new ArrayList<>();
            list.add(e1);

            return list;
        }

        /**
         * Creates a modifiable {@link List} with two entries.
         *
         * @param e1 first entry
         * @param e2 second entry
         * @return modifiable list
         * @param <E> type of the entries in the list
         */
        public static <E> List<E> of(E e1, E e2) {
            ArrayList<E> list = new ArrayList<>();
            list.add(e1);
            list.add(e2);

            return list;
        }

        /**
         * Creates a modifiable {@link List} with three entries.
         *
         * @param e1 first entry
         * @param e2 second entry
         * @param e3 third entry
         * @return modifiable list
         * @param <E> type of the entries in the list
         */
        public static <E> List<E> of(E e1, E e2, E e3) {
            ArrayList<E> list = new ArrayList<>();
            list.add(e1);
            list.add(e2);
            list.add(e3);

            return list;
        }

        /**
         * Creates a modifiable {@link List} with four entries.
         *
         * @param e1 first entry
         * @param e2 second entry
         * @param e3 third entry
         * @param e4 fourth entry
         * @return modifiable list
         * @param <E> type of the entries in the list
         */
        public static <E> List<E> of(E e1, E e2, E e3, E e4) {
            ArrayList<E> list = new ArrayList<>();
            list.add(e1);
            list.add(e2);
            list.add(e3);
            list.add(e4);

            return list;
        }

        /**
         * Creates a modifiable {@link List} with five entries.
         *
         * @param e1 first entry
         * @param e2 second entry
         * @param e3 third entry
         * @param e4 fourth entry
         * @param e5 fifth entry
         * @return modifiable list
         * @param <E> type of the entries in the list
         */
        public static <E> List<E> of(E e1, E e2, E e3, E e4, E e5) {
            ArrayList<E> list = new ArrayList<>();
            list.add(e1);
            list.add(e2);
            list.add(e3);
            list.add(e4);
            list.add(e5);

            return list;
        }

        /**
         * Creates a modifiable {@link List} with six entries.
         *
         * @param e1 first entry
         * @param e2 second entry
         * @param e3 third entry
         * @param e4 fourth entry
         * @param e5 fifth entry
         * @param e6 sixth entry
         * @return modifiable list
         * @param <E> type of the entries in the list
         */
        public static <E> List<E> of(E e1, E e2, E e3, E e4, E e5, E e6) {
            ArrayList<E> list = new ArrayList<>();
            list.add(e1);
            list.add(e2);
            list.add(e3);
            list.add(e4);
            list.add(e5);
            list.add(e6);

            return list;
        }

        /**
         * Creates a modifiable {@link List} with seven entries.
         *
         * @param e1 first entry
         * @param e2 second entry
         * @param e3 third entry
         * @param e4 fourth entry
         * @param e5 fifth entry
         * @param e6 sixth entry
         * @param e7 seventh entry
         * @return modifiable list
         * @param <E> type of the entries in the list
         */
        public static <E> List<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7) {
            ArrayList<E> list = new ArrayList<>();
            list.add(e1);
            list.add(e2);
            list.add(e3);
            list.add(e4);
            list.add(e5);
            list.add(e6);
            list.add(e7);

            return list;
        }

        /**
         * Creates a modifiable {@link List} with eight entries.
         *
         * @param e1 first entry
         * @param e2 second entry
         * @param e3 third entry
         * @param e4 fourth entry
         * @param e5 fifth entry
         * @param e6 sixth entry
         * @param e7 seventh entry
         * @param e8 eighth entry
         * @return modifiable list
         * @param <E> type of the entries in the list
         */
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

            return list;
        }

        /**
         * Creates a modifiable {@link List} with nine entries.
         *
         * @param e1 first entry
         * @param e2 second entry
         * @param e3 third entry
         * @param e4 fourth entry
         * @param e5 fifth entry
         * @param e6 sixth entry
         * @param e7 seventh entry
         * @param e8 eighth entry
         * @param e9 ninth entry
         * @return modifiable list
         * @param <E> type of the entries in the list
         */
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

            return list;
        }

        /**
         * Creates a modifiable {@link List} with ten entries.
         *
         * @param e1 first entry
         * @param e2 second entry
         * @param e3 third entry
         * @param e4 fourth entry
         * @param e5 fifth entry
         * @param e6 sixth entry
         * @param e7 seventh entry
         * @param e8 eighth entry
         * @param e9 ninth entry
         * @param e10 tenth entry
         * @return modifiable list
         * @param <E> type of the entries in the list
         */
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

            return list;
        }

        /**
         * For transforming an array to a modifiable {@link List}.
         *
         * @param entries array to transform
         * @return transformed Java {@link List}
         * @param <E> type of the entries in the list
         */
        public static <E> List<E> listFromArray(E[] entries) {
            return Arrays.asList(entries);
        }

        /**
         * Creates modifiable {@link List} with N entries.
         *
         * @param entries to add
         * @return modifiable list
         * @param <E> type of the entries in the list
         */
        @SafeVarargs
        @SuppressWarnings("varargs")
        public static <E> List<E> of(E... entries) {
            return switch (entries.length) { // implicit null check of entries
                case 0 -> Modifiable.of();
                case 1 -> Modifiable.of(entries[0]);
                case 2 -> Modifiable.of(entries[0], entries[1]);
                default -> Modifiable.listFromArray(entries);
            };
        }
    }
}
