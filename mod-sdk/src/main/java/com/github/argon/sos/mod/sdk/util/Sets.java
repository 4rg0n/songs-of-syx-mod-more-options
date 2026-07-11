package com.github.argon.sos.mod.sdk.util;

import lombok.experimental.UtilityClass;

import java.util.*;
import java.util.stream.Collectors;

/**
 * For creating simple unmodifiable sets with just one line of code:
 * <pre>
 *     Sets.of("", "", "");
 * </pre>
 */
@UtilityClass
public class Sets {
    /**
     * Creates an unmodifiable empty {@link Set}.
     *
     * @return unmodifiable empty set
     * @param <E> type of the entries in the set
     */
    public static <E> Set<E> of() {
        return Collections.emptySet();
    }

    /**
     * Creates an unmodifiable {@link Set} with one entry.
     *
     * @param e1 first entry
     * @return unmodifiable set
     * @param <E> type of the entries in the set
     */
    public static <E> Set<E> of(E e1) {
        return Collections.singleton(e1);
    }

    /**
     * Creates an unmodifiable {@link Set} with two entries.
     *
     * @param e1 first entry
     * @param e2 second entry
     * @return unmodifiable set
     * @param <E> type of the entries in the set
     */
    public static <E> Set<E> of(E e1, E e2) {
        return Collections.unmodifiableSet(Modifiable.of(
            e1,
            e2
        ));
    }

    /**
     * Creates an unmodifiable {@link Set} with three entries.
     *
     * @param e1 first entry
     * @param e2 second entry
     * @param e3 third entry
     * @return unmodifiable set
     * @param <E> type of the entries in the set
     */
    public static <E> Set<E> of(E e1, E e2, E e3) {
        return Collections.unmodifiableSet(Modifiable.of(
            e1,
            e2,
            e3
        ));
    }

    /**
     * Creates an unmodifiable {@link Set} with four entries.
     *
     * @param e1 first entry
     * @param e2 second entry
     * @param e3 third entry
     * @param e4 fourth entry
     * @return unmodifiable set
     * @param <E> type of the entries in the set
     */
    public static <E> Set<E> of(E e1, E e2, E e3, E e4) {
        return Collections.unmodifiableSet(Modifiable.of(
            e1,
            e2,
            e3,
            e4
        ));
    }

    /**
     * Creates an unmodifiable {@link Set} with five entries.
     *
     * @param e1 first entry
     * @param e2 second entry
     * @param e3 third entry
     * @param e4 fourth entry
     * @param e5 fifth entry
     * @return unmodifiable set
     * @param <E> type of the entries in the set
     */
    public static <E> Set<E> of(E e1, E e2, E e3, E e4, E e5) {
        return Collections.unmodifiableSet(Modifiable.of(
            e1,
            e2,
            e3,
            e4,
            e5
        ));
    }

    /**
     * Creates an unmodifiable {@link Set} with six entries.
     *
     * @param e1 first entry
     * @param e2 second entry
     * @param e3 third entry
     * @param e4 fourth entry
     * @param e5 fifth entry
     * @param e6 sixth entry
     * @return unmodifiable set
     * @param <E> type of the entries in the set
     */
    public static <E> Set<E> of(E e1, E e2, E e3, E e4, E e5, E e6) {
        return Collections.unmodifiableSet(Modifiable.of(
            e1,
            e2,
            e3,
            e4,
            e5,
            e6
        ));
    }

