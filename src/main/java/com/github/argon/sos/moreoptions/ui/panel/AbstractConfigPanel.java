package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.game.Action;
import com.github.argon.sos.moreoptions.game.ui.Refreshable;
import com.github.argon.sos.moreoptions.game.ui.Valuable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import snake2d.util.gui.GuiSection;


public abstract class AbstractConfigPanel<Config, Element> extends GuiSection implements
    Valuable<Config, Element>,
    Refreshable<Element> {

    protected final Config defaultConfig;
    @Getter
    protected final String title;
    protected final int availableWidth;
    protected final int availableHeight;

    @Setter
    @Accessors(fluent = true, chain = false)
    protected Action<Element> refreshAction = o -> {};

    public AbstractConfigPanel(String title, Config defaultConfig, int availableWidth, int availableHeight) {
        this.defaultConfig = defaultConfig;
        this.title = title;
        this.availableWidth = availableWidth;
        this.availableHeight = availableHeight;
    }

    public void resetToDefault() {
        setValue(defaultConfig);
    }

    @Override
    public void refresh() {
        refreshAction.accept(element());
    }

    protected abstract Element element();
}
