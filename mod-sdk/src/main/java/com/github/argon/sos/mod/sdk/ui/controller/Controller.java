package com.github.argon.sos.mod.sdk.ui.controller;

import com.github.argon.sos.mod.sdk.phase.Phases;

/**
 * Used for adding functionality to UI elements.
 *
 * @param <UiElement> type of ui element to control
 */
public interface Controller<UiElement> extends Phases {
    /**
     * Returns the controlled ui element.
     *
     * @return controller ui element
     */
    UiElement getElement();
}
