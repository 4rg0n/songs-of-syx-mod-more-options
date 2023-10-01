package game.time;

import init.settings.S;
import lombok.Setter;
import snake2d.CORE;
import snake2d.util.color.RGB;
import snake2d.util.color.RGB.RGBImp;
import snake2d.util.light.AmbientLight;

public final class LightSun {
	
	static final double twilightSeconds = TIME.secondsPerDay*0.025;
	private final RGBImp black = new RGBImp().r(0.1).g(0.1).b(0.1);
	private final RGBImp dawnA = new RGBImp().r(1.7).g(0.7).b(0.4);
	private final RGBImp dusk = new RGBImp().r(1.7).g(0.5).b(0.3);
	private final RGBImp day = new RGBImp().r(1).g(1).b(1);
	private final RGBImp w = new RGBImp();
	private final RGBImp w2 = new RGBImp();
	
	private final AmbientLight work = new AmbientLight();
	private final AmbientLight work2 = new AmbientLight();
	private final RGBImp dayLight = new RGBImp().r(2.5).g(1.5).b(1.00);

	@Setter
	private boolean locked = false;

	@Setter
	private int cycleLock = 0;
	
	private double dawnEffect = 1.0;
	
	private final double minTilt = 10;
	private final double maxTilt = 45;
	private final double reflection = 0.3;
	
	public LightSun(){

	}
	
	public void apply(int x1, int x2, int y1, int y2, RGB tint) {
		
		double part = TIME.light().partOfCircular();
		if (TIME.light().nightIs())
			part = 0;
		
		
		
		double ref = reflection/6 + 5*reflection*part/6;
		dayLight.set(1.5, 1.5, 1.5);
		dayLight.multiply(tint);
		
		work.copy(TIME.seasons().currentDay).shade(ref);
		work.multiply(tint);
		work.setTilt(45).setDir(180-TIME.light().shadow.dir());
		work.register(x1, x2, y1, y2);
		
		if ((locked && cycleLock == 0) ^ (!locked && S.get().lightCycle.get() == 0)) {
			double i = 0.75;

			double tilt = minTilt+(maxTilt-minTilt)*0.5;
			day.copy(dayLight).shade(0.5);
			w.interpolate(dayLight, day, i);
			set(w, minTilt + tilt, x1, x2, y1, y2);
		}else if ((locked && cycleLock == 1) ^ (!locked && TIME.light().nightIs() && TIME.light().secondsLeft() < twilightSeconds)) {
			
			//predawn
			double i = (twilightSeconds-TIME.light().secondsLeft())/twilightSeconds;
			i*= dawnEffect;
			i = Math.sqrt(i);
			w.interpolate(black, dawnA, i);
			w.multiply(tint);
			set(w, 0, x1, x2, y1, y2);
		}else if ((locked && cycleLock == 2) ^ (!locked && TIME.light().dayIs() && TIME.light().secondOf() < twilightSeconds/2)) {
			
			//dawn
			double i = (TIME.light().secondOf())/(twilightSeconds/2);
			i *= dawnEffect;

			double t = i*minTilt;
			i*= i;

			w.interpolate(dawnA, dayLight, i);
			w.multiply(tint);
			set(w, t, x1, x2, y1, y2);
			
		}else if((locked && cycleLock == 3) ^ (!locked && TIME.light().dayIs() && TIME.light().secondsLeft() < twilightSeconds/2)) {
			
			//dusk
			double i = 1.0 - (TIME.light().secondsLeft())/(twilightSeconds/2);
			i *= dawnEffect;
			w2.copy(dusk);
			w2.multiply(tint);
			w.interpolate(dayLight, w2, i);
			set(w, minTilt-i*minTilt, x1, x2, y1, y2);
		}else if((locked && cycleLock == 4) ^ (!locked && TIME.light().nightIs() && TIME.light().secondOf() < twilightSeconds)) {
			
			//afterDusk
			double i = (TIME.light().secondOf())/twilightSeconds;
			i*= dawnEffect;
			i *= i;
			
			w2.copy(dusk);
			w2.multiply(tint);
			w.interpolate(w2, black, i);
			set(w, 0, x1, x2, y1, y2);
			
		}else if((locked && cycleLock == 5) ^ (!locked && TIME.light().dayIs())) {
			
			double i = (TIME.light().secondOf()-twilightSeconds/2)/(TIME.light().secondsTotal()-twilightSeconds);

			double tilt = (maxTilt-minTilt)*TIME.seasons().currentDay.dayLength();
			day.copy(dayLight).shade(0.5);
			if (TIME.light().partOf() < 0.5) {
				i *= 2;
				i = Math.pow(i, 0.5);
				tilt *= i;
				
				w.interpolate(dayLight, day, i);
				set(w, minTilt + tilt, x1, x2, y1, y2);
			}else {
				i = (i-0.5)*2;
				i = Math.pow(i, 0.5);
				i = 1.0-i;
				
				tilt *= i;
				
				w.interpolate(dayLight, day, i);
				set(w, minTilt + tilt, x1, x2, y1, y2);
			}
			
		}else {

		}
	}
	
	private void set(RGB color, double tilt, int x1, int x2, int y1, int y2) {
		
		double part = TIME.light().partOfCircular();
		if (TIME.light().nightIs())
			part = 0;
		
		if (S.get().lightCycle.get() == 0)
			part = 0.85;
		
		double ref = reflection/4 + 3*reflection*part/3;
		
		double amount = 2.0 - ref;
		
		work.setTilt(tilt).setDir(TIME.light().shadow.dir()).copy(color);
		
		CORE.renderer().lightDepthSet((byte) 0);
		work2.Set(work, amount*0.4);
		work2.register(x1, x2, y1, y2);
		
		CORE.renderer().lightDepthSet((byte) 127);
		work2.Set(work, amount*0.4);
		work2.register(x1, x2, y1, y2);
		
		CORE.renderer().shadeLight(false);
		work2.Set(work, amount*0.2);
		work2.register(x1, x2, y1, y2);
		TIME.light().shadow.setShadowDay(tilt);
	}
	

	
	
	
}
