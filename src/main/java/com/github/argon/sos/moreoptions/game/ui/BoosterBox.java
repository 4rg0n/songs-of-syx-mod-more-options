package com.github.argon.sos.moreoptions.game.ui;

import game.boosting.Boostable;
import init.sprite.UI.UI;
import settlement.entity.humanoid.Humanoid;
import settlement.stats.Induvidual;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GUI_BOX;
import snake2d.util.gui.Hoverable.HOVERABLE;
import util.colors.GCOLOR;
import util.colors.GCOLOR_UI;
import util.gui.misc.GText;
import util.info.GFORMAT;

public class BoosterBox extends HOVERABLE.HoverableAbs {
	private final Boostable boostable;
	private final Humanoid humanoid;
	private final GText text = new GText(UI.FONT().S, 6);

	public BoosterBox(Boostable boostable, Humanoid humanoid){
		super(100, 26);
		this.boostable = boostable;
		this.humanoid = humanoid;
	}

	@Override
	protected void render(SPRITE_RENDERER r, float ds, boolean isHovered) {
		GCOLOR.UI().border().render(r, body,-1);
		GCOLOR.UI().bg(true, false, isHovered).render(r, body,-2);
		double curr = boostable.get(humanoid.indu());
		double m = boostable.max(Induvidual.class);

		if (m > 0) {
			COLOR col = GCOLOR_UI.color(GCOLOR.UI().NEUTRAL.inactive, true, false, isHovered);
			int w = (int) (curr/m*(body().width()-6));
			col.render(r, body().x1()+3, body().x1()+3+w, body().y1()+3, body().y2()-3);

		}


		boostable.icon.render(r, body().x1()+3, body().y1()+3);

		text.clear();
		GFORMAT.fRel(text, curr, humanoid.race().boosts.value(boostable));
		text.render(r, body().x1()+23, body().y1()+3);

	}

	@Override
	public void hoverInfoGet(GUI_BOX text) {
		text.title(boostable.name);
		text.text(boostable.desc);
		text.NL(8);
		boostable.hover(text, humanoid.indu(), true);
	}
}
