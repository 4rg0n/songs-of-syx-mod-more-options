package com.github.argon.sos.mod.sdk.ui.controller;

import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Holds and gives access to {@link UiController}s.
 */
@NoArgsConstructor
public class UiControllers {
    private final Map<Class<?>, UiController<?>> controllers = new HashMap<>();

    /**
     * Creates new {@link UiControllers} holding the given list of controllers.
     *
     * @param uiControllerList to hold
     */
    public UiControllers(List<UiController<?>> uiControllerList) {
        uiControllerList.forEach(this::add);
    }

    /**
     * Adds an controller.
     *
     * @param uiController to add
     * @return this
     */
    public UiControllers add(UiController<?> uiController) {
        controllers.put(uiController.getClass(), uiController);
        return this;
    }

    /**
     * Returns a controller instance by the given class.
     *
     * @param clazz of the controller
     * @return controller instance if present
     * @param <T> type of the controller class
     */
    public <T extends UiController<?>> Optional<T> get(Class<T> clazz) {
        UiController<?> uiController = controllers.get(clazz);

        if (uiController == null) {
            return Optional.empty();
        }

        //noinspection unchecked
        return Optional.of((T) uiController);
    }
}
