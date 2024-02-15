package game.boosting;

import init.race.POP_CL;
import init.settings.S;
import init.sprite.UI.Icon;
import init.sprite.UI.UI;
import lombok.Setter;
import snake2d.LOG;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GUI_BOX;
import snake2d.util.misc.CLAMP;
import snake2d.util.sets.ArrayListGrower;
import snake2d.util.sets.ArrayListResize;
import snake2d.util.sets.INDEXED;
import snake2d.util.sets.LIST;
import snake2d.util.sprite.SPRITE;
import util.colors.GCOLOR;
import util.dic.DicMisc;
import util.gui.misc.GBox;
import util.gui.misc.GText;
import util.info.GFORMAT;
import util.info.INFO;

public final class Boostable extends INFO implements INDEXED{

	final ArrayListGrower<BoostSpec> factors = new ArrayListGrower<>();
	final ArrayListGrower<BoostSpec> consumers = new ArrayListGrower<>();

	private final static ArrayListResize<BoostSpec> hovTmp = new ArrayListResize<BoostSpec>(32, 1024);

	private final int index;
	private byte deadlockCheck;

	// MODDED: was final, must be changeable
	@Setter
	public double baseValue;

	public final String key;
	public final Icon icon;
	public final BoostableCat cat;
	public final double minValue;

	public Boostable(int index, String key, double baseValue, CharSequence name, CharSequence desc, SPRITE icon, BoostableCat category, double minValue) {
		super(name, desc);
		this.index = index;
		this.baseValue = baseValue;
		this.key = key;
		this.icon = icon != null ? new Icon(Icon.S, icon) : UI.icons().s.DUMMY;
		this.cat = category;
		this.minValue = minValue;
		cat.all.add(this);
	}

	public Boostable copy() {
		Boostable b = new Boostable(index, key, baseValue, name, desc, icon, cat, minValue);
		b.factors.add(factors);
		b.consumers.add(consumers);
		return b;
	}

	public LIST<BoostSpec> adds() {
		return consumers;
	}

	public LIST<BoostSpec> muls() {
		return factors;
	}

	public double min(Class<? extends BOOSTABLE_O> b) {
		double m = 1;
		for (int i = 0; i < factors.size(); i++) {
			if (factors.get(i).booster.has(b))
				m *= factors.get(i).booster.min();
		}
		double a = baseValue;
		for (int i = 0; i < consumers.size(); i++) {
			if (consumers.get(i).booster.has(b))
				a += consumers.get(i).booster.min();
		}
		return m * a;
	}

	public double max(Class<? extends BOOSTABLE_O> b) {
		double m = 1;
		for (int i = 0; i < factors.size(); i++) {
			if (factors.get(i).booster.has(b))
				m *= factors.get(i).booster.max();
		}
		double a = baseValue;
		for (int i = 0; i < consumers.size(); i++) {
			if (consumers.get(i).booster.has(b))
				a += consumers.get(i).booster.max();
		}
		return m * a;
	}
	
	
	public double maxD(Class<? extends BOOSTABLE_O> b) {
		double m = 1;
		for (int i = 0; i < factors.size(); i++) {
			if (factors.get(i).booster.has(b)) {
				LOG.ln("mul " + factors.get(i).booster.info.name + " " + factors.get(i).booster.max());
				m *= factors.get(i).booster.max();
			}
		}
		double a = baseValue;
		for (int i = 0; i < consumers.size(); i++) {
			if (consumers.get(i).booster.has(b)) {
				LOG.ln("add " + consumers.get(i).booster.info.name + " " + consumers.get(i).booster.max());
				a += consumers.get(i).booster.max();
			}
		}
		return m * a;
	}

	
	public double progress(BOOSTABLE_O b) {
		double min = min(b.getClass());
		double max = max(b.getClass());
		
		double delta = max-min;
		return CLAMP.d(get(b)/delta, 0,1);
		
	}

	public double get(BOOSTABLE_O t) {
		if (deadlockCheck > 1) {
			throw new RuntimeException(
					"boostable " + key + "seems to be deadlocked. Make sure it's not a factor in its own factors");
		}

		deadlockCheck++;
		double m = 1;
		for (int i = 0; i < factors.size(); i++) {
			if ( factors.get(i).booster.has(t.getClass()))
				m *= factors.get(i).get(t);
		}
		double a = baseValue;
		double sub = 0;
		for (int i = 0; i < consumers.size(); i++) {
			if (!consumers.get(i).booster.has(t.getClass()))
				continue;
			double v = consumers.get(i).get(t);
			if (v > 0)
				a += v;
			else
				sub += v;
		}
		deadlockCheck--;
		return Math.max(m * a + sub, minValue);
	}
	
