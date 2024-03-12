package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.game.ui.Valuable;
import lombok.Getter;
import snake2d.util.gui.GuiSection;


public abstract class AbstractConfigPanel<Config, Element> extends GuiSection implements Valuable<Config, Element> {
    protected final Config defaultConfig;
    @Getter
    protected final String title;

    public AbstractConfigPanel(String title, Config defaultConfig) {
        this.defaultConfig = defaultConfig;
        this.title = title;
    }

    public void resetToDefault() {
        setValue(defaultConfig);
    }
}
