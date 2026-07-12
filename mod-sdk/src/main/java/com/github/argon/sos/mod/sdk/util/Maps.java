package com.github.argon.sos.mod.sdk.util;

import lombok.experimental.UtilityClass;

import java.util.*;

/**
 * For creating simple unmodifiable maps with just a few lines of code:
 * <pre>
 *     Maps.of(
 *      "key1", 1,
 *      "key2", 2,
 *      "key3", 3);
 * </pre>
 */
@UtilityClass
public class Maps {
    /**
     * Creates an unmodifiable empty {@link Map}.
     *
     * @return unmodifiable empty map
     * @param <K> type of the keys in the map
     * @param <V> type of the values in the map
     */
    public static <K, V> Map<K, V> of() {
        return Collections.emptyMap();
    }

    /**
     * Creates an unmodifiable {@link Map} with one entry.
     *
     * @param key1 first key
     * @param value1 first value
     * @return unmodifiable map
     * @param <K> type of the keys in the map
     * @param <V> type of the values in the map
     */
    public static <K, V> Map<K, V> of(K key1, V value1) {
        return Collections.singletonMap(key1, value1);
    }

    /**
     * Creates an unmodifiable {@link Map} with two entries.
     *
     * @param key1 first key
     * @param value1 first value
     * @param key2 second key
     * @param value2 second value
     * @return unmodifiable map
     * @param <K> type of the keys in the map
     * @param <V> type of the values in the map
     */
    public static <K, V> Map<K, V> of(
        K key1, V value1,
        K key2, V value2
    ) {
        return Collections.unmodifiableMap(Modifiable.of(
            key1, value1,
            key2, value2
        ));
    }

    /**
     * Creates an unmodifiable {@link Map} with three entries.
     *
     * @param key1 first key
     * @param value1 first value
     * @param key2 second key
     * @param value2 second value
     * @param key3 third key
     * @param value3 third value
     * @return unmodifiable map
     * @param <K> type of the keys in the map
     * @param <V> type of the values in the map
     */
    public static <K, V> Map<K, V> of(
        K key1, V value1,
        K key2, V value2,
        K key3, V value3
    ) {
        return Collections.unmodifiableMap(Modifiable.of(
            key1, value1,
            key2, value2,
            key3, value3
        ));
    }

    /**
     * Creates an unmodifiable {@link Map} with four entries.
     *
     * @param key1 first key
     * @param value1 first value
     * @param key2 second key
     * @param value2 second value
     * @param key3 third key
     * @param value3 third value
     * @param key4 fourth key
     * @param value4 fourth value
     * @return unmodifiable map
     * @param <K> type of the keys in the map
     * @param <V> type of the values in the map
     */
    public static <K, V> Map<K, V> of(
        K key1, V value1,
        K key2, V value2,
        K key3, V value3,
        K key4, V value4
    ) {
        return Collections.unmodifiableMap(Modifiable.of(
            key1, value1,
            key2, value2,
            key3, value3,
            key4, value4
        ));
    }

    /**
     * Creates an unmodifiable {@link Map} with five entries.
     *
     * @param key1 first key
     * @param value1 first value
     * @param key2 second key
     * @param value2 second value
     * @param key3 third key
     * @param value3 third value
     * @param key4 fourth key
     * @param value4 fourth value
     * @param key5 fifth key
     * @param value5 fifth value
     * @return unmodifiable map
     * @param <K> type of the keys in the map
     * @param <V> type of the values in the map
     */
    public static <K, V> Map<K, V> of(
        K key1, V value1,
        K key2, V value2,
        K key3, V value3,
        K key4, V value4,
        K key5, V value5
    ) {
        return Collections.unmodifiableMap(Modifiable.of(
            key1, value1,
            key2, value2,
            key3, value3,
            key4, value4,
            key5, value5
        ));
    }

    /**
     * Creates an unmodifiable {@link Map} with six entries.
     *
     * @param key1 first key
     * @param value1 first value
     * @param key2 second key
     * @param value2 second value
     * @param key3 third key
     * @param value3 third value
     * @param key4 fourth key
     * @param value4 fourth value
     * @param key5 fifth key
     * @param value5 fifth value
     * @param key6 sixth key
     * @param value6 sixth value
     * @return unmodifiable map
     * @param <K> type of the keys in the map
     * @param <V> type of the values in the map
     */
    public static <K, V> Map<K, V> of(
        K key1, V value1,
        K key2, V value2,
        K key3, V value3,
        K key4, V value4,
        K key5, V value5,
        K key6, V value6
    ) {
        return Collections.unmodifiableMap(Modifiable.of(
            key1, value1,
            key2, value2,
            key3, value3,
            key4, value4,
            key5, value5,
            key6, value6
        ));
    }

