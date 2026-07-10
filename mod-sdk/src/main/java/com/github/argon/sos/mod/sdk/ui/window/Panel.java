package com.github.argon.sos.mod.sdk.ui.window;

import init.constant.C;
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
import util.text.Dic;
import util.gui.misc.GText;

import java.io.Serial;

/**
 * A box to display content with a title at the top.
 */
public class Panel extends CLICKABLE.ClickableAbs {

	private final static int MW = 8 * C.SG;
	private final GText title = new GText(UI.FONT().H2, 32).lablify();

	private UIPanel panel = UI.PANEL().thin;
	private boolean closeH;

	@Setter
	@Nullable
	private COLOR titleBackground;

	private final RecFacade outerFrame = new RecFacade() {

		@Serial
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
			body.moveY1(Y1 + MW + (double) titleHeight() / 2);
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
			body.setHeight(height - MW * 2 - (double) titleHeight() / 2);
			return this;
		}
	};

	/**
	 * Creates a new {@link Panel}.
	 */
	public Panel() {
	}

	/**
	 * Creates a new {@link Panel} with given {@link RECTANGLE} for size and position.
	 *
	 * @param rectangle for size and position
	 */
	public Panel(RECTANGLE rectangle) {
		set(rectangle);
	}

	/**
	 * Creates a new {@link Panel} with given width and height for size.
	 *
	 * @param width of the panel
	 * @param height of the panel
	 */
	public Panel(int width, int height) {
		setSize(width, height);
	}

	/**
	 * Sets panel to the {@link RECTANGLE} size and position.
	 *
	 * @param rectangle for size and position
	 * @return this
	 */
	public Panel set(RECTANGLE rectangle) {
		body.set(rectangle);
		return this;
	}

	/**
	 * Sets panel height and width.
	 *
	 * @param width of the panel
	 * @param height of the panel
	 * @return this
	 */
	public Panel setSize(int width, int height) {
		body.setDim(width, height);
		return this;
	}

	/**
	 * Will make it a "big" panel.
	 *
	 * @return this
	 */
	public Panel setBig() {
		panel = UI.PANEL().big;
		return this;
	}

	/**
	 * Will make the panel the size of a button.
	 *
	 * @return this
	 */
	public Panel setButton() {
		panel = UI.PANEL().butt;
		return this;
	}

	/**
	 * Returns the outer frame as {@link RecFacade} of the panel.
	 *
	 * @return body
	 */
	@Override
	public RecFacade body() {
		return outerFrame;
	}

	/**
	 * Returns the inner part as {@link Rec} of the panel.
	 *
	 * @return body
	 */
	public Rec inner() {
		return body;
	}

	private int titleHeight() {
		if (title.isEmpty())
			return 0;
		return UI.PANEL().titleBox(title.getFont().height()).height;
	}

	/**
	 * Sets the title at the top for the panel.
	 *
	 * @param title to set
	 */
	public void setTitle(CharSequence title) {
		this.title.clear().set(title);
	}

	/**
	 * Sets the title with given font at the top for the panel.
	 *
	 * @param title to set
	 * @param font to use
	 */
	public void setTitle(CharSequence title, Font font) {
		this.title.setFont(font);
		this.title.clear().set(title);
	}

	/**
	 * Returns the title.
	 *
	 * @return title
	 */
	public GText title() {
		return title;
	}

	/**
	 * Sets an action, which is executed when the panel is closed.
	 *
	 * @param action to set
	 * @return this
	 */
	public Panel setCloseAction(ACTION action) {
		this.clickAction = action;
		hoverInfoSet(Dic.¤¤Close);
		return this;
	}

	/**
	 * Executed when the panel is hovered.
	 *
	 * @param mouseCoordinates of the mouse pointer
	 * @return whether the panel is hover-able
	 */
	@Override
	public boolean hover(COORDINATE mouseCoordinates) {
		closeH = false;
		if (clickAction != null) {
			int cy = outerFrame.y1() - panel.margin + panel.tMid;
			int cx = outerFrame.x2() + panel.margin - panel.tMid;
			closeH = mouseCoordinates.tileDistanceTo(cx, cy) < 16;
		}
		this.isHovered = closeH;
		return closeH;
	}

	/**
	 * Executed when the panel is rendered.
	 *
	 * @param renderer to use
	 * @param deltaSeconds since last render loop
	 * @param isActive whether the panel is active
	 * @param isSelected whether the panel is selected
	 * @param isHovered whether the panel is hovered
	 */
	@Override
	protected void render(SPRITE_RENDERER renderer, float deltaSeconds, boolean isActive, boolean isSelected, boolean isHovered) {
		panel.render(renderer, outerFrame, 0);
		renderTitle(renderer);

		if (clickAction != null) {
			int cy = outerFrame.y1() - panel.margin + panel.tMid;
			int cx = outerFrame.x2() + panel.margin - panel.tMid;
			UI.PANEL().panelClose.renderC(renderer, closeH ? 1 : 0, cx, cy);
		}
	}

	private void renderTitle(SPRITE_RENDERER r) {
		if (!title.isEmpty()) {
			TitleBox b = UI.PANEL().titleBox(title.getFont().height());
			title.setMultipleLines(false);
			int cy = outerFrame.y1() - panel.margin + panel.tMid;
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
