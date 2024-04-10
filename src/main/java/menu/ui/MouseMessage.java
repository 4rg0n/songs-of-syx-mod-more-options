package menu.ui;

import init.C;
import lombok.Setter;
import menu.Ui;
import snake2d.Renderer;
import snake2d.util.datatypes.COORDINATE;
import snake2d.util.datatypes.Coo;
import util.gui.misc.GBox;

public class MouseMessage {
	
	private final GBox timed = new GBox();
	private final GBox normal = new GBox();
	private double timer = 0;
	private final Coo clickCoo = new Coo();
	@Setter
	private int distance = 0;

	public void render(Renderer r, float ds) {
		
		GBox box = this.timed;
		
		if (timer < 0) {
			timed.clear();
			box = normal;
		}
		
	
		
		timer -= ds;
		
		if (!box.emptyIs()) {
			COORDINATE mCoo = Ui.getInstance().getMouseCoo();
			
			int M = 48*C.SG + distance;
			
			int y1 = mCoo.y() + M;
			if (mCoo.y()+M+box.height() > C.HEIGHT()) {
				y1 = mCoo.y()-M-box.height();
			}
			
			int x1 = mCoo.x() - box.width()/2;
			if (x1 < M) {
				x1 = M;
			}else if(x1 + box.width() + M > C.WIDTH()) {
				x1 = C.WIDTH()- box.width() - M;
			}
			
			if (y1 < M) {
				y1 = M;
				x1 = mCoo.x() <= C.DIM().cX() ? mCoo.x()+M : mCoo.x()-M-box.width(); 
			}else if(y1 + box.height() > C.DIM().height()) {
				y1 = C.DIM().height() - box.height() - M;
				x1 = mCoo.x() <= C.DIM().cX() ? mCoo.x()+M : mCoo.x()-M-box.width(); 
			}
			
			box.render(r, x1, y1);
		}
		
		normal.clear();
		distance = 0;
	}
	
	public GBox get() {
		if (timer == 0)
			return timed;
		return normal;
	}
	
	public void update(COORDINATE mCoo) {
		if (mCoo.tileDistanceTo(clickCoo) > 10)
			timer = -1;
	}

	public boolean isOn() {
		return timer >= 0 && !timed.emptyIs();
	}

	public boolean close() {
		if (timer > 0) {
			timer = 0;
			return true;
		}
		return false;
	}
}
