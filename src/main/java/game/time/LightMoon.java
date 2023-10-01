package game.time;

import init.settings.S;
import lombok.Setter;
import snake2d.CORE;
import snake2d.util.color.RGB;
import snake2d.util.color.RGB.RGBImp;
import snake2d.util.light.AmbientLight;

public final class LightMoon {
	
	private final RGBImp black = new RGBImp().r(0.1).g(0.1).b(0.1);
	private final RGBImp moonA = new RGBImp().r(0.4).g(0.4).b(1.5).shade(1.5);
//	private final PointLight point = new PointLight(0.4/2, 0.4/2, 1.5/2);

	private final RGBImp w = new RGBImp();
	
	private final AmbientLight work = new AmbientLight();
	private final AmbientLight work2 = new AmbientLight();

	@Setter
	private boolean locked = false;

	@Setter
	private int cycleLock = 0;
	
	public LightMoon(){

	}
	
	public void apply(int x1, int x2, int y1, int y2) {
		
		final double twilightSeconds = LightSun.twilightSeconds;
		
		double shadow = 0;
		
		if(S.get().lightCycle.get() == 0)
			return;
		
		if (TIME.light().nightIs()) {
			if(TIME.light().secondsLeft() > twilightSeconds && TIME.light().secondsLeft()<= twilightSeconds*2)
				shadow = (TIME.light().secondsLeft()-twilightSeconds)/twilightSeconds;
			else if (TIME.light().secondOf() > twilightSeconds && TIME.light().secondOf() <= twilightSeconds*2)
				shadow = (TIME.light().secondOf()-twilightSeconds)/twilightSeconds;
			else if(TIME.light().secondsLeft() >= twilightSeconds*2 && TIME.light().secondOf() > twilightSeconds*2)
				shadow = 1.0;
			else
				shadow = 0;
		}
		
		if ((locked && cycleLock == 1) ^ (!locked && TIME.light().nightIs() && TIME.light().secondsLeft() < twilightSeconds)) {
			
			//predawn
			double i = (twilightSeconds-TIME.light().secondsLeft())/twilightSeconds;
			w.interpolate(moonA, black, i);
			set(w, 15-i*15, shadow, x1, x2, y1, y2);
		}else if((locked && cycleLock == 4) ^ (!locked &&TIME.light().nightIs() && TIME.light().secondOf() < twilightSeconds)) {
			
			//afterDusk
			double i = (TIME.light().secondOf())/twilightSeconds;
			
			w.interpolate(black, moonA, i);
			set(w, 15*i, shadow, x1, x2, y1, y2);
			
		}else if((locked && cycleLock == 6) ^ (!locked && TIME.light().nightIs())) {
			
			double i = (TIME.light().secondOf()-twilightSeconds)/(TIME.light().secondsTotal()-2*twilightSeconds);

			double tilt = 10;
			
			if (TIME.light().partOf() < 0.5) {
				i *= 2;
				tilt *= i;
				
				
				set(moonA, 15 + tilt, shadow, x1, x2, y1, y2);
			}else {
				i = (i-0.5)*2;

				tilt *= (1.0-i);
				set(moonA, 15 + tilt, shadow, x1, x2, y1, y2);
			}
			
		}
		
//		if (TIME.light().nightIs()) {
//			double i = TIME.light().partOfCircular();
//			point.setRed(0.4*i);
//			point.setGreen(0.4*i);
//			point.setBlue(1.5*i);
//			point.setRadius(y2-y1);
//			point.setZ(100);
//			point.set(x1 +(x2-x1)/2, y1+(y2-y1)/2);
//			point.register();
//		}
	}
	
	private void set(RGB color, double tilt, double shadow, int x1, int x2, int y1, int y2) {
		work.setTilt(tilt).setDir(TIME.light().shadow.dirNight()).copy(color);
		work.shade(0.5 + 3.0*S.get().nightGamma.getD());
		work2.Set(work, 0.25+1.0-shadow);
		CORE.renderer().shadeLight(false);
		work2.register(x1, x2, y1, y2);
		if (shadow > 0) {			
			
			
			work.shade(shadow*0.5);
			CORE.renderer().lightDepthSet((byte) 127);
			work.register(x1, x2, y1, y2);
			CORE.renderer().lightDepthSet((byte) 0);
			work.register(x1, x2, y1, y2);
		}
		
		TIME.light().shadow.setShadowNight(tilt);
		CORE.renderer().shadeLight(false);
	}
	

	
	
	
}
