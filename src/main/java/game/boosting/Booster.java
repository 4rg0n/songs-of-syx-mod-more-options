package game.boosting;

import snake2d.util.color.COLOR;
import snake2d.util.gui.GUI_BOX;
import snake2d.util.misc.CLAMP;
import snake2d.util.sprite.SPRITE;
import util.gui.misc.GBox;
import util.gui.misc.GText;
import util.info.GFORMAT;

public abstract class Booster implements BValue{

	
	public final BSourceInfo info;
	
//	public CharSequence name;
//	public final SPRITE icon;
	public final boolean isMul;
	
//	public Booster(CharSequence name, SPRITE icon, boolean isMul){
//		this(name, icon, null, isMul);
//	}
	
	public Booster(BSourceInfo info, boolean isMul){
		
		this.info = info;
//		if (append != null) {
//			name = name + " (" + append + ")";
//		}
//		this.name = name;
		this.isMul = isMul;
//		this.icon = new SPRITE.Resized(icon, Icon.S);
	}
	
	public abstract double from();
	
	public abstract double to();
	
	public double min() {
		return Math.min(from(), to());
	}
	
	public double max() {
		return Math.max(from(), to());
	}
	
	public double getValue(double input) {
		input = CLAMP.d(input, 0, 1);
		return from() + input*(to()-from());
	}
	
	public double get(Boostable bo, BOOSTABLE_O o) {
		return from() + (to()-from())*o.boostableValue(bo, this);
	}

	public boolean isPositive(double input) {
		return (isMul && getValue(input) >= 1) || getValue(input) > 0;
	}
	
	public void hoverDetailed(GUI_BOX box, double value) {
		hover(box, info.icon, info.name, value, from(), to(), isMul);
	}
	
	public void hover(GUI_BOX box, double value) {
		hover(box, info.icon, info.name, value, isMul);
	}
	
	public static void hover(GUI_BOX box, SPRITE icon, CharSequence name, double value, boolean isMul) {
		GBox b = (GBox) box;
		b.add(icon);
		b.text(name);
		b.tab(7);
		GText t = b.text();
		format(t, value, isMul);
		b.add(t);
	}
	
	public static void hover(GUI_BOX box, SPRITE icon, CharSequence name, double value, double from, double to, boolean isMul) {
		
		GBox b = (GBox) box;
		hover(box, icon, name, value, isMul);
		b.tab(9);
		
		GText t = b.text();
		t.color(COLOR.WHITE65);
		
		t.add('(');
		t.add(from);
		b.add(t);
		b.tab(11);
		
		t = b.text();
		t.color(COLOR.WHITE65);
		
		t.add('<').add('-').add('>').s();
		t.add(to);
		t.add(')');
		b.add(t);
		
		
	}
	
	public GText format(GText t, double value) {
		return format(t, value, isMul);
	}
	
	public static GText format(GText t, double value, boolean isMul) {
		if (isMul) {
			t.add('*').s();
			GFORMAT.f1(t, value);
		}else {
			if (value == (int) value)
				GFORMAT.iIncr(t, (int)value);
			else
				GFORMAT.f0(t, value);
		}
		return t;
	}
	
	public BoostSpec add(Boostable boostable, CharSequence append){
		BoostSpec b = new BoostSpec(this, boostable, append);
		boostable.addFactor(b);
		return b;
	}
	
	public BoostSpec add(Boostable boostable){
		return add(boostable, null);
	}

	
}
