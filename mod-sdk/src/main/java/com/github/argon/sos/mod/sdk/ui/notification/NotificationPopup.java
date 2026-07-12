package com.github.argon.sos.mod.sdk.ui.notification;

import com.github.argon.sos.mod.sdk.ui.window.Panel;
import init.constant.C;
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

/**
 * Used for showing {@link Notification}s as popup on the screen.
 */
public final class NotificationPopup {

	@Getter
	private final GuiSection section = new GuiSection();
	private final Inter interrupter = new Inter(section);
	private final InterManager manager;

	private Notification notification;

	/**
	 * Creates a new {@link NotificationPopup} with given interrupt manager.
	 *
	 * @param manager interrupt manager
	 */
	public NotificationPopup(InterManager manager){
		this.manager = manager;
	}

	/**
	 * Shows a notification popup with given {@link Notification} as message.
	 *
	 * @param notification to show
	 * @param x coordinate of the notification
	 * @param y coordinate of the notification
	 */
	public void show(Notification notification, int x, int y) {
		this.section.clear();
		this.section.add(notification);
		this.notification = notification;

		showP(x, y, notification.getTitle(), notification.getTitleBackground());
	}

	/**
	 * Closes the current displayed notification.
	 */
	public void close() {
		interrupter.hide();
	}

	/**
	 * Closes the interrupter without closing the notification itself.
	 */
	public void closeSilent() {
		interrupter.hideSilent();
	}

	/**
	 * Whether a notification is currently shown.
	 *
	 * @return whether a notification is currently shown
	 */
	public boolean showing() {
		return interrupter.isActivated();
	}
	
	private void showP(int x, int y, @Nullable String title, @Nullable COLOR titleBackground) {

		int M = C.SG*32;

		if (title == null) {
			interrupter.clearTitle();
		} else {
			interrupter.setTitle(title);
		}

		interrupter.getBox().setTitleBackground(titleBackground);

		section.body().moveCX(x);
		if (y > C.HEIGHT()/2){
			section.body().moveY2(y-M);
		}else {
			section.body().moveY1(y+M);
		}


		if (section.body().x2()+M >= C.WIDTH()) {
			section.body().moveX2(C.WIDTH()-M);
		}
		
		if (section.body().x1() - M < 0) {
			section.body().moveX1(x+M);
		}
		
		if (section.body().y2()+M >= C.HEIGHT()) {
			section.body().moveY2(C.HEIGHT()-M);
		}
		
		if (section.body().y1() - M < 0) {
			section.body().moveY1(M);
		}
		
		if (!interrupter.isActivated()) {
			manager.add(interrupter);
		}
	}

	private class Inter extends Interrupter {

		@Getter
		private final Panel box;
		ACTION exit = this::hide;
		Inter(GuiSection s){
			box = new Panel();
			box.setButton();
		}

		public void setTitle(CharSequence title) {
			box.setTitle(title);
		}

		public void clearTitle() {
			box.title().clear();
		}

		@Override
		protected void hoverTimer(GBox text) {
			section.hoverInfoGet(text);
		}

		@Override
		protected void mouseClick(MButt button) {
			if(button == MButt.LEFT){
				if (!section.click())
					box.click();
			}
		}
		
		@Override
		public void hide() {
			if (notification != null) notification.hide();
			super.hide();
		}

		public void hideSilent() {
			super.hide();
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
			section.render(r, ds);

			return true;
		}

		@Override
		protected boolean update(float ds) {
			return true;
		}
	}

}
