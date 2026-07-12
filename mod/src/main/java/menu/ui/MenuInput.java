package menu.ui;

import com.github.argon.sos.mod.sdk.ui.input.InputString;
import menu.MenuUi;
import snake2d.util.sprite.text.StringInputSprite;

public class MenuInput extends InputString {

	public MenuInput(StringInputSprite input) {
		super(input, MenuUi.MOUSE_COO_SUPPLIER, 0);
	}

	public MenuInput(StringInputSprite input, int inputWidth) {
		super(input, MenuUi.MOUSE_COO_SUPPLIER, inputWidth);
	}
}
