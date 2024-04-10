package com.github.argon.sos.moreoptions.ui.tab;

import com.github.argon.sos.moreoptions.game.action.Action;
import com.github.argon.sos.moreoptions.game.action.Refreshable;
import com.github.argon.sos.moreoptions.game.action.Valuable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import snake2d.util.gui.GuiSection;


public abstract class AbstractConfigTab<Config, Element> extends GuiSection implements
    Valuable<Config>,
    Refreshable {

    protected final Config defaultConfig;
    @Getter
    protected final String title;
    protected final int availableWidth;
    protected final int availableHeight;

    @Setter
    @Accessors(fluent = true, chain = false)
    protected Action<Element> refreshAction = o -> {};

    public AbstractConfigTab(String title, Config defaultConfig, int availableWidth, int availableHeight) {
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
