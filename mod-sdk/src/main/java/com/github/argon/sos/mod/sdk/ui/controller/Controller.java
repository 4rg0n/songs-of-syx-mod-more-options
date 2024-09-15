package com.github.argon.sos.mod.sdk.ui.controller;

import com.github.argon.sos.mod.sdk.phase.Phases;

/**
 * @param <UiElement> type of ui element to control
 */
public interface Controller<UiElement> extends Phases {
    UiElement getElement();
}
