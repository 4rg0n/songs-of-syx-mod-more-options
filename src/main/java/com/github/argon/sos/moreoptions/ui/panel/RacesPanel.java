package com.github.argon.sos.moreoptions.ui.panel;

import com.github.argon.sos.moreoptions.config.MoreOptionsV2Config;
import com.github.argon.sos.moreoptions.game.ui.Valuable;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import snake2d.util.gui.GuiSection;

import java.util.Map;

/**
 * TODO
 */
public class RacesPanel extends GuiSection implements Valuable<Map<String, MoreOptionsV2Config.Range>, RacesPanel> {
    private static final Logger log = Loggers.getLogger(RacesPanel.class);
    public RacesPanel() {
    }

    @Override
    public Map<String, MoreOptionsV2Config.Range> getValue() {
        return null;
    }

    @Override
    public void setValue(Map<String, MoreOptionsV2Config.Range> config) {

    }
}
