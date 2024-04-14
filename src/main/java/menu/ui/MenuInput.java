package menu.ui;

import com.github.argon.sos.moreoptions.game.ui.Input;
import menu.Ui;
import snake2d.util.sprite.text.StringInputSprite;

public class MenuInput extends Input {

	public MenuInput(StringInputSprite input) {
		super(input, Ui.MOUSE_COO_SUPPLIER, 0);
	}

	public MenuInput(StringInputSprite input, int inputWidth) {
		super(input, Ui.MOUSE_COO_SUPPLIER, inputWidth);
	}
}