    /**
     * Creates an unmodifiable {@link Map} with seven entries.
     *
     * @param key1 first key
     * @param value1 first value
     * @param key2 second key
     * @param value2 second value
     * @param key3 third key
     * @param value3 third value
     * @param key4 fourth key
     * @param value4 fourth value
     * @param key5 fifth key
     * @param value5 fifth value
     * @param key6 sixth key
     * @param value6 sixth value
     * @param key7 seventh key
     * @param value7 seventh value
     * @return unmodifiable map
     * @param <K> type of the keys in the map
     * @param <V> type of the values in the map
     */
    public static <K, V> Map<K, V> of(
        K key1, V value1,
        K key2, V value2,
        K key3, V value3,
        K key4, V value4,
        K key5, V value5,
        K key6, V value6,
        K key7, V value7
    ) {
        return Collections.unmodifiableMap(Modifiable.of(
            key1, value1,
            key2, value2,
            key3, value3,
            key4, value4,
            key5, value5,
            key6, value6,
            key7, value7
        ));
    }

    /**
     * Creates an unmodifiable {@link Map} with eight entries.
     *
     * @param key1 first key
     * @param value1 first value
     * @param key2 second key
     * @param value2 second value
     * @param key3 third key
     * @param value3 third value
     * @param key4 fourth key
     * @param value4 fourth value
     * @param key5 fifth key
     * @param value5 fifth value
     * @param key6 sixth key
     * @param value6 sixth value
     * @param key7 seventh key
     * @param value7 seventh value
     * @param key8 eight key
     * @param value8 eight value
     * @return unmodifiable map
     * @param <K> type of the keys in the map
     * @param <V> type of the values in the map
     */
    public static <K, V> Map<K, V> of(
        K key1, V value1,
        K key2, V value2,
        K key3, V value3,
        K key4, V value4,
        K key5, V value5,
        K key6, V value6,
        K key7, V value7,
        K key8, V value8
    ) {
        return Collections.unmodifiableMap(Modifiable.of(
            key1, value1,
            key2, value2,
            key3, value3,
            key4, value4,
            key5, value5,
            key6, value6,
            key7, value7,
            key8, value8
        ));
    }

    /**
     * Creates an unmodifiable {@link Map} with nine entries.
     *
     * @param key1 first key
     * @param value1 first value
     * @param key2 second key
     * @param value2 second value
     * @param key3 third key
     * @param value3 third value
     * @param key4 fourth key
     * @param value4 fourth value
     * @param key5 fifth key
     * @param value5 fifth value
     * @param key6 sixth key
     * @param value6 sixth value
     * @param key7 seventh key
     * @param value7 seventh value
     * @param key8 eight key
     * @param value8 eight value
     * @param key9 ninth key
     * @param value9 ninth value
     * @return unmodifiable map
     * @param <K> type of the keys in the map
     * @param <V> type of the values in the map
     */
    public static <K, V> Map<K, V> of(
        K key1, V value1,
        K key2, V value2,
        K key3, V value3,
        K key4, V value4,
        K key5, V value5,
        K key6, V value6,
        K key7, V value7,
        K key8, V value8,
        K key9, V value9
    ) {
        return Collections.unmodifiableMap(Modifiable.of(
            key1, value1,
            key2, value2,
            key3, value3,
            key4, value4,
            key5, value5,
            key6, value6,
            key7, value7,
            key8, value8,
            key9, value9
        ));
    }

    /**
     * Creates an unmodifiable {@link Map} with ten entries.
     *
     * @param key1 first key
     * @param value1 first value
     * @param key2 second key
     * @param value2 second value
     * @param key3 third key
     * @param value3 third value
     * @param key4 fourth key
     * @param value4 fourth value
     * @param key5 fifth key
     * @param value5 fifth value
     * @param key6 sixth key
     * @param value6 sixth value
     * @param key7 seventh key
     * @param value7 seventh value
     * @param key8 eight key
     * @param value8 eight value
     * @param key9 ninth key
     * @param value9 ninth value
     * @param key10 tenth key
     * @param value10 tenth value
     * @return unmodifiable map
     * @param <K> type of the keys in the map
     * @param <V> type of the values in the map
     */
    public static <K, V> Map<K, V> of(
        K key1, V value1,
        K key2, V value2,
        K key3, V value3,
        K key4, V value4,
        K key5, V value5,
        K key6, V value6,
        K key7, V value7,
        K key8, V value8,
        K key9, V value9,
        K key10, V value10
    ) {
        return Collections.unmodifiableMap(Modifiable.of(
            key1, value1,
            key2, value2,
            key3, value3,
            key4, value4,
            key5, value5,
            key6, value6,
            key7, value7,
            key8, value8,
            key9, value9,
            key10, value10
        ));
    }

