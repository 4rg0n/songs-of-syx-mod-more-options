package com.github.argon.sos.mod.sdk.ui.controller;

import com.github.argon.sos.mod.sdk.ModSdkModule;
import com.github.argon.sos.mod.sdk.ui.Notificator;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * Base class for controllers.
 *
 * @param <UiElement> type of the ui element to control.
 */
public abstract class AbstractController<UiElement> implements Controller<UiElement> {
    @Getter
    private final UiElement element;

    /**
     * For displaying in game notifications
     */
    @Getter(lazy = true, value = AccessLevel.PROTECTED)
    private final Notificator notificator = ModSdkModule.notificator();

    /**
     * Contains all created controllers
     */
    @Getter(lazy = true, value = AccessLevel.PROTECTED)
    private final Controllers controllers = ModSdkModule.controllers();

    public AbstractController(UiElement element) {
        this.element = element;
        getControllers().add(this);
    }
}
