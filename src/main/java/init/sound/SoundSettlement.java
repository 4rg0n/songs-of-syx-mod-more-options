package init.sound;

import game.GAME;
import init.paths.PATH;
import init.paths.PATHS;
import lombok.Setter;
import snake2d.CORE;
import snake2d.Errors;
import snake2d.SoundEffect;
import snake2d.util.datatypes.RECTANGLE;
import snake2d.util.rnd.RND;
import snake2d.util.sets.ArrayListResize;
import snake2d.util.sets.INDEXED;
import snake2d.util.sets.LIST;
import util.keymap.RCollection;

public class SoundSettlement {

	public final Action action = new Action();
	public final RCollection<Sound> animal =  new Animals();
	private double playCount = 0;
	private float ggain = 1.0f;
	
	SoundSettlement(){
		
	}
	
	public final class Action extends RCollection<Sound>{
		
		private final ArrayListResize<Sound> all = new ArrayListResize<>(20, 256);
		private PATH g = PATHS.SOUND().action;
		public final Sound sword = gett("_SWORD");
		public final Sound dig = gett("_DIG");
		public final Sound axe = gett("_AXE");
		public final Sound build = gett("_BUILD");
		public final Sound stone = gett("_STONE");
		public final Sound squish = gett("_SQUISH");
		public final Sound sleep = gett("_SLEEP");
		public final Sound decay = gett("_DECAY");
		public final Sound pain = gett("_PAIN");
		
		public Action() {
			super("SOUND");
			
			String[] files = g.folders();
			for (String f : files) {
				gett(f);
			}
			all.trim();
			
			map.expand();
		}
		
		private Sound gett(String name) {
			Sound s = new Sound(all.size(), name, g.getFolder(name));
			all.add(s);
			map.put(name, s);
			return s;
		}

		@Override
		public Sound getAt(int index) {
			return all.get(index);
		}

		@Override
		public LIST<Sound> all() {
			return all;
		}
		
	}
	
	private final class Animals extends RCollection<Sound>{
		
		private final ArrayListResize<Sound> all = new ArrayListResize<>(20, 256);
		
		public Animals() {
			super("SOUND");
			PATH g = PATHS.SOUND().animal;
			String[] files = g.folders();
			
			for (String f : files) {
				get(f, g.getFolder(f));
			}
			all.trim();
		}
		
		private Sound get(String name, PATH getter) {
			Sound s = new Sound(all.size(), name, getter);
			all.add(s);
			map.put(name, s);
			
			
			return s;
		}

		@Override
		public Sound getAt(int index) {
			return all.get(index);
		}

		@Override
		public LIST<Sound> all() {
			return all;
		}
		
	}
	
	public class Sound implements INDEXED{
		
		private final SoundEffect[] sounds;
		private final int index;
		public final String key;

		@Setter
		private int limiter;
		
		
		Sound(int index, String key, PATH path){
			String[] files = path.getFiles();
			sounds = new SoundEffect[files.length];
			if (sounds.length == 0)
				throw new Errors.DataError("There must be at least 1 sound file", path.get());
			for (int i = 0; i < files.length; i++) {
				sounds[i] = CORE.getSoundCore().getEffect(path.get(files[i]));
			}
			this.index = index;
			this.key = key;
		}
		
		public void rnd(RECTANGLE body) {
			rnd(body, 0.8f + RND.rFloat(0.2));
		}
		
		public void rnd(RECTANGLE body,double gain) {
			rnd(body.cX(), body.cY(), gain);
		}
		
		public void rnd(int x, int y, double gain) {
			if (ggain <= 0 || !shouldPlay())
				return;
			int i = RND.rInt(sounds.length);
			float pitch = RND.rFloat1(0.3);
			gain *= ggain;

			if (limiter == 0) {
				gain = 0;
			} else if (limiter == 100) {
				gain = gain * 100 / 0.0001f;
			} else {
				gain = gain * 100 / (100 - limiter);
			}
			
			sounds[i].play(x, y, pitch, (float)gain, false);
		}
		
		public SoundEffect get() {
			return sounds[RND.rInt(sounds.length)];
		}
		
		public SoundEffect get(int i) {
			return sounds[i];
		}
		
		public int size() {
			return sounds.length;
		}

		@Override
		public int index() {
			return index;
		}
		
	}
	
	public void set(double gain) {
		this.ggain = (float) gain;
		if (gain > 1 || gain < 0)
			GAME.Notify(""+gain);
	}
	
	private boolean shouldPlay() {
		playCount += GAME.SPEED.speedI();
		if (playCount >= 1) {
			playCount -= 1;
			return true;
		}
		return false;
	}
	
}