    /**
     * Creates an unmodifiable empty linked {@link Map}.
     *
     * @return unmodifiable empty linked map
     * @param <K> type of the keys in the map
     * @param <V> type of the values in the map
     * @param <M> unused type parameter
     */
    public static <K, V, M> Map<K, V> ofLinked() {
        return Collections.unmodifiableMap(Modifiable.ofLinked());
    }

    /**
     * Creates an unmodifiable linked {@link Map} with one entry.
     *
     * @param key1 first key
     * @param value1 first value
     * @return unmodifiable linked map
     * @param <K> type of the keys in the map
     * @param <V> type of the values in the map
     * @param <M> unused type parameter
     */
    public static <K, V, M> Map<K, V> ofLinked(K key1, V value1) {
        return Collections.unmodifiableMap(Modifiable.ofLinked(
            key1, value1
        ));
    }

    /**
     * Creates an unmodifiable linked {@link Map} with two entries.
     *
     * @param key1 first key
     * @param value1 first value
     * @param key2 second key
     * @param value2 second value
     * @return unmodifiable linked map
     * @param <K> type of the keys in the map
     * @param <V> type of the values in the map
     */
    public static <K, V> Map<K, V> ofLinked(
        K key1, V value1,
        K key2, V value2
    ) {
        return Collections.unmodifiableMap(Modifiable.ofLinked(
            key1, value1,
            key2, value2
        ));
    }

    /**
     * Creates an unmodifiable linked {@link Map} with three entries.
     *
     * @param key1 first key
     * @param value1 first value
     * @param key2 second key
     * @param value2 second value
     * @param key3 third key
     * @param value3 third value
     * @return unmodifiable linked map
     * @param <K> type of the keys in the map
     * @param <V> type of the values in the map
     */
    public static <K, V> Map<K, V> ofLinked(
        K key1, V value1,
        K key2, V value2,
        K key3, V value3
    ) {
        return Collections.unmodifiableMap(Modifiable.ofLinked(
            key1, value1,
            key2, value2,
            key3, value3
        ));
    }

    /**
     * Creates an unmodifiable linked {@link Map} with four entries.
     *
     * @param key1 first key
     * @param value1 first value
     * @param key2 second key
     * @param value2 second value
     * @param key3 third key
     * @param value3 third value
     * @param key4 fourth key
     * @param value4 fourth value
     * @return unmodifiable linked map
     * @param <K> type of the keys in the map
     * @param <V> type of the values in the map
     */
    public static <K, V> Map<K, V> ofLinked(
        K key1, V value1,
        K key2, V value2,
        K key3, V value3,
        K key4, V value4
    ) {
        return Collections.unmodifiableMap(Modifiable.ofLinked(
            key1, value1,
            key2, value2,
            key3, value3,
            key4, value4
        ));
    }

    /**
     * Creates an unmodifiable linked {@link Map} with five entries.
     *
     * @param key1 first key
     * @param value1 first value
     * @param key2 second key
     * @param value2 second value
     * @param key3 third key
     * @param value3 third value
     * @param key4 fourth key
     * @param value4 fourth value
     * @param key5 fifth key
     * @param value5 fifth value
     * @return unmodifiable linked map
     * @param <K> type of the keys in the map
     * @param <V> type of the values in the map
     */
    public static <K, V> Map<K, V> ofLinked(
        K key1, V value1,
        K key2, V value2,
        K key3, V value3,
        K key4, V value4,
        K key5, V value5
    ) {
        return Collections.unmodifiableMap(Modifiable.ofLinked(
            key1, value1,
            key2, value2,
            key3, value3,
            key4, value4,
            key5, value5
        ));
    }

    /**
     * Creates an unmodifiable linked {@link Map} with six entries.
     *
     * @param key1 first key
     * @param value1 first value
     * @param key2 second key
     * @param value2 second value
     * @param key3 third key
     * @param value3 third value
     * @param key4 fourth key
     * @param value4 fourth value
     * @param key5 fifth key
     * @param value5 fifth value
     * @param key6 sixth key
     * @param value6 sixth value
     * @return unmodifiable linked map
     * @param <K> type of the keys in the map
     * @param <V> type of the values in the map
     */
    public static <K, V> Map<K, V> ofLinked(
        K key1, V value1,
        K key2, V value2,
        K key3, V value3,
        K key4, V value4,
        K key5, V value5,
        K key6, V value6
    ) {
        return Collections.unmodifiableMap(Modifiable.ofLinked(
            key1, value1,
            key2, value2,
            key3, value3,
            key4, value4,
            key5, value5,
            key6, value6
        ));
    }

