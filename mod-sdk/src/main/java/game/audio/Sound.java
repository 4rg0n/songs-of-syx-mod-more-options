package game.audio;

import game.GAME;
import lombok.Setter;
import snake2d.util.datatypes.RECTANGLE;
import snake2d.util.rnd.RND;
import snake2d.util.sets.LIST;
import view.main.VIEW;

public final class Sound {
	
	private static double playCount;
	public final LIST<SoundFile> all;

	// MODDED
	@Setter
	private double gainLimit = 1.0;
	
	Sound(LIST<SoundFile> all) {
		this.all = all;
	}

	public void rnd(RECTANGLE body) {
		rnd(body, (0.8f + RND.rFloat(0.2)));
	}
	
	public void rnd(RECTANGLE body,double gain) {
		rnd(body.cX(), body.cY(), gain);
	}
	
	public void rnd(int x, int y, double gain) {
		playCount += GAME.SPEED.speedI();
		if (playCount >= 1) {
			playCount -= 1;
		}else
			return;
		
		if (VIEW.world().isActive())
			return;

		// MODDED
		all.rnd().rnd(x, y, gain*AUDIO.mono().sGain*this.gainLimit);
	}
}