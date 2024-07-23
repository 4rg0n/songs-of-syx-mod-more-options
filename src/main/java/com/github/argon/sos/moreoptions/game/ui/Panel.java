package com.github.argon.sos.moreoptions.game.ui;

import init.C;
import init.sprite.UI.UI;
import init.sprite.UI.UIPanels.TitleBox;
import init.sprite.UI.UIPanels.UIPanel;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.datatypes.*;
import snake2d.util.gui.clickable.CLICKABLE;
import snake2d.util.misc.ACTION;
import snake2d.util.sprite.text.Font;
import util.dic.Dic;
import util.gui.misc.GText;

public class Panel extends CLICKABLE.ClickableAbs {

	private final static int MW = 8 * C.SG;
	private final GText title = new GText(UI.FONT().H2, 32).lablify();

	private UIPanel panel = UI.PANEL().thin;
	private boolean closeH;

	@Setter
	@Nullable
	private COLOR titleBackground;

	private final RecFacade outer = new RecFacade() {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public int width() {
			return body.width() + MW * 2;
		}

		@Override
		public int height() {
			return body.height() + MW * 2 + titleHeight() / 2;
		}

		@Override
		public int y1() {
			return body.y1() - MW - titleHeight() / 2;
		}

		@Override
		public int x1() {
			return body.x1() - MW;
		}

		@Override
		public RECTANGLEE moveY1(double Y1) {
			body.moveY1(Y1 + MW + titleHeight() / 2);
			return this;
		}

		@Override
		public RECTANGLEE moveX1(double X1) {
			body.moveX1(X1 + MW);
			return this;
		}

		@Override
		public RecFacade setWidth(double width) {
			body.setWidth(width - MW * 2);
			return this;
		}

		@Override
		public RecFacade setHeight(double height) {
			body.setHeight(height - MW * 2 - titleHeight() / 2);
			return this;
		}
	};

	public Panel() {

	}

	public Panel(RECTANGLE r) {
		body.set(r);
	}

	public Panel(int width, int height) {
		body.setDim(width, height);
	}

	public Panel set(RECTANGLE r) {
		body.set(r);
		return this;
	}
	
	public Panel setDim(int width, int height) {
		body.setWidth(width).setHeight(height);
		return this;
	}

	public Panel setBig() {
		panel = UI.PANEL().big;
		return this;
	}

	public Panel setButt() {
		panel = UI.PANEL().butt;
		return this;
	}

	@Override
	public RecFacade body() {
		return outer;
	}

	public Rec inner() {
		return body;
	}

	private int titleHeight() {
		if (title.length() == 0)
			return 0;
		return UI.PANEL().titleBox(title.getFont().height()).height;
	}

	public void setTitle(CharSequence title) {
		this.title.clear().set(title);
	}

	public void setTitle(CharSequence title, Font f) {
		this.title.setFont(f);
		this.title.clear().set(title);
	}

	public GText title() {
		return title;
	}

	public Panel setCloseAction(ACTION action) {
		this.clickAction = action;
		hoverInfoSet(Dic.¤¤Close);
		return this;
	}

	@Override
	public boolean hover(COORDINATE mCoo) {
		closeH = false;
		if (clickAction != null) {
			int cy = outer.y1() - panel.margin + panel.tMid;
			int cx = outer.x2() + panel.margin - panel.tMid;
			closeH = mCoo.tileDistanceTo(cx, cy) < 16;
		}
		this.isHovered = closeH;
		return closeH;
	}

	@Override
	protected void render(SPRITE_RENDERER r, float ds, boolean isActive, boolean isSelected, boolean isHovered) {
		// COLOR.YELLOW100.render(r, outer);
		panel.render(r, outer, 0);

		renderTitle(r);

		if (clickAction != null) {

			int cy = outer.y1() - panel.margin + panel.tMid;
			int cx = outer.x2() + panel.margin - panel.tMid;
			UI.PANEL().panelClose.renderC(r, closeH ? 1 : 0, cx, cy);
		}

		// COLOR.RED100.render(r, body);

	}

	public void renderTitle(SPRITE_RENDERER r) {
		if (title.length() != 0) {

			TitleBox b = UI.PANEL().titleBox(title.getFont().height());
//			int w = body.width() - b.height * 2;
//			if (w <= 0)
//				return;
			title.setMultipleLines(false);
			int cy = outer.y1() - panel.margin + panel.tMid;
			int y1 = cy - b.height / 2;
			int x1 = body.cX() - title.width() / 2;
			int y2 = cy - title.height() / 2;

			b.render(r, x1, y1, title.width());
			if (titleBackground != null) {
				titleBackground.render(r, x1, y2, title.width(), title.height(), 0);
			}

			title.render(r, x1, y2);

		}
	}

}
