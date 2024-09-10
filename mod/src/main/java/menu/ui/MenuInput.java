package menu.ui;

import com.github.argon.sos.mod.sdk.game.ui.Input;
import menu.MenuUi;
import snake2d.util.sprite.text.StringInputSprite;

public class MenuInput extends Input {

	public MenuInput(StringInputSprite input) {
		super(input, MenuUi.MOUSE_COO_SUPPLIER, 0);
	}

	public MenuInput(StringInputSprite input, int inputWidth) {
		super(input, MenuUi.MOUSE_COO_SUPPLIER, inputWidth);
	}
}
