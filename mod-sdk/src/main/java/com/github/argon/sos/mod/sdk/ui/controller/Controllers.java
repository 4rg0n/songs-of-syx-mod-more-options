package com.github.argon.sos.mod.sdk.ui.controller;

import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@NoArgsConstructor
public class Controllers {
    private final Map<Class<?>, Controller<?>> controllers = new HashMap<>();

    /**
     * Creates new {@link Controllers} holding the given list of controllers.
     *
     * @param controllerList to hold
     */
    public Controllers(List<Controller<?>> controllerList) {
        controllerList.forEach(this::add);
    }

    /**
     * Adds an controller.
     *
     * @param controller to add
     * @return this
     */
    public Controllers add(Controller<?> controller) {
        controllers.put(controller.getClass(), controller);
        return this;
    }

    /**
     * Returns a controller instance by the given class.
     *
     * @param clazz of the controller
     * @return controller instance if present
     * @param <T> type of the controller class
     */
    public <T extends Controller<?>> Optional<T> get(Class<T> clazz) {
        Controller<?> controller = controllers.get(clazz);

        if (controller == null) {
            return Optional.empty();
        }

        //noinspection unchecked
        return Optional.of((T) controller);
    }
}