    /**
     * Creates an unmodifiable linked {@link Map} with seven entries.
     *
     * @param key1 first key
     * @param value1 first value
     * @param key2 second key
     * @param value2 second value
     * @param key3 third key
     * @param value3 third value
     * @param key4 fourth key
     * @param value4 fourth value
     * @param key5 fifth key
     * @param value5 fifth value
     * @param key6 sixth key
     * @param value6 sixth value
     * @param key7 seventh key
     * @param value7 seventh value
     * @return unmodifiable linked map
     * @param <K> type of the keys in the map
     * @param <V> type of the values in the map
     */
    public static <K, V> Map<K, V> ofLinked(
        K key1, V value1,
        K key2, V value2,
        K key3, V value3,
        K key4, V value4,
        K key5, V value5,
        K key6, V value6,
        K key7, V value7
    ) {
        return Collections.unmodifiableMap(Modifiable.ofLinked(
            key1, value1,
            key2, value2,
            key3, value3,
            key4, value4,
            key5, value5,
            key6, value6,
            key7, value7
        ));
    }

    /**
     * Creates an unmodifiable linked {@link Map} with eight entries.
     *
     * @param key1 first key
     * @param value1 first value
     * @param key2 second key
     * @param value2 second value
     * @param key3 third key
     * @param value3 third value
     * @param key4 fourth key
     * @param value4 fourth value
     * @param key5 fifth key
     * @param value5 fifth value
     * @param key6 sixth key
     * @param value6 sixth value
     * @param key7 seventh key
     * @param value7 seventh value
     * @param key8 eight key
     * @param value8 eight value
     * @return unmodifiable linked map
     * @param <K> type of the keys in the map
     * @param <V> type of the values in the map
     */
    public static <K, V> Map<K, V> ofLinked(
        K key1, V value1,
        K key2, V value2,
        K key3, V value3,
        K key4, V value4,
        K key5, V value5,
        K key6, V value6,
        K key7, V value7,
        K key8, V value8
    ) {
        return Collections.unmodifiableMap(Modifiable.ofLinked(
            key1, value1,
            key2, value2,
            key3, value3,
            key4, value4,
            key5, value5,
            key6, value6,
            key7, value7,
            key8, value8
        ));
    }

    /**
     * Creates an unmodifiable linked {@link Map} with nine entries.
     *
     * @param key1 first key
     * @param value1 first value
     * @param key2 second key
     * @param value2 second value
     * @param key3 third key
     * @param value3 third value
     * @param key4 fourth key
     * @param value4 fourth value
     * @param key5 fifth key
     * @param value5 fifth value
     * @param key6 sixth key
     * @param value6 sixth value
     * @param key7 seventh key
     * @param value7 seventh value
     * @param key8 eight key
     * @param value8 eight value
     * @param key9 ninth key
     * @param value9 ninth value
     * @return unmodifiable linked map
     * @param <K> type of the keys in the map
     * @param <V> type of the values in the map
     */
    public static <K, V> Map<K, V> ofLinked(
        K key1, V value1,
        K key2, V value2,
        K key3, V value3,
        K key4, V value4,
        K key5, V value5,
        K key6, V value6,
        K key7, V value7,
        K key8, V value8,
        K key9, V value9
    ) {
        return Collections.unmodifiableMap(Modifiable.ofLinked(
            key1, value1,
            key2, value2,
            key3, value3,
            key4, value4,
            key5, value5,
            key6, value6,
            key7, value7,
            key8, value8,
            key9, value9
        ));
    }

    /**
     * Creates an unmodifiable linked {@link Map} with ten entries.
     *
     * @param key1 first key
     * @param value1 first value
     * @param key2 second key
     * @param value2 second value
     * @param key3 third key
     * @param value3 third value
     * @param key4 fourth key
     * @param value4 fourth value
     * @param key5 fifth key
     * @param value5 fifth value
     * @param key6 sixth key
     * @param value6 sixth value
     * @param key7 seventh key
     * @param value7 seventh value
     * @param key8 eight key
     * @param value8 eight value
     * @param key9 ninth key
     * @param value9 ninth value
     * @param key10 tenth key
     * @param value10 tenth value
     * @return unmodifiable linked map
     * @param <K> type of the keys in the map
     * @param <V> type of the values in the map
     */
    public static <K, V> Map<K, V> ofLinked(
        K key1, V value1,
        K key2, V value2,
        K key3, V value3,
        K key4, V value4,
        K key5, V value5,
        K key6, V value6,
        K key7, V value7,
        K key8, V value8,
        K key9, V value9,
        K key10, V value10
    ) {
        return Collections.unmodifiableMap(Modifiable.ofLinked(
            key1, value1,
            key2, value2,
            key3, value3,
            key4, value4,
            key5, value5,
            key6, value6,
            key7, value7,
            key8, value8,
            key9, value9,
            key10, value10
        ));
    }

