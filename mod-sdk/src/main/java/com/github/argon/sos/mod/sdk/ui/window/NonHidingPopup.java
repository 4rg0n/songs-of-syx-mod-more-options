package com.github.argon.sos.mod.sdk.ui.window;

import init.constant.C;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import snake2d.MButt;
import snake2d.Renderer;
import snake2d.util.datatypes.COORDINATE;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.clickable.CLICKABLE;
import snake2d.util.gui.renderable.RENDEROBJ;
import snake2d.util.misc.ACTION;
import util.gui.misc.GBox;
import util.gui.panel.GPanel;
import view.interrupter.InterManager;
import view.interrupter.Interrupter;
import view.main.VIEW;

/**
 * Used for showing any kind of ui as popup on the screen.
 * Will not close other popups when shown.
 */
public class NonHidingPopup {

	@Getter
	private final GuiSection section = new GuiSection();
	private final Inter interrupt = new Inter(section);
	@Nullable
	private final InterManager manager;
	private CLICKABLE trigger;

	/**
	 * Creates a new {@link NonHidingPopup}.
	 *
	 * @param manager interrupt manager
	 */
	public NonHidingPopup(@Nullable InterManager manager){
		this.manager = manager;
	}

	/**
	 * Shows a given ui element next to the trigger in a popup.
	 *
	 * @param uiElement to show
	 * @param trigger to display the popup with the element next to
	 */
	public void show(RENDEROBJ uiElement, CLICKABLE trigger) {
		show(uiElement, trigger, false);
	}

	/**
	 * Shows a given ui element next to the trigger in a popup.
	 *
	 * @param uiElement to show
	 * @param trigger to display the popup with the element next to
	 * @param centreAtMouse will display the popup at the mouse pointer
	 */
	public void show(RENDEROBJ uiElement, CLICKABLE trigger, boolean centreAtMouse) {
		this.section.clear();
		this.section.add(uiElement);
		this.trigger = trigger;

		showPopup(trigger.body().cX(), trigger.body().cY(), centreAtMouse);
	}

	/**
	 * Closes the popup.
	 */
	public void close() {
		interrupt.hide();
	}

	/**
	 * Tells whether the popup is currently shown.
	 *
	 * @return whether the popup is currently shown
	 */
	public boolean showing() {
		return interrupt.isActivated();
	}
	
	private void showPopup(int x, int y, boolean centre) {
		
		int M = C.SG*32;
		
		if (centre) {
			section.body().moveC(VIEW.mouse());
			if (manager != null && !interrupt.isActivated()) {
				manager.add(interrupt);
			}
		}else {
			section.body().moveCX(x);
			if (y > C.HEIGHT() / 2){
				section.body().moveY2(y - M);
			}else {
				section.body().moveY1(y + M);
			}
		}

		if (section.body().x2() + M >= C.WIDTH()) {
			section.body().moveX2(C.WIDTH()-M);
		}
		
		if (section.body().x1() - M < 0) {
			section.body().moveX1(x + M);
		}
		
		if (section.body().y2() + M >= C.HEIGHT()) {
			section.body().moveY2(C.HEIGHT() - M);
		}
		
		if (section.body().y1() - M < 0) {
			section.body().moveY1(M);
		}
		
		if (manager != null && !interrupt.isActivated()) {
			manager.add(interrupt);
		}
	}

	private class Inter extends Interrupter {
		
		private final GPanel box;
		ACTION exit = new ACTION() {
			
			@Override
			public void exe() {
				hide();
			}
		};
		
		
		Inter(GuiSection s){
			box = new GPanel();
			box.setButt();
		}
		
		@Override
		protected void hoverTimer(GBox text) {
			section.hoverInfoGet(text);
		}

		@Override
		protected void mouseClick(MButt button) {
			if (button == MButt.RIGHT){
				hide();
			}else if(button == MButt.LEFT){
				if (!section.click())
					box.click();
			}
		}
		
		@Override
		public void hide() {
			super.hide();
		}

		@Override
		protected boolean otherClick(MButt butt) {
			hide();
			return butt == MButt.RIGHT;
		}

		@Override
		protected boolean hover(COORDINATE mCoo, boolean mouseHasMoved) {
			return section.hover(mCoo) || box.hover(mCoo);
		}

		@Override
		protected boolean render(Renderer r, float ds) {
			box.inner().set(section);
			box.clickActionSet(exit);
			box.render(r, ds);
			//box.moveExit(exit);
			section.render(r, ds);
			if (trigger != null) {
				trigger.selectTmp();
			}
			return true;
		}

		@Override
		protected boolean update(float ds) {
			return true;
		}
	}
}
