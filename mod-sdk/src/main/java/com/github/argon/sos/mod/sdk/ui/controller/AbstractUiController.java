package com.github.argon.sos.mod.sdk.ui.controller;

import com.github.argon.sos.mod.sdk.ModSdkModule;
import com.github.argon.sos.mod.sdk.ui.notification.Notificator;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * Base class for controllers.
 *
 * @param <UiElement> type of the ui element to control.
 */
public abstract class AbstractUiController<UiElement> implements UiController<UiElement> {
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
    private final UiControllers uiControllers = ModSdkModule.uiControllers();

    /**
     * Create a controller with an ui element to control
     *
     * @param element the actual ui element to control
     */
    public AbstractUiController(UiElement element) {
        this.element = element;
        getUiControllers().add(this);
    }
}
