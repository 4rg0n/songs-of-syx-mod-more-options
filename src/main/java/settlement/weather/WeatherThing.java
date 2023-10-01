package settlement.weather;

import lombok.Setter;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;
import snake2d.util.misc.CLAMP;
import snake2d.util.sets.LinkedList;
import util.data.DOUBLE.DOUBLE_MUTABLE;
import util.info.INFO;

import java.io.IOException;

public class WeatherThing implements DOUBLE_MUTABLE{

	static LinkedList<WeatherThing> all;
	private double d;
	public final INFO info;
	@Setter
	private double amountLimit = 1.0d;
	
	WeatherThing(CharSequence name, CharSequence desc){
		this.info = new INFO(name, desc);
		all.add(this);
	}
	
	@Override
	public double getD() {
		return d * amountLimit;
	}

	@Override
	public DOUBLE_MUTABLE setD(double d) {
		this.d = CLAMP.d(d, 0, 1);
		return this;
	}
	
	@Override
	public INFO info() {
		return info;
	}

	protected void save(FilePutter file) {
		file.d(d);
	}

	protected void load(FileGetter file) throws IOException {
		d = file.d();
	}
	
	protected void init() {
		
	}
	
	void update(double ds) {
		
	};
	

	
	protected double adjustTowards(double current, double speed, double target) {
		if (current >= target) {
			current -= speed;
			if (current < target)
				current = target;
		}else if(current < target) {
			current += speed;
			if (current > target)
				current = target;
		}
		return current;
	}
	
}
