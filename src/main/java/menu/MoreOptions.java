package menu;

import com.github.argon.sos.moreoptions.game.ui.Button;
import snake2d.util.gui.GuiSection;

public class MoreOptions extends GuiSection implements SC {
    public MoreOptions() {
        addDown(0, new Button("TEST"));


    }

    @Override
    public boolean back(Menu menu) {
        menu.switchScreen(menu.sandbox);
        return true;
    }
}