	public double debug(BOOSTABLE_O t) {
		
		LOG.ln(name);
		
		double m = 1;
		for (int i = 0; i < factors.size(); i++) {
			if ( factors.get(i).booster.has(t.getClass())) {
				LOG.ln(factors.get(i).booster.info.name + " m " + factors.get(i).get(t));
				m *= factors.get(i).get(t);
			}
		}
		double a = baseValue;
		double sub = 0;
		for (int i = 0; i < consumers.size(); i++) {
			if (!consumers.get(i).booster.has(t.getClass()))
				continue;
			LOG.ln(consumers.get(i).booster.info.name + " a " + consumers.get(i).get(t));
			double v = consumers.get(i).get(t);
			if (v > 0)
				a += v;
			else
				sub += v;
		}
		
		LOG.ln(m + " " + a + " " + sub);
		
		return Math.max(m * a + sub, minValue);
	}

	public double getAllBut(BOOSTABLE_O t, BoostSpec exclude) {

		double m = 1;
		for (int i = 0; i < factors.size(); i++) {
			if (factors.get(i) != exclude)
				m *= factors.get(i).get(t);
		}
		double a = baseValue;
		double sub = 0;
		for (int i = 0; i < consumers.size(); i++) {
			if (factors.get(i) != exclude) {
				double v = consumers.get(i).get(t);
				if (v > 0)
					a += v;
				else
					sub += v;
			}

		}
		return m * a + sub;
	}

	public void clearAllFactors() {
		factors.clear();
		consumers.clear();
	}

	public void addFactor(BoostSpec f) {
		if (f.booster.isMul) {
			factors.add(f);
		} else {
			consumers.add(f);
		}
	}

	public void removeFactor(BoostSpec f) {
		if (f.booster.isMul) {
			factors.remove(f);
		} else {
			consumers.remove(f);
		}
	}

	public void hover(GUI_BOX box, BOOSTABLE_O f, boolean keepNops) {
		hover(box, f, name, keepNops);
	}

	static final int htab = 7;

	public void hoverDetailed(GUI_BOX box, BOOSTABLE_O f, CharSequence name, boolean keepNops) {
		GBox b = (GBox) box;
		if (name != null)
			b.textLL(name);
		b.NL();

		double add = baseValue;
		double sub = 0;
		double mul = 1;

		{
			hovTmp.clearSoft();

			for (BoostSpec l : adds()) {
				double d = l.get(f);
				if (d < 0) {
					sub += d;
				}else {
					add += d;
				}
				hovTmp.add(l);
					
			}
			
			for (BoostSpec l : muls()) {
				double d = l.get(f);
				mul *= d;
				hovTmp.add(l);
			}
			
			for (BoostSpec s : hovTmp) {
				s.booster.hoverDetailed(box, s.get(f));
				if (s.booster.has(f.getClass()))
					b.add(UI.icons().s.arrowUp);
				else
					b.add(UI.icons().s.arrowDown);
				b.NL();
			}
		}

		{
			b.NL(8);
			b.tab(1);
			b.textL(DicMisc.¤¤Total);
			b.tab(5);

			b.add(GFORMAT.f0(b.text(), add));
			b.add(b.text().add('*'));
			b.add(GFORMAT.f1(b.text(), mul));
			if (sub != 0)
				b.add(GFORMAT.f0(b.text(), sub));
			b.add(b.text().add('='));

			b.add(GFORMAT.fRel(b.text(), get(f), baseValue));
		}

		b.NL();

	}
	
	public void hoverDetailedHistoric(GUI_BOX box, POP_CL o, CharSequence name, boolean keepNops, int daysBack) {
		GBox b = (GBox) box;
		if (name != null)
			b.textLL(name);
		b.NL();

		double add = baseValue;
		double sub = 0;
		double mul = 1;

		{
			hovTmp.clearSoft();

			for (BoostSpec l : adds()) {
				double d =  l.booster.getValue(l.booster.vGet(o, daysBack));
				if (d < 0) {
					sub += d;
				}else {
					add += d;
				}
				hovTmp.add(l);
					
			}
			
			for (BoostSpec l : muls()) {
				double d = l.booster.getValue(l.booster.vGet(o, daysBack));
				mul *= d;
				hovTmp.add(l);
			}
			
			for (BoostSpec s : hovTmp) {
				s.booster.hoverDetailed(box, s.booster.getValue(s.booster.vGet(o, daysBack)));
				b.NL();
			}
		}

		{
			b.NL(8);
			b.tab(1);
			b.textL(DicMisc.¤¤Total);
			b.tab(5);

			b.add(GFORMAT.f0(b.text(), add));
			b.add(b.text().add('*'));
			b.add(GFORMAT.f1(b.text(), mul));
			if (sub != 0)
				b.add(GFORMAT.f0(b.text(), sub));
			b.add(b.text().add('='));
			b.add(GFORMAT.fRel(b.text(),Math.max(mul * add + sub, minValue), baseValue));
		}

		b.NL();

	}
	