    /**
     * For creating simple modifiable maps with just a few lines of code:
     * <pre>
     *     Maps.Modifiable.of(
     *      "key1", 1,
     *      "key2", 2,
     *      "key3", 3);
     * </pre>
     */
    @UtilityClass
    public static class Modifiable {
        /**
         * Creates a modifiable empty {@link Map}.
         *
         * @return modifiable empty map
         * @param <K> type of the keys in the map
         * @param <V> type of the values in the map
         */
        public static <K, V> Map<K, V> of() {
            return new HashMap<>();
        }

        /**
         * Creates a modifiable {@link Map} with one entry.
         *
         * @param key1 first key
         * @param value1 first value
         * @return modifiable map
         * @param <K> type of the keys in the map
         * @param <V> type of the values in the map
         */
        public static <K, V> Map<K, V> of(K key1, V value1) {
            HashMap<K, V> map = new HashMap<>();
            map.put(key1, value1);

            return map;
        }

        /**
         * Creates a modifiable {@link Map} with two entries.
         *
         * @param key1 first key
         * @param value1 first value
         * @param key2 second key
         * @param value2 second value
         * @return modifiable map
         * @param <K> type of the keys in the map
         * @param <V> type of the values in the map
         */
        public static <K, V> Map<K, V> of(
            K key1, V value1,
            K key2, V value2
        ) {
            HashMap<K, V> map = new HashMap<>();
            map.put(key1, value1);
            map.put(key2, value2);

            return map;
        }

        /**
         * Creates a modifiable {@link Map} with three entries.
         *
         * @param key1 first key
         * @param value1 first value
         * @param key2 second key
         * @param value2 second value
         * @param key3 third key
         * @param value3 third value
         * @return modifiable map
         * @param <K> type of the keys in the map
         * @param <V> type of the values in the map
         */
        public static <K, V> Map<K, V> of(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3
        ) {
            HashMap<K, V> map = new HashMap<>();
            map.put(key1, value1);
            map.put(key2, value2);
            map.put(key3, value3);

            return map;
        }

        /**
         * Creates a modifiable {@link Map} with four entries.
         *
         * @param key1 first key
         * @param value1 first value
         * @param key2 second key
         * @param value2 second value
         * @param key3 third key
         * @param value3 third value
         * @param key4 fourth key
         * @param value4 fourth value
         * @return modifiable map
         * @param <K> type of the keys in the map
         * @param <V> type of the values in the map
         */
        public static <K, V> Map<K, V> of(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4
        ) {
            HashMap<K, V> map = new HashMap<>();
            map.put(key1, value1);
            map.put(key2, value2);
            map.put(key3, value3);
            map.put(key4, value4);

            return map;
        }

        /**
         * Creates a modifiable {@link Map} with five entries.
         *
         * @param key1 first key
         * @param value1 first value
         * @param key2 second key
         * @param value2 second value
         * @param key3 third key
         * @param value3 third value
         * @param key4 fourth key
         * @param value4 fourth value
         * @param key5 fifth key
         * @param value5 fifth value
         * @return modifiable map
         * @param <K> type of the keys in the map
         * @param <V> type of the values in the map
         */
        public static <K, V> Map<K, V> of(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4,
            K key5, V value5
        ) {
            HashMap<K, V> map = new HashMap<>();
            map.put(key1, value1);
            map.put(key2, value2);
            map.put(key3, value3);
            map.put(key4, value4);
            map.put(key5, value5);

            return map;
        }

        /**
         * Creates a modifiable {@link Map} with six entries.
         *
         * @param key1 first key
         * @param value1 first value
         * @param key2 second key
         * @param value2 second value
         * @param key3 third key
         * @param value3 third value
         * @param key4 fourth key
         * @param value4 fourth value
         * @param key5 fifth key
         * @param value5 fifth value
         * @param key6 sixth key
         * @param value6 sixth value
         * @return modifiable map
         * @param <K> type of the keys in the map
         * @param <V> type of the values in the map
         */
        public static <K, V> Map<K, V> of(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4,
            K key5, V value5,
            K key6, V value6
        ) {
            HashMap<K, V> map = new HashMap<>();
            map.put(key1, value1);
            map.put(key2, value2);
            map.put(key3, value3);
            map.put(key4, value4);
            map.put(key5, value5);
            map.put(key6, value6);

            return map;
        }

