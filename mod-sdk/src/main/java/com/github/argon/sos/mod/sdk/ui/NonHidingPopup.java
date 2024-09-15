package com.github.argon.sos.mod.sdk.ui;

import init.C;
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
	
	private final GuiSection s = new GuiSection();
	private final Inter inter = new Inter(s);
	@Nullable
	private final InterManager manager;
	private CLICKABLE trigger;

	public NonHidingPopup() {
		this(null);
	}
	
	public NonHidingPopup(@Nullable InterManager manager){
		this.manager = manager;
	}

	
	public void show(RENDEROBJ s, CLICKABLE trigger) {
		show(s, trigger, false);
	}
	
	public void show(RENDEROBJ s, CLICKABLE trigger, boolean centreAtMouse) {
		this.s.clear();
		this.s.add(s);
		this.trigger = trigger;
		showP(trigger.body().cX(), trigger.body().cY(), centreAtMouse);
		
	}
	
	public GuiSection section() {
		return s;
	}
	
	public void close() {
		inter.hide();
	}
	
	
	public boolean showing() {
		return inter.isActivated();
	}
	
	protected void showP(int x, int y, boolean centre) {
		
		int M = C.SG*32;
		
		if (centre) {
			s.body().moveC(VIEW.mouse());
			if (manager != null && !inter.isActivated()) {
				manager.add(inter);
			}
		}else {
			s.body().moveCX(x);
			if (y > C.HEIGHT()/2){
				s.body().moveY2(y-M);
			}else {
				s.body().moveY1(y+M);
			}
		}

		if (s.body().x2()+M >= C.WIDTH()) {
			s.body().moveX2(C.WIDTH()-M);
		}
		
		if (s.body().x1() - M < 0) {
			s.body().moveX1(x+M);
		}
		
		if (s.body().y2()+M >= C.HEIGHT()) {
			s.body().moveY2(C.HEIGHT()-M);
		}
		
		if (s.body().y1() - M < 0) {
			s.body().moveY1(M);
		}
		
		if (manager != null && !inter.isActivated()) {
			manager.add(inter);
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
			s.hoverInfoGet(text);
		}

		@Override
		protected void mouseClick(MButt button) {
			if (button == MButt.RIGHT){
				hide();
			}else if(button == MButt.LEFT){
				if (!s.click())
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
			if (butt == MButt.RIGHT)
				return true;
			return false;
		}

		@Override
		protected boolean hover(COORDINATE mCoo, boolean mouseHasMoved) {
			return s.hover(mCoo) || box.hover(mCoo);
		}

		@Override
		protected boolean render(Renderer r, float ds) {
			box.inner().set(s);
			box.clickActionSet(exit);
			box.render(r, ds);
			//box.moveExit(exit);
			s.render(r, ds);
			if (trigger != null) {
				trigger.selectTmp();
			}
			return true;
		}

		@Override
		protected boolean update(float ds) {
//			if (KEY.anyPressed()) {
//				hide();
//			}
			return true;
		}
	}

}
