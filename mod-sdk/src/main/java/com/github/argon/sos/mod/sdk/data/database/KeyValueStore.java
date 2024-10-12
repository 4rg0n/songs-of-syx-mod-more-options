package com.github.argon.sos.mod.sdk.data.database;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class KeyValueStore {
    private final Map<String, String> memory = new HashMap<>();
    private final KeyValueDatabase file;

    public void init() {
        file.init();
    }

    public void load() {
        memory.clear();
        memory.putAll(file.findAll());
    }

    public void flush() {
        file.deleteAll();
        file.insert(memory);
    }

    public void save(String key, String value) {
        memory.put(key, value);
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(memory.get(key));
    }

    public boolean exists(String key) {
        return memory.containsKey(key);
    }

    public Map<String, String> getAll() {
        return memory;
    }
}