        /**
         * Creates a modifiable {@link Map} with seven entries.
         *
         * @param key1 first key
         * @param value1 first value
         * @param key2 second key
         * @param value2 second value
         * @param key3 third key
         * @param value3 third value
         * @param key4 fourth key
         * @param value4 fourth value
         * @param key5 fifth key
         * @param value5 fifth value
         * @param key6 sixth key
         * @param value6 sixth value
         * @param key7 seventh key
         * @param value7 seventh value
         * @return modifiable map
         * @param <K> type of the keys in the map
         * @param <V> type of the values in the map
         */
        public static <K, V> Map<K, V> of(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4,
            K key5, V value5,
            K key6, V value6,
            K key7, V value7
        ) {
            HashMap<K, V> map = new HashMap<>();
            map.put(key1, value1);
            map.put(key2, value2);
            map.put(key3, value3);
            map.put(key4, value4);
            map.put(key5, value5);
            map.put(key6, value6);
            map.put(key7, value7);

            return map;
        }

        /**
         * Creates a modifiable {@link Map} with eight entries.
         *
         * @param key1 first key
         * @param value1 first value
         * @param key2 second key
         * @param value2 second value
         * @param key3 third key
         * @param value3 third value
         * @param key4 fourth key
         * @param value4 fourth value
         * @param key5 fifth key
         * @param value5 fifth value
         * @param key6 sixth key
         * @param value6 sixth value
         * @param key7 seventh key
         * @param value7 seventh value
         * @param key8 eight key
         * @param value8 eight value
         * @return modifiable map
         * @param <K> type of the keys in the map
         * @param <V> type of the values in the map
         */
        public static <K, V> Map<K, V> of(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4,
            K key5, V value5,
            K key6, V value6,
            K key7, V value7,
            K key8, V value8
        ) {
            HashMap<K, V> map = new HashMap<>();
            map.put(key1, value1);
            map.put(key2, value2);
            map.put(key3, value3);
            map.put(key4, value4);
            map.put(key5, value5);
            map.put(key6, value6);
            map.put(key7, value7);
            map.put(key8, value8);

            return map;
        }

        /**
         * Creates a modifiable {@link Map} with nine entries.
         *
         * @param key1 first key
         * @param value1 first value
         * @param key2 second key
         * @param value2 second value
         * @param key3 third key
         * @param value3 third value
         * @param key4 fourth key
         * @param value4 fourth value
         * @param key5 fifth key
         * @param value5 fifth value
         * @param key6 sixth key
         * @param value6 sixth value
         * @param key7 seventh key
         * @param value7 seventh value
         * @param key8 eight key
         * @param value8 eight value
         * @param key9 ninth key
         * @param value9 ninth value
         * @return modifiable map
         * @param <K> type of the keys in the map
         * @param <V> type of the values in the map
         */
        public static <K, V> Map<K, V> of(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4,
            K key5, V value5,
            K key6, V value6,
            K key7, V value7,
            K key8, V value8,
            K key9, V value9
        ) {
            HashMap<K, V> map = new HashMap<>();
            map.put(key1, value1);
            map.put(key2, value2);
            map.put(key3, value3);
            map.put(key4, value4);
            map.put(key5, value5);
            map.put(key6, value6);
            map.put(key7, value7);
            map.put(key8, value8);
            map.put(key9, value9);

            return map;
        }

        /**
         * Creates a modifiable {@link Map} with ten entries.
         *
         * @param key1 first key
         * @param value1 first value
         * @param key2 second key
         * @param value2 second value
         * @param key3 third key
         * @param value3 third value
         * @param key4 fourth key
         * @param value4 fourth value
         * @param key5 fifth key
         * @param value5 fifth value
         * @param key6 sixth key
         * @param value6 sixth value
         * @param key7 seventh key
         * @param value7 seventh value
         * @param key8 eight key
         * @param value8 eight value
         * @param key9 ninth key
         * @param value9 ninth value
         * @param key10 tenth key
         * @param value10 tenth value
         * @return modifiable map
         * @param <K> type of the keys in the map
         * @param <V> type of the values in the map
         */
        public static <K, V> Map<K, V> of(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4,
            K key5, V value5,
            K key6, V value6,
            K key7, V value7,
            K key8, V value8,
            K key9, V value9,
            K key10, V value10
        ) {
            HashMap<K, V> map = new HashMap<>();
            map.put(key1, value1);
            map.put(key2, value2);
            map.put(key3, value3);
            map.put(key4, value4);
            map.put(key5, value5);
            map.put(key6, value6);
            map.put(key7, value7);
            map.put(key8, value8);
            map.put(key9, value9);
            map.put(key10, value10);

            return map;
        }

