package com.github.argon.sos.moreoptions.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Maps {
    public static <K, V> Map<K, V> of() {
        return Collections.emptyMap();
    }

    public static <K, V> Map<K, V> of(K key1, V value1) {
        HashMap<K, V> map = new HashMap<>();
        map.put(key1, value1);

        return Collections.unmodifiableMap(map);
    }

    public static <K, V> Map<K, V> of(K key1, V value1, K key2, V value2) {
        HashMap<K, V> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);

        return Collections.unmodifiableMap(map);
    }

    public static <K, V> Map<K, V> of(K key1, V value1, K key2, V value2, K key3, V value3) {
        HashMap<K, V> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);

        return Collections.unmodifiableMap(map);
    }

    public static <K, V> Map<K, V> of(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4) {
        HashMap<K, V> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);

        return Collections.unmodifiableMap(map);
    }

    public static <K, V> Map<K, V> of(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5) {
        HashMap<K, V> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        map.put(key5, value5);

        return Collections.unmodifiableMap(map);
    }

    public static <K, V, M> Map<K, V> ofLinked(K key1, V value1) {
        LinkedHashMap<K, V> map = new LinkedHashMap<>();
        map.put(key1, value1);

        return Collections.unmodifiableMap(map);
    }

    public static <K, V> Map<K, V> ofLinked(K key1, V value1, K key2, V value2) {
        LinkedHashMap<K, V> map = new LinkedHashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);

        return Collections.unmodifiableMap(map);
    }

    public static <K, V> Map<K, V> ofLinked(K key1, V value1, K key2, V value2, K key3, V value3) {
        LinkedHashMap<K, V> map = new LinkedHashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);

        return Collections.unmodifiableMap(map);
    }

    public static <K, V> Map<K, V> ofLinked(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4) {
        LinkedHashMap<K, V> map = new LinkedHashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);

        return Collections.unmodifiableMap(map);
    }

    public static <K, V> Map<K, V> ofLinked(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5) {
        LinkedHashMap<K, V> map = new LinkedHashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        map.put(key5, value5);

        return Collections.unmodifiableMap(map);
    }

    public static <K, V> Map<K, V> ofLinked(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5, K key6, V value6) {
        LinkedHashMap<K, V> map = new LinkedHashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        map.put(key5, value5);
        map.put(key6, value6);

        return Collections.unmodifiableMap(map);
    }

    public static <K, V> Map<K, V> ofLinked(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5, K key6, V value6, K key7, V value7) {
        LinkedHashMap<K, V> map = new LinkedHashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        map.put(key5, value5);
        map.put(key7, value7);

        return Collections.unmodifiableMap(map);
    }
}
