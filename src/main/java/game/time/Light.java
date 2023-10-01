package game.time;

import lombok.Getter;
import lombok.experimental.Accessors;
import snake2d.CORE;
import snake2d.util.color.RGB;
import snake2d.util.datatypes.RECTANGLE;
import snake2d.util.light.AmbientLight;

public final class Light {

	private boolean isDay = false;
	private boolean isNight = false;

	private double secondsTotal;
	private double secondsPassed;
	private double secondsLeft;
	private double partOf;
	private double partOfCircular;
	private double secondsDay;

	@Getter
	@Accessors(fluent = true)
	private final LightSun sun = new LightSun();
	@Getter
	@Accessors(fluent = true)
	private final LightMoon moon = new LightMoon();
	public final LightShadows shadow = new LightShadows();
	private final AmbientLight room = new AmbientLight(0.7, 0.5, 0.3, 0, 20);
	private final Gui gui = new Gui();
	
	Light(){
		
	}
	
	public void bindRoom() {
		room.setTilt(20);
		room.setDir(180);
		double roomI = 1.0;
		if (dayIs()) {
			roomI = 1.0-partOfCircular();
			roomI *= roomI;
		}
		room.r(0.6*roomI);
		room.g(0.3*roomI);
		room.b(0.1*roomI);
		CORE.renderer().lightDepthSet((byte) 127);
		CORE.renderer().setTileLight(room);
	}
	
//	public void applyWorld(int x1, int x2, int y1, int y2, double season) {
//		moon.apply(x1, x2, y1, y2);
//		sun.apply(x1, x2, y1, y2, season);
//		
//	}
//	
//	public void applyWorld(RECTANGLE rec, double season) {
//		applyWorld(rec.x1(), rec.x2(), rec.y1(), rec.y2(), season);
//	}
	
	public void apply(RECTANGLE rec, RGB mask) {
		apply(rec.x1(), rec.x2(), rec.y1(), rec.y2(), mask);
	}
	
	public void apply(int x1, int x2, int y1, int y2, RGB mask) {
		moon.apply(x1, x2, y1, y2);
		sun.apply(x1, x2, y1, y2, mask);
	}
	
	public void applyGuiLight(float ds, RECTANGLE rec) {
		gui.register(ds, rec);
	}
	
	public void applyGuiLight(float ds, int x1, int x2, int y1, int y2) {
		gui.register(ds, x1, x2, y1, y2);
	}
	
	public boolean dayIs() {
		return isDay;
	}
	
	public boolean nightIs() {
		return isNight;
	}
	
	public double partOf() {
		return partOf;
	}
	
	public double partOfCircular() {
		return partOfCircular;
	}
	
	public double secondOf() {
		return secondsPassed;
	}
	
	public double secondsLeft() {
		return secondsLeft;
	}
	
	public double secondsTotal() {
		return secondsTotal;
	}
	
	public double secondsDay() {
		return secondsDay;
	}
	
	void update(double ds) {

		
		double ratio = TIME.seasons().currentDay.dayLength();
		double dawn = (1.0-ratio)/2;
		double dusk = dawn + ratio;
		double now = TIME.days().bitPartOf();
		
		secondsDay = (dusk-dawn)*TIME.secondsPerDay;
		
		isDay = now > dawn && now < dusk;
		isNight = !isDay;
		
		if (isDay) {
			double total = dusk-dawn;
			secondsTotal = TIME.secondsPerDay*total;
			partOf = (now-dawn)/total;
		}else if (now <= dawn){
			double prev = (1.0 - TIME.seasons().previousDay.dayLength())/2;
			double total = dawn + prev;
			secondsTotal = TIME.secondsPerDay*total;
			partOf = (prev+now)/total;
		}else {
			double next = (1.0 - TIME.seasons().nextDay.dayLength())/2;
			double total = (1.0-dusk) + next;
			secondsTotal = TIME.secondsPerDay*total;
			partOf = (now-dusk)/total;
		}
		
		if (partOf > 0.5) {
			partOfCircular = 1.0 - (partOf-0.5)*2;
		}else {
			partOfCircular = partOf*2;
		}
		secondsPassed = partOf *secondsTotal;
		secondsLeft = secondsTotal - secondsPassed;
		shadow.update(this);
		gui.update(this, ds);

	}
	
}
