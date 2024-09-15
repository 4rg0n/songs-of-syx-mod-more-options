package com.github.argon.sos.mod.sdk.ui.controller;

import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@NoArgsConstructor
public class Controllers {
    private final Map<Class<?>, Controller<?>> controllers = new HashMap<>();

    public Controllers(List<Controller<?>> controllerList) {
        controllerList.forEach(this::add);
    }

    public Controllers add(Controller<?> controller) {
        controllers.put(controller.getClass(), controller);
        return this;
    }

    public <T extends Controller<?>> Optional<T> get(Class<T> key) {
        Controller<?> controller = controllers.get(key);

        if (controller == null) {
            return Optional.empty();
        }

        //noinspection unchecked
        return Optional.of((T) controller);
    }
}
