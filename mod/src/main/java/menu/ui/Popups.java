package menu.ui;

import snake2d.util.gui.GuiSection;
import snake2d.util.gui.clickable.CLICKABLE;
import snake2d.util.gui.renderable.RENDEROBJ;

import java.util.concurrent.CopyOnWriteArrayList;

public class Popups extends GuiSection {
	private final CopyOnWriteArrayList<PopupPanel> popups = new CopyOnWriteArrayList<>();

	public void show(RENDEROBJ render, CLICKABLE trigger) {
		newPanel().show(render, trigger);
	}

	public void close() {
		popups.forEach(this::destroyPanel);
	}

	public void close(CLICKABLE trigger) {
		popups.stream()
			.filter(popup -> popup.getTrigger() != null)
			.filter(popup -> popup.getTrigger().equals(trigger))
			.forEach(this::destroyPanel);
	}

	private PopupPanel newPanel() {
		PopupPanel popupPanel = new PopupPanel();
		clickActionSet(() -> {
			if(destroyPanel(popupPanel)) {
				clear();
				popups.forEach(popup -> {
					add(popup);
					popup.show();
				});
			};
		});
		popups.add(popupPanel);
		add(popupPanel);

		return popupPanel;
	}

	private boolean destroyPanel(PopupPanel popupPanel) {
		boolean removed = popups.remove(popupPanel);
		if (removed) {
			popupPanel.hide();
		}

		return removed;
	}
}
