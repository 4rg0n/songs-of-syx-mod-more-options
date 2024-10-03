package game.audio;

import lombok.Setter;
import snake2d.SoundStream;
import snake2d.util.sets.LIST;
import snake2d.util.sets.LISTE;
import util.keymap.MAPPED;

public final class Ambiance implements MAPPED{
	
	AmbianceUpdater.Channel channel;
	
	public final LIST<SoundStream> streams;
	double priority;
	// MODDED: made private
	private double gain;
	private final String key;
	private final int index;

	// MODDED
	@Setter
	private double gainLimit = 1.0;

	Ambiance(String key, LISTE<Ambiance> all, LIST<SoundStream> streams){
		this.streams = streams;
		index = all.add(this);
		this.key = key;
	}

	public double gain() {
		// MODDED: AmbianceChannel and AmbienceUpdated only read from here now
		return gain * gainLimit;
	}
	
	public Ambiance gainSet(double gain) {
		this.gain = gain;
		return this;
	}
	
	public double priority() {
		return priority;
	}
	
	public Ambiance prioritySet(double priority) {
		this.priority = priority;
		return this;
	}
	
	public Ambiance priorityInc(double priority) {
		this.priority += priority;
		return this;
	}
	
//	public void set(double gain) {
//		play(gain, gain);
//	}
//	
//	public void play(double priority, double gain) {
//		this.priority = priority;
//		this.gain = gain;
//	}
//	
//	public void increase(double priority, double gain) {
//		this.priority += priority;
//		this.gain = gain;
//	}

	@Override
	public int index() {
		return index;
	}

	@Override
	public String key() {
		return key;
	}
	
}