        /**
         * Creates a modifiable empty linked {@link Map}.
         *
         * @return modifiable empty linked map
         * @param <K> type of the keys in the map
         * @param <V> type of the values in the map
         */
        public static <K, V> Map<K, V> ofLinked() {
            return new LinkedHashMap<>();
        }

        /**
         * Creates a modifiable linked {@link Map} with one entry.
         *
         * @param key1 first key
         * @param value1 first value
         * @return modifiable linked map
         * @param <K> type of the keys in the map
         * @param <V> type of the values in the map
         */
        public static <K, V> Map<K, V> ofLinked(K key1, V value1) {
            LinkedHashMap<K, V> map = new LinkedHashMap<>();
            map.put(key1, value1);

            return map;
        }

        /**
         * Creates a modifiable linked {@link Map} with two entries.
         *
         * @param key1 first key
         * @param value1 first value
         * @param key2 second key
         * @param value2 second value
         * @return modifiable linked map
         * @param <K> type of the keys in the map
         * @param <V> type of the values in the map
         */
        public static <K, V> Map<K, V> ofLinked(
            K key1, V value1,
            K key2, V value2
        ) {
            LinkedHashMap<K, V> map = new LinkedHashMap<>();
            map.put(key1, value1);
            map.put(key2, value2);

            return map;
        }

        /**
         * Creates a modifiable linked {@link Map} with three entries.
         *
         * @param key1 first key
         * @param value1 first value
         * @param key2 second key
         * @param value2 second value
         * @param key3 third key
         * @param value3 third value
         * @return modifiable linked map
         * @param <K> type of the keys in the map
         * @param <V> type of the values in the map
         */
        public static <K, V> Map<K, V> ofLinked(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3
        ) {
            LinkedHashMap<K, V> map = new LinkedHashMap<>();
            map.put(key1, value1);
            map.put(key2, value2);
            map.put(key3, value3);

            return map;
        }

        /**
         * Creates a modifiable linked {@link Map} with four entries.
         *
         * @param key1 first key
         * @param value1 first value
         * @param key2 second key
         * @param value2 second value
         * @param key3 third key
         * @param value3 third value
         * @param key4 fourth key
         * @param value4 fourth value
         * @return modifiable linked map
         * @param <K> type of the keys in the map
         * @param <V> type of the values in the map
         */
        public static <K, V> Map<K, V> ofLinked(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4
        ) {
            LinkedHashMap<K, V> map = new LinkedHashMap<>();
            map.put(key1, value1);
            map.put(key2, value2);
            map.put(key3, value3);
            map.put(key4, value4);

            return map;
        }

        /**
         * Creates a modifiable linked {@link Map} with five entries.
         *
         * @param key1 first key
         * @param value1 first value
         * @param key2 second key
         * @param value2 second value
         * @param key3 third key
         * @param value3 third value
         * @param key4 fourth key
         * @param value4 fourth value
         * @param key5 fifth key
         * @param value5 fifth value
         * @return modifiable linked map
         * @param <K> type of the keys in the map
         * @param <V> type of the values in the map
         */
        public static <K, V> Map<K, V> ofLinked(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4,
            K key5, V value5
        ) {
            LinkedHashMap<K, V> map = new LinkedHashMap<>();
            map.put(key1, value1);
            map.put(key2, value2);
            map.put(key3, value3);
            map.put(key4, value4);
            map.put(key5, value5);

            return map;
        }

        /**
         * Creates a modifiable linked {@link Map} with six entries.
         *
         * @param key1 first key
         * @param value1 first value
         * @param key2 second key
         * @param value2 second value
         * @param key3 third key
         * @param value3 third value
         * @param key4 fourth key
         * @param value4 fourth value
         * @param key5 fifth key
         * @param value5 fifth value
         * @param key6 sixth key
         * @param value6 sixth value
         * @return modifiable linked map
         * @param <K> type of the keys in the map
         * @param <V> type of the values in the map
         */
        public static <K, V> Map<K, V> ofLinked(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4,
            K key5, V value5,
            K key6, V value6
        ) {
            LinkedHashMap<K, V> map = new LinkedHashMap<>();
            map.put(key1, value1);
            map.put(key2, value2);
            map.put(key3, value3);
            map.put(key4, value4);
            map.put(key5, value5);
            map.put(key6, value6);

            return map;
        }