    /**
     * Creates an unmodifiable {@link Set} with seven entries.
     *
     * @param e1 first entry
     * @param e2 second entry
     * @param e3 third entry
     * @param e4 fourth entry
     * @param e5 fifth entry
     * @param e6 sixth entry
     * @param e7 seventh entry
     * @return unmodifiable set
     * @param <E> type of the entries in the set
     */
    public static <E> Set<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7) {
        return Collections.unmodifiableSet(Modifiable.of(
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
     * Creates an unmodifiable {@link Set} with eight entries.
     *
     * @param e1 first entry
     * @param e2 second entry
     * @param e3 third entry
     * @param e4 fourth entry
     * @param e5 fifth entry
     * @param e6 sixth entry
     * @param e7 seventh entry
     * @param e8 eighth entry
     * @return unmodifiable set
     * @param <E> type of the entries in the set
     */
    public static <E> Set<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8) {
        return Collections.unmodifiableSet(Modifiable.of(
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
     * Creates an unmodifiable {@link Set} with nine entries.
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
     * @return unmodifiable set
     * @param <E> type of the entries in the set
     */
    public static <E> Set<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9) {
        return Collections.unmodifiableSet(Modifiable.of(
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
     * Creates an unmodifiable {@link Set} with ten entries.
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
     * @return unmodifiable set
     * @param <E> type of the entries in the set
     */
    public static <E> Set<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9, E e10) {
        return Collections.unmodifiableSet(Modifiable.of(
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
     * Creates an unmodifiable {@link Set} with N entries.
     *
     * @param entries to add
     * @return unmodifiable set
     * @param <E> type of the entries in the set
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <E> Set<E> of(E... entries) {
        return switch (entries.length) { // implicit null check of entries
            case 0 -> Sets.of();
            case 1 -> Sets.of(entries[0]);
            case 2 -> Sets.of(entries[0], entries[1]);
            default -> setFromArray(entries);
        };
    }

    /**
     * Creates a {@link SortedSet} from a {@link Set}.
     *
     * @param set to create sorted from
     * @return sorted set
     * @param <E> type of the entries in the set
     */
    public static <E> SortedSet<E> sort(Set<E> set) {
        return set.stream()
            .sorted()
            .collect(Collectors.toCollection(TreeSet::new));
    }

    /**
     * For transforming an array to an unmodifiable {@link Set}.
     *
     * @param entries array to transform
     * @return transformed Java {@link Set}
     * @param <E> type of the entries in the set
     */
    public static <E> Set<E> setFromArray(E[] entries) {
        return Collections.unmodifiableSet(Modifiable.setFromArray(entries));
    }

    /**
     * For creating simple modifiable sets with just one line of code:
     * <pre>
     *     Sets.Modifiable.of("", "", "");
     * </pre>
     */
    public static class Modifiable {

        /**
         * Creates a modifiable empty {@link Set}.
         *
         * @return modifiable empty set
         * @param <E> type of the entries in the set
         */
        public static <E> Set<E> of() {
            return new HashSet<>();
        }

        /**
         * Creates a modifiable {@link Set} with one entry.
         *
         * @param e1 first entry
         * @return modifiable set
         * @param <E> type of the entries in the set
         */
        public static <E> Set<E> of(E e1) {
            HashSet<E> set = new HashSet<>();
            set.add(e1);
            
            return set;
        }

        /**
         * Creates a modifiable {@link Set} with two entries.
         *
         * @param e1 first entry
         * @param e2 second entry
         * @return modifiable set
         * @param <E> type of the entries in the set
         */
        public static <E> Set<E> of(E e1, E e2) {
            HashSet<E> set = new HashSet<>();
            set.add(e1);
            set.add(e2);

            return set;
        }

        /**
         * Creates a modifiable {@link Set} with three entries.
         *
         * @param e1 first entry
         * @param e2 second entry
         * @param e3 third entry
         * @return modifiable set
         * @param <E> type of the entries in the set
         */
        public static <E> Set<E> of(E e1, E e2, E e3) {
            HashSet<E> set = new HashSet<>();
            set.add(e1);
            set.add(e2);
            set.add(e3);

            return set;
        }

        /**
         * Creates a modifiable {@link Set} with four entries.
         *
         * @param e1 first entry
         * @param e2 second entry
         * @param e3 third entry
         * @param e4 fourth entry
         * @return modifiable set
         * @param <E> type of the entries in the set
         */
        public static <E> Set<E> of(E e1, E e2, E e3, E e4) {
            HashSet<E> set = new HashSet<>();
            set.add(e1);
            set.add(e2);
            set.add(e3);
            set.add(e4);

            return set;
        }

        /**
         * Creates a modifiable {@link Set} with five entries.
         *
         * @param e1 first entry
         * @param e2 second entry
         * @param e3 third entry
         * @param e4 fourth entry
         * @param e5 fifth entry
         * @return modifiable set
         * @param <E> type of the entries in the set
         */
        public static <E> Set<E> of(E e1, E e2, E e3, E e4, E e5) {
            HashSet<E> set = new HashSet<>();
            set.add(e1);
            set.add(e2);
            set.add(e3);
            set.add(e4);
            set.add(e5);

            return set;
        }

        /**
         * Creates a modifiable {@link Set} with six entries.
         *
         * @param e1 first entry
         * @param e2 second entry
         * @param e3 third entry
         * @param e4 fourth entry
         * @param e5 fifth entry
         * @param e6 sixth entry
         * @return modifiable set
         * @param <E> type of the entries in the set
         */
        public static <E> Set<E> of(E e1, E e2, E e3, E e4, E e5, E e6) {
            HashSet<E> set = new HashSet<>();
            set.add(e1);
            set.add(e2);
            set.add(e3);
            set.add(e4);
            set.add(e5);
            set.add(e6);

            return set;
        }

        /**
         * Creates a modifiable {@link Set} with seven entries.
         *
         * @param e1 first entry
         * @param e2 second entry
         * @param e3 third entry
         * @param e4 fourth entry
         * @param e5 fifth entry
         * @param e6 sixth entry
         * @param e7 seventh entry
         * @return modifiable set
         * @param <E> type of the entries in the set
         */
        public static <E> Set<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7) {
            HashSet<E> set = new HashSet<>();
            set.add(e1);
            set.add(e2);
            set.add(e3);
            set.add(e4);
            set.add(e5);
            set.add(e6);
            set.add(e7);

            return set;
        }

        /**
         * Creates a modifiable {@link Set} with eight entries.
         *
         * @param e1 first entry
         * @param e2 second entry
         * @param e3 third entry
         * @param e4 fourth entry
         * @param e5 fifth entry
         * @param e6 sixth entry
         * @param e7 seventh entry
         * @param e8 eighth entry
         * @return modifiable set
         * @param <E> type of the entries in the set
         */
        public static <E> Set<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8) {
            HashSet<E> set = new HashSet<>();
            set.add(e1);
            set.add(e2);
            set.add(e3);
            set.add(e4);
            set.add(e5);
            set.add(e6);
            set.add(e7);
            set.add(e8);

            return set;
        }

        /**
         * Creates a modifiable {@link Set} with nine entries.
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
         * @return modifiable set
         * @param <E> type of the entries in the set
         */
        public static <E> Set<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9) {
            HashSet<E> set = new HashSet<>();
            set.add(e1);
            set.add(e2);
            set.add(e3);
            set.add(e4);
            set.add(e5);
            set.add(e6);
            set.add(e7);
            set.add(e8);
            set.add(e9);

            return set;
        }

        /**
         * Creates a modifiable {@link Set} with ten entries.
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
         * @return modifiable set
         * @param <E> type of the entries in the set
         */
        public static <E> Set<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9, E e10) {
            HashSet<E> set = new HashSet<>();
            set.add(e1);
            set.add(e2);
            set.add(e3);
            set.add(e4);
            set.add(e5);
            set.add(e6);
            set.add(e7);
            set.add(e8);
            set.add(e9);
            set.add(e10);

            return set;
        }

        /**
         * Creates a modifiable {@link Set} with N entries.
         *
         * @param entries to add
         * @return modifiable set
         * @param <E> type of the entries in the set
         */
        @SafeVarargs
        @SuppressWarnings("varargs")
        public static <E> Set<E> of(E... entries) {
            return switch (entries.length) { // implicit null check of entries
                case 0 -> Modifiable.of();
                case 1 -> Modifiable.of(entries[0]);
                case 2 -> Modifiable.of(entries[0], entries[1]);
                default -> setFromArray(entries);
            };
        }

        /**
         * For transforming an array to a modifiable {@link Set}.
         *
         * @param entries array to transform
         * @return transformed Java {@link Set}
         * @param <E> type of the entries in the set
         */
        public static <E> Set<E> setFromArray(E[] entries) {
            return new HashSet<>(Arrays.asList(entries));
        }
    }
}
