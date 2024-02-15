package game.boosting;

import init.sprite.UI.Icon;
import init.sprite.UI.UI;
import snake2d.util.sprite.SPRITE;

public class BSourceInfo {

	public final CharSequence name;
	public final SPRITE icon;
	
	public BSourceInfo(CharSequence name, SPRITE icon) {
		this(name, null, icon);
	}
	
	public BSourceInfo(CharSequence name, CharSequence append, SPRITE icon) {
		
		if (append != null)
			name = name + " (" + append + ")";
		this.name = name;
		if (icon == null)
			icon = UI.icons().s.DUMMY;
		this.icon = new SPRITE.Resized(icon, Icon.S);
		
	}

	
}
