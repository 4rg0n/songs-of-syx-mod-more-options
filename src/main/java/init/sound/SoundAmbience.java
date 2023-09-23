package init.sound;

import init.paths.PATH;
import init.paths.PATHS;
import lombok.Setter;
import snake2d.CORE;
import snake2d.SoundStream;
import snake2d.util.misc.CLAMP;
import snake2d.util.rnd.RND;
import snake2d.util.sets.ArrayList;
import snake2d.util.sets.LinkedList;
import snake2d.util.sets.Tree;
import view.main.VIEW;

public final class SoundAmbience {

	{
		Ambience.all = new LinkedList<>();
	}
	public final Ambience nature = new Ambience("nature");
	public final Ambience wind = new Ambience("wind");
	public final Ambience night = new Ambience("night");
	public final Ambience water = new Ambience("water");
	public final Ambience rain = new Ambience("rain");
	public final Ambience windTrees = new Ambience("windtrees");
	public final Ambience windhowl = new Ambience("windhowl");
	public final Ambience thunder = new Ambience("thunder");
	
	private final Channel[] channels = new Channel[] {new Channel(), new Channel(), new Channel(), new Channel()};
	
	private final Tree<Ambience> aSort = new Tree<Ambience>(Ambience.all.size()*16) {

		@Override
		protected boolean isGreaterThan(Ambience current, Ambience cmp) {
			return current.priority > cmp.priority;
		}
		
	};
	private final ArrayList<Channel> cfree = new ArrayList<>(channels.length);
	
	SoundAmbience(){
		
	}
	private double last = VIEW.renderSecond();
	
	public void update(float ds) {
		
		ds = (float) (VIEW.renderSecond()-last);
		last = VIEW.renderSecond();
		for (Ambience a : Ambience.all) {
			a.channel = null;
		}
		
		cfree.clear();
		
		for (Channel c : channels) {
			
			c.update(ds);
			if (c.current == null)
				cfree.add(c);
			else if (c.current.playFull) {
				c.current.channel = null;
				c.play = true;
			}else {
				c.current.channel = c;
				c.play = false;
			}
		}
		
		
		aSort.clear();
		
		for (Ambience a : Ambience.all) {
			if (a.priority > 0) {
				aSort.add(a);
			}
		}
		
		while(aSort.hasMore()) {
			
	
			Ambience a = aSort.pollGreatest();
		
			
			if (a.channel != null) {
				a.channel.play = true;
			}else if(!cfree.isEmpty()) {
				Channel c = cfree.removeLast();
				c.init(a);
				a.playExtra -= 1;
			}else {
				break;
			}
		}

		for (Ambience a : Ambience.all) {
			
			a.playExtra -= 0.25*ds;
			if (a.playExtra <= 0) {
				a.priority = 0;
				a.playExtra = 0;
			}
		}
		
	}
	
	
	public static final class Ambience {
		
		private Channel channel;
		
		private static LinkedList<Ambience> all;
		private int ri = 0;
		private final SoundStream[] streams;
		private double priority;
		private double gain;

		@Setter
		private int limiter;

		private boolean playFull;
		private double playExtra;

		private boolean enabled = true;

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public boolean isEnabled() {
			return enabled;
		}

		private Ambience(String key){
			PATH f = PATHS.SOUND().ambience.getFolder(key);
			String[] files = f.getFiles();
			streams = new SoundStream[files.length];
			for (int i = 0; i < files.length; i++) {
				streams[i] = CORE.getSoundCore().getStream(f.get(files[i]), false);
			}
			all.add(this);
		}
		
		public void play(double gain) {
			play(gain, gain);
		}
		
		public void play(double priority, double gain) {
			playFull = false;
			playExtra = 0;

			if (limiter == 0) {
				gain = 0;
			} else if (limiter == 100) {
				gain = gain * 100 / 0.0001f;
			} else {
				gain = gain * 100 / (100 - limiter);
			}

			this.priority = priority*gain;
			this.gain = gain;
		}
		
		public void playAnother(double gain) {
			playAnother(gain, gain);
		}
		
		public void playAnother(double priority, double gain) {
			playFull = true;
			playExtra ++;
			playExtra = CLAMP.d(playExtra, 0, 1);

			if (limiter == 0) {
				gain = 0;
			} else if (limiter == 100) {
				gain = gain * 100 / 0.0001f;
			} else {
				gain = gain * 100 / (100 - limiter);
			}

			this.priority = priority;
			this.gain = gain;
		}
		
	}
	
	private static class Channel {
		
		private Ambience current;
		private SoundStream stream;
		private double gain;
		private boolean play;
		
		private void update(double ds) {
			if (current == null)
				return;
			if (!current.isEnabled()) {
				return;
			}

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
			
			if (gain < current.gain) {
				gain += ds*2;
				if (gain > current.gain)
					gain = current.gain;
			}else if (gain > current.gain) {
				gain -= ds*2;
				if (gain < current.gain)
					gain = current.gain;
			}
			gain = CLAMP.d(gain, 0, 1);
			stream.setGain(gain);
			
		}
		
		private void init(Ambience c) {
			current = c;
			int ri = 0;
			if (c.streams.length > 1) {
				ri = RND.rInt(c.streams.length);
				if (ri == c.ri)
					ri = (ri+1)%c.streams.length;
			}
			c.ri = ri;
			stream = c.streams[c.ri];
			stream.setLooping(false);
			gain = 0;
			stream.setGain(gain);
			stream.play();
			play = true;
		}
		
	}
	
	
}