        /**
         * Creates a modifiable linked {@link Map} with seven entries.
         *
         * @param key1 first key
         * @param value1 first value
         * @param key2 second key
         * @param value2 second value
         * @param key3 third key
         * @param value3 third value
         * @param key4 fourth key
         * @param value4 fourth value
         * @param key5 fifth key
         * @param value5 fifth value
         * @param key6 sixth key
         * @param value6 sixth value
         * @param key7 seventh key
         * @param value7 seventh value
         * @return modifiable linked map
         * @param <K> type of the keys in the map
         * @param <V> type of the values in the map
         */
        public static <K, V> Map<K, V> ofLinked(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4,
            K key5, V value5,
            K key6, V value6,
            K key7, V value7
        ) {
            LinkedHashMap<K, V> map = new LinkedHashMap<>();
            map.put(key1, value1);
            map.put(key2, value2);
            map.put(key3, value3);
            map.put(key4, value4);
            map.put(key5, value5);
            map.put(key6, value6);
            map.put(key7, value7);

            return map;
        }

        /**
         * Creates a modifiable linked {@link Map} with eight entries.
         *
         * @param key1 first key
         * @param value1 first value
         * @param key2 second key
         * @param value2 second value
         * @param key3 third key
         * @param value3 third value
         * @param key4 fourth key
         * @param value4 fourth value
         * @param key5 fifth key
         * @param value5 fifth value
         * @param key6 sixth key
         * @param value6 sixth value
         * @param key7 seventh key
         * @param value7 seventh value
         * @param key8 eight key
         * @param value8 eight value
         * @return modifiable linked map
         * @param <K> type of the keys in the map
         * @param <V> type of the values in the map
         */
        public static <K, V> Map<K, V> ofLinked(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4,
            K key5, V value5,
            K key6, V value6,
            K key7, V value7,
            K key8, V value8
        ) {
            LinkedHashMap<K, V> map = new LinkedHashMap<>();
            map.put(key1, value1);
            map.put(key2, value2);
            map.put(key3, value3);
            map.put(key4, value4);
            map.put(key5, value5);
            map.put(key6, value6);
            map.put(key7, value7);
            map.put(key8, value8);

            return map;
        }

        /**
         * Creates a modifiable linked {@link Map} with nine entries.
         *
         * @param key1 first key
         * @param value1 first value
         * @param key2 second key
         * @param value2 second value
         * @param key3 third key
         * @param value3 third value
         * @param key4 fourth key
         * @param value4 fourth value
         * @param key5 fifth key
         * @param value5 fifth value
         * @param key6 sixth key
         * @param value6 sixth value
         * @param key7 seventh key
         * @param value7 seventh value
         * @param key8 eight key
         * @param value8 eight value
         * @param key9 ninth key
         * @param value9 ninth value
         * @return modifiable linked map
         * @param <K> type of the keys in the map
         * @param <V> type of the values in the map
         */
        public static <K, V> Map<K, V> ofLinked(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4,
            K key5, V value5,
            K key6, V value6,
            K key7, V value7,
            K key8, V value8,
            K key9, V value9
        ) {
            LinkedHashMap<K, V> map = new LinkedHashMap<>();
            map.put(key1, value1);
            map.put(key2, value2);
            map.put(key3, value3);
            map.put(key4, value4);
            map.put(key5, value5);
            map.put(key6, value6);
            map.put(key7, value7);
            map.put(key8, value8);
            map.put(key9, value9);

            return map;
        }

        /**
         * Creates a modifiable linked {@link Map} with ten entries.
         *
         * @param key1 first key
         * @param value1 first value
         * @param key2 second key
         * @param value2 second value
         * @param key3 third key
         * @param value3 third value
         * @param key4 fourth key
         * @param value4 fourth value
         * @param key5 fifth key
         * @param value5 fifth value
         * @param key6 sixth key
         * @param value6 sixth value
         * @param key7 seventh key
         * @param value7 seventh value
         * @param key8 eight key
         * @param value8 eight value
         * @param key9 ninth key
         * @param value9 ninth value
         * @param key10 tenth key
         * @param value10 tenth value
         * @return modifiable linked map
         * @param <K> type of the keys in the map
         * @param <V> type of the values in the map
         */
        public static <K, V> Map<K, V> ofLinked(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4,
            K key5, V value5,
            K key6, V value6,
            K key7, V value7,
            K key8, V value8,
            K key9, V value9,
            K key10, V value10
        ) {
            LinkedHashMap<K, V> map = new LinkedHashMap<>();
            map.put(key1, value1);
            map.put(key2, value2);
            map.put(key3, value3);
            map.put(key4, value4);
            map.put(key5, value5);
            map.put(key6, value6);
            map.put(key7, value7);
            map.put(key8, value8);
            map.put(key9, value9);
            map.put(key10, value10);

            return map;
        }
    }
}
