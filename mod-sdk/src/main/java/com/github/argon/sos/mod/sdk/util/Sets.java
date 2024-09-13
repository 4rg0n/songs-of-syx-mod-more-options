package com.github.argon.sos.mod.sdk.util;

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
        return Collections.unmodifiableSet(Modifiable.of(
            e1,
            e2
        ));
    }

    public static <E> Set<E> of(E e1, E e2, E e3) {
        return Collections.unmodifiableSet(Modifiable.of(
            e1,
            e2,
            e3
        ));
    }

    public static <E> Set<E> of(E e1, E e2, E e3, E e4) {
        return Collections.unmodifiableSet(Modifiable.of(
            e1,
            e2,
            e3,
            e4
        ));
    }

    public static <E> Set<E> of(E e1, E e2, E e3, E e4, E e5) {
        return Collections.unmodifiableSet(Modifiable.of(
            e1,
            e2,
            e3,
            e4,
            e5
        ));
    }

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
    
    public static class Modifiable {
        public static <E> Set<E> of() {
            return new HashSet<>();
        }

        public static <E> Set<E> of(E e1) {
            HashSet<E> set = new HashSet<>();
            set.add(e1);
            
            return set;
        }

        public static <E> Set<E> of(E e1, E e2) {
            HashSet<E> set = new HashSet<>();
            set.add(e1);
            set.add(e2);

            return set;
        }

        public static <E> Set<E> of(E e1, E e2, E e3) {
            HashSet<E> set = new HashSet<>();
            set.add(e1);
            set.add(e2);
            set.add(e3);

            return set;
        }

        public static <E> Set<E> of(E e1, E e2, E e3, E e4) {
            HashSet<E> set = new HashSet<>();
            set.add(e1);
            set.add(e2);
            set.add(e3);
            set.add(e4);

            return set;
        }

        public static <E> Set<E> of(E e1, E e2, E e3, E e4, E e5) {
            HashSet<E> set = new HashSet<>();
            set.add(e1);
            set.add(e2);
            set.add(e3);
            set.add(e4);
            set.add(e5);

            return set;
        }

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

        @SafeVarargs
        @SuppressWarnings("varargs")
        public static <E> Set<E> of(E... elements) {
            switch (elements.length) { // implicit null check of elements
                case 0:
                    return Sets.Modifiable.of();
                case 1:
                    return Sets.Modifiable.of(elements[0]);
                case 2:
                    return Sets.Modifiable.of(elements[0], elements[1]);
                default:
                    return setFromArray(elements);
            }
        }
    }
}
