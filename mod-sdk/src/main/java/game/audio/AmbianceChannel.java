package game.audio;

import snake2d.SoundStream;
import snake2d.util.misc.CLAMP;

final class AmbianceChannel {
	
	private Ambiance current;
	private SoundStream stream;
	private double gain;
	private boolean play;
	
	public void update(double ds) {
		if (current == null)
			return;
		if (!stream.isPlaying()) {
			current = null;
			return;
		}
		if (!play || current.priority <= 0) {
			gain -= ds*2;
			if (gain <= 0) {
				gain = 0;
				stream.stop();
				play = false;
			}
		}

		// MODDED: made it read from gain()
		if (gain < current.gain()) {
			gain += ds*2;
			if (gain > current.gain())
				gain = current.gain();
		}else if (gain > current.gain()) {
			gain -= ds*2;
			if (gain < current.gain())
				gain = current.gain();
		}
		gain = CLAMP.d(gain, 0, 1);
		stream.setGain(gain);
		
	}
	
	public void init(Ambiance c) {
		current = c;
		stream = c.streams.rnd();
		stream.setLooping(false);
		gain = 0;
		stream.setGain(gain);
		stream.play();
		play = true;
	}
	
}