	public void hover(GUI_BOX box, BOOSTABLE_O f, CharSequence name, boolean keepNops) {
		GBox b = (GBox) box;
		if (name != null)
			b.textLL(name);
		b.NL();

		double add = baseValue;
		double sub = 0;
		double mul = 1;

		{
			int tab = 0;
			hovTmp.clearSoft();

			for (BoostSpec l : adds()) {
				double d = l.get(f);
				if (d < 0) {
					sub += d;
					hovTmp.add(l);
				}
			}

			int ti = 0;

			for (BoostSpec l : adds()) {
				double d = l.get(f);
				if (d <= 0)
					continue;
				add += d;
				
				tab = hov(f, b, l, tab);
				if (ti < hovTmp.size()) {
					tab = hov(f, b, hovTmp.get(ti), tab);
				} else {
					tab = 0;
					b.NL();
				}
				ti++;
			}
			for (; ti < hovTmp.size(); ti++) {
				tab = 1;
				hov(f, b, hovTmp.get(ti), tab);
			}

			// b.NL();
			// b.tab(1*(htab+2));
			// b.text(DicMisc.¤¤Addative);
			// b.tab(1*(htab+2)+htab);
			// b.add(GFORMAT.f0(b.text(), tot));
			b.NL(4);

			tab = 0;
		}

		{
			int tab = 0;
			hovTmp.clearSoft();

			for (BoostSpec l : muls()) {
				double d = l.get(f);
				if (d < 1) {
					mul *= d;
					hovTmp.add(l);
				}
			}

			int ti = 0;

			for (BoostSpec l : muls()) {
				double d = l.get(f);
				if (d <= 1)
					continue;
				tab = hov(f, b, l, tab);
				mul *= d;
				if (ti < hovTmp.size()) {
					tab = hov(f, b, hovTmp.get(ti), tab);
				} else {
					tab = 0;
					b.NL();
				}
				ti++;
			}
			for (; ti < hovTmp.size(); ti++) {
				tab = 1;
				hov(f, b, hovTmp.get(ti), tab);
			}

			// b.NL();
			// b.tab(1*(htab+2));
			// b.text(DicMisc.¤¤Multipliers);
			// b.tab(1*(htab+2)+htab);
			// GText t = b.text();
			// t.add('*');
			// b.add(GFORMAT.f1(t, mul));
			b.NL(4);

			tab = 0;
		}

		{
			b.NL(8);
			b.tab(1);
			b.textL(DicMisc.¤¤Total);
			b.tab(5);

			b.add(GFORMAT.f0(b.text(), add));
			b.add(b.text().add('*'));
			b.add(GFORMAT.f1(b.text(), mul));
			if (sub != 0)
				b.add(GFORMAT.f0(b.text(), sub));
			b.add(b.text().add('='));

			b.add(GFORMAT.fRel(b.text(), get(f), baseValue));
		}

		if (keepNops) {
			b.NL(8);
			int tab = 0;
			for (BoostSpec l : adds()) {
				double d = l.get(f);
				if (d == 0) {
					tab = hov(f, b, l, tab);
				}
			}
			for (BoostSpec l : muls()) {
				double d = l.get(f);
				if (d == 1) {
					tab = hov(f, b, l, tab);
				}
			}
		}

		b.NL();

	}

	private int hov(BOOSTABLE_O f, GBox b, BoostSpec l, int tab) {

		if (!l.booster.isMul) {
			double d = l.get(f);
			COLOR c = GCOLOR.T().INACTIVE;
			if (d < 0)
				c = GCOLOR.T().IBAD;
			else if (d > 0)
				c = GCOLOR.T().IGOOD;

			b.tab(tab * (htab + 2));
			b.add(b.text().color(c).add(l.booster.info.name));
			b.tab(tab * (htab + 2) + htab);

			GText t = b.text();
			if (d == (int) d)
				GFORMAT.iIncr(t, (int) d);
			else
				GFORMAT.f0(t, d);
			t.color(c);
			if (S.get().developer)
				t.add(l.booster.has(f.getClass()) ? ":)" : "!");
			
			b.add(t);

		} else {
			double d = l.get(f);
			COLOR c = GCOLOR.T().INACTIVE;
			if (d < 1)
				c = GCOLOR.T().IBAD;
			else if (d > 1)
				c = GCOLOR.T().IGOOD;
			b.tab(tab * (htab + 2));
			b.add(b.text().color(c).add(l.booster.info.name));
			b.tab(tab * (htab + 2) + htab);

			GText t = b.text();
			t.add('*');
			GFORMAT.f1(t, d);
			t.color(c);
			if (S.get().developer)
				t.add(l.booster.has(f.getClass()) ? ":)" : "!");
			b.add(t);

		}
		if (tab > 0) {
			tab = 0;
			b.NL();
		} else {
			tab = 1;
		}
		return tab;
	}
	
	@Override
	public int index() {
		return index;
	}

}