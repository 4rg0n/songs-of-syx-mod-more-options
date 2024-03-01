package com.github.argon.sos.moreoptions.game.ui;

import init.C;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import snake2d.MButt;
import snake2d.Renderer;
import snake2d.util.color.COLOR;
import snake2d.util.datatypes.COORDINATE;
import snake2d.util.gui.GuiSection;
import snake2d.util.misc.ACTION;
import util.gui.misc.GBox;
import view.interrupter.InterManager;
import view.interrupter.Interrupter;

public final class NotificationPopup {
	
	private final GuiSection displayContainer = new GuiSection();
	private final Inter inter = new Inter(displayContainer);
	private final InterManager manager;

	private Notification notification;

	public NotificationPopup(InterManager manager){
		this.manager = manager;
	}

	public void show(Notification notification, int x, int y) {
		this.displayContainer.clear();
		this.displayContainer.add(notification);
		this.notification = notification;

		showP(x, y, notification.getTitle(), notification.getTitleBackground());
	}

	public GuiSection section() {
		return displayContainer;
	}
	
	public void close() {
		inter.hide();
	}

	public void closeSilent() {
		inter.hideSilent();
	}
	
	
	public boolean showing() {
		return inter.isActivated();
	}
	
	private void showP(int x, int y, @Nullable String title, @Nullable COLOR titleBackground) {

		int M = C.SG*32;

		if (title == null) {
			inter.clearTitle();
		} else {
			inter.setTitle(title);
		}

		inter.getBox().setTitleBackground(titleBackground);

		displayContainer.body().moveCX(x);
		if (y > C.HEIGHT()/2){
			displayContainer.body().moveY2(y-M);
		}else {
			displayContainer.body().moveY1(y+M);
		}


		if (displayContainer.body().x2()+M >= C.WIDTH()) {
			displayContainer.body().moveX2(C.WIDTH()-M);
		}
		
		if (displayContainer.body().x1() - M < 0) {
			displayContainer.body().moveX1(x+M);
		}
		
		if (displayContainer.body().y2()+M >= C.HEIGHT()) {
			displayContainer.body().moveY2(C.HEIGHT()-M);
		}
		
		if (displayContainer.body().y1() - M < 0) {
			displayContainer.body().moveY1(M);
		}
		
		if (!inter.isActivated()) {
			manager.add(inter);
		}
	}

	private class Inter extends Interrupter {

		@Getter
		private final Panel box;
		ACTION exit = this::hide;
		Inter(GuiSection s){
			box = new Panel();
			box.setButt();
		}

		public void setTitle(CharSequence title) {
			box.setTitle(title);
		}

		public void clearTitle() {
			box.title().clear();
		}

		@Override
		protected void hoverTimer(GBox text) {
			displayContainer.hoverInfoGet(text);
		}

		@Override
		protected void mouseClick(MButt button) {
			if(button == MButt.LEFT){
				if (!displayContainer.click())
					box.click();
			}
		}
		
		@Override
		public void hide() {
			if (notification != null) notification.hide();
			// TODO Auto-generated method stub
			super.hide();
		}

		public void hideSilent() {
			super.hide();
		}

		@Override
		protected boolean hover(COORDINATE mCoo, boolean mouseHasMoved) {
			return displayContainer.hover(mCoo) || box.hover(mCoo);
		}

		@Override
		protected boolean render(Renderer r, float ds) {
			box.inner().set(displayContainer);
			box.clickActionSet(exit);

			box.render(r, ds);
			displayContainer.render(r, ds);

			return true;
		}

		@Override
		protected boolean update(float ds) {
			return true;
		}
	}

}
