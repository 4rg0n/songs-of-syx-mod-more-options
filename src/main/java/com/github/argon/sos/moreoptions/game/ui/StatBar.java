package com.github.argon.sos.moreoptions.game.ui;

import settlement.entity.humanoid.Humanoid;
import settlement.stats.stat.STAT;
import snake2d.SPRITE_RENDERER;
import snake2d.util.gui.GUI_BOX;
import snake2d.util.gui.clickable.CLICKABLE;
import util.gui.misc.GBox;
import util.gui.misc.GMeter;
import util.info.GFORMAT;
import view.sett.ui.standing.StatRow;

public class StatBar extends CLICKABLE.ClickableAbs {
	private final Humanoid humanoid;
	private final STAT stat;

	public StatBar(Humanoid humanoid, STAT stat){
		this.stat = stat;
		this.humanoid = humanoid;
		body.setDim(200, 24);
	}

	@Override
	protected void render(SPRITE_RENDERER render, float ds, boolean isActive, boolean isSelected, boolean isHovered) {
		double now = stat.standing().get(humanoid.indu());
		double max = stat.standing().max(humanoid.indu().clas(), humanoid.race());
		int statBarWidth = (int) (200 * stat.standing().normalized(humanoid.indu().clas(), humanoid.race()));

		if (statBarWidth > 0) {
			if (statBarWidth < 20)
				statBarWidth = 20;
			GMeter.render(render, GMeter.C_REDGREEN, now / max, body().x1(), body().x1() + statBarWidth, body().y1()+3, body().y2()-3);
		}
	}

	@Override
	public void hoverInfoGet(GUI_BOX text) {
		text.title(stat.info().name);
		text.text(stat.info().desc);
		text.NL();
		GBox b = (GBox) text;
		if (stat.indu().max(humanoid.indu()) == 1) {
			b.add(GFORMAT.bool(b.text(), stat.indu().get(humanoid.indu()) == 1));
		}else if (stat.info().isInt()) {
			b.add(GFORMAT.i(b.text(), stat.indu().get(humanoid.indu())));
		}else {
			double d = stat.indu().getD(humanoid.indu());
			b.add(GFORMAT.perc(b.text(), d));
		}

		text.NL(8);
		StatRow.hoverStanding(text, stat, humanoid.indu());


	}
}