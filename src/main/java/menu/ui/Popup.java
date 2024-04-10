package menu.ui;

import init.C;
import org.jetbrains.annotations.Nullable;
import snake2d.SPRITE_RENDERER;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.clickable.CLICKABLE;
import snake2d.util.gui.renderable.RENDEROBJ;
import util.gui.panel.GPanel;
import view.main.VIEW;

public class Popup extends GuiSection {
	
	private final GuiSection section = new GuiSection();

	protected final GPanel panel = new GPanel();

	@Nullable
	private CLICKABLE trigger;

	public Popup() {
		panel.setButt();
		panel.clickActionSet(this::close);
		add(panel);
		add(section);
		visableSet(false);
	}

	public void show(RENDEROBJ s, CLICKABLE trigger) {
		show(s, trigger, false);
	}
	
	public void show(RENDEROBJ s, CLICKABLE trigger, boolean centreAtMouse) {
		this.section.clear();
		this.section.add(s);
		this.trigger = trigger;
        showP(trigger.body().cX(), trigger.body().cY(), centreAtMouse);
	}

	public void close() {
		visableSet(false);
		if (trigger != null) {
			trigger.selectedSet(false);
			trigger = null;
		}
	}

	@Override
	public void render(SPRITE_RENDERER r, float ds) {
		super.render(r, ds);
		if (trigger != null) {
			trigger.selectedSet(true);
		}
	}

	protected void showP(int x, int y, boolean centre) {
		int M = C.SG*32;
		if (centre) {
			section.body().moveC(VIEW.mouse());
		}else {
			section.body().moveCX(x);
			if (y > C.HEIGHT()/2){
				section.body().moveY2(y-M);
			}else {
				section.body().moveY1(y+M);
			}
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

		panel.inner().set(section);
		visableSet(true);
	}
}
