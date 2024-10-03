package game.audio;


import snake2d.SoundStream;
import snake2d.util.misc.CLAMP;
import snake2d.util.sets.ArrayList;
import snake2d.util.sets.Tree;
import view.main.VIEW;

public final class AmbianceUpdater {

	
	private final Channel[] channels = new Channel[] {new Channel(), new Channel(), new Channel(), new Channel(), new Channel()};
	
	private Tree<Ambiance> aSort;
	private final ArrayList<Channel> cfree = new ArrayList<>(channels.length);
	private final ArrayList<Ambiance> toPlay = new ArrayList<>(channels.length);
	private final Ambiances aaa;
	
	AmbianceUpdater(Ambiances aaa){
		this.aaa = aaa;
	}
	
	private double last = -100;
	
	double[] debugPrio;
	double[] debugGain;
	
	public void update() {
		
		double ds = (float) (VIEW.renderSecond()-last);
		if (ds < 0.1)
			return;
		last = VIEW.renderSecond();
		
		if (debugPrio != null) {
			for (Ambiance a : aaa.all()) {
				a.priority = debugPrio[a.index()];
				if (a.priority > 0) {
					a.gainSet(debugGain[a.index()]);
				}
			}
			debugPrio = null;
			debugGain = null;
		}
		if (aSort == null || aSort.size() != aaa.all().size()) {
			aSort = new Tree<Ambiance>(aaa.all().size()) {

				@Override
				protected boolean isGreaterThan(Ambiance current, Ambiance cmp) {
					return current.priority > cmp.priority;
				}
				
			};
		}
		
		aSort.clear();
		for (Ambiance a : aaa.all()) {
			if (a.priority > 0) {
				aSort.add(a);
			}
		}

		toPlay.clearSloppy();
		
		int ci = 0;
		while(aSort.hasMore()) {
			Ambiance a = aSort.pollGreatest();
			if (ci < channels.length) {
				if (a.channel == null)
					toPlay.add(a);
			}else {
				a.priority = 0;
			}
			ci++;
		}
		
		cfree.clearSloppy();
		for (Channel c : channels) {
			c.update(ds);
			if (c.current == null) {
				cfree.add(c);
			}
		}
		
		for (int i = 0; i < toPlay.size() && i < cfree.size(); i++)
			cfree.get(i).init(toPlay.get(i));
			
		
		
		
	}
	
	static final class Channel {
		
		private Ambiance current;
		private SoundStream stream;
		private double gain;
		
		public void update(double ds) {
			if (current == null)
				return;

			if (!stream.isPlaying()) {
				if (current.priority > 0) {
					init(current);	
				}else {
					current.channel = null;
					current = null;
				}
				return;
			}
			
			if (current.priority <= 0) {
				gain -= ds;
				if (gain <= 0) {
					gain = 0;
					stream.stop();
				}
			}else {
				// MODDED: made it read from gain()
				if (gain < current.gain()) {
					gain += ds;
					if (gain > current.gain())
						gain = current.gain();
				}else if (gain > current.gain()) {
					gain -= ds;
					if (gain < current.gain())
						gain = current.gain();
				}
			}
			
			gain = CLAMP.d(gain, 0, 1);
			stream.setGain(gain);
			
		}
		
		public void init(Ambiance c) {
			c.channel = this;
			current = c;
			stream = c.streams.rnd();
			stream.setLooping(false);
			gain = 0;
			stream.setGain(gain);
			stream.play();
		}
		
	}
	
	
}
