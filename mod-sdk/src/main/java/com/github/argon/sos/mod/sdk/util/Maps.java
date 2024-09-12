package com.github.argon.sos.mod.sdk.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * For creating simple maps with just a few lines of code:
 * <pre>
 *     Maps.of(
 *      "key1", 1,
 *      "key2", 2,
 *      "key3", 3);
 * </pre>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Maps {
    public static <K, V> Map<K, V> of() {
        return Collections.emptyMap();
    }

    public static <K, V> Map<K, V> of(K key1, V value1) {
        return Collections.unmodifiableMap(Modifiable.of(
            key1, value1
        ));
    }

    public static <K, V> Map<K, V> of(
        K key1, V value1,
        K key2, V value2
    ) {
        return Collections.unmodifiableMap(Modifiable.of(
            key1, value1,
            key2, value2
        ));
    }

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

    public static <K, V, M> Map<K, V> ofLinked() {
        return Collections.unmodifiableMap(Modifiable.ofLinked());
    }

    public static <K, V, M> Map<K, V> ofLinked(K key1, V value1) {
        return Collections.unmodifiableMap(Modifiable.ofLinked(
            key1, value1
        ));
    }

    public static <K, V> Map<K, V> ofLinked(
        K key1, V value1,
        K key2, V value2
    ) {
        return Collections.unmodifiableMap(Modifiable.ofLinked(
            key1, value1,
            key2, value2
        ));
    }

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

    public static class Modifiable {
        public static <K, V> Map<K, V> of() {
            return new HashMap<>();
        }

        public static <K, V> Map<K, V> of(K key1, V value1) {
            HashMap<K, V> map = new HashMap<>();
            map.put(key1, value1);

            return map;
        }

        public static <K, V> Map<K, V> of(
            K key1, V value1,
            K key2, V value2
        ) {
            HashMap<K, V> map = new HashMap<>();
            map.put(key1, value1);
            map.put(key2, value2);

            return map;
        }

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

        public static <K, V> Map<K, V> ofLinked() {
            return new LinkedHashMap<>();
        }

        public static <K, V> Map<K, V> ofLinked(K key1, V value1) {
            LinkedHashMap<K, V> map = new LinkedHashMap<>();
            map.put(key1, value1);

            return map;
        }

        public static <K, V> Map<K, V> ofLinked(
            K key1, V value1,
            K key2, V value2
        ) {
            LinkedHashMap<K, V> map = new LinkedHashMap<>();
            map.put(key1, value1);
            map.put(key2, value2);

            return map;
        }

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
