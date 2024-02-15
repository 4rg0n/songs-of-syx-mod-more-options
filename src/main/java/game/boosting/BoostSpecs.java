package game.boosting;

import game.GAME;
import game.boosting.BOOSTING.Entry;
import snake2d.LOG;
import snake2d.util.color.COLOR;
import snake2d.util.file.Json;
import snake2d.util.gui.GUI_BOX;
import snake2d.util.misc.ACTION;
import snake2d.util.sets.ArrayListGrower;
import snake2d.util.sets.LIST;
import snake2d.util.sets.LinkedList;
import snake2d.util.sprite.SPRITE;
import util.colors.GCOLOR;
import util.data.BOOLEANO;
import util.dic.DicMisc;
import util.gui.misc.GBox;
import util.gui.misc.GText;
import util.info.GFORMAT;

public class BoostSpecs {

	private final ArrayListGrower<BoostSpec> all = new ArrayListGrower<>();
	final boolean connect;
	
	public static final String MUL = ">MUL";
	public static final String ADD = ">ADD";

	private PromiseList li = null;
	public final BSourceInfo info;
	
	public BoostSpecs(CharSequence sourceName, SPRITE icon, boolean connect) {
		this.connect = connect;
		info = new BSourceInfo(sourceName, icon);
		
	}
	
	public void push(Booster factor, String targetKey, Object path){
		push(targetKey, factor, path.toString(), false);
		
	}
	
	public BoostSpec push(Booster factor, Boostable target) {
		return push(factor, target, null);
	}
	
	public void push(Boostable target, double value, boolean isMul) {
		Booster w = new BoosterImp.DUMMY(info, value, isMul);
		BoostSpec boost = new BoostSpec(w, target, null);
		all.add(boost);
		if (connect)
			target.addFactor(boost);
	}
	
	public BoostSpec push(Booster factor, Boostable target, CharSequence append) {
		BoostSpec boost = new BoostSpec(factor, target, append);
		all.add(boost);
		if (connect)
			target.addFactor(boost);
		return boost;
	}
	
	public boolean remove(Boostable target) {
		boolean ret = false;
		for (int i = 0; i < all.size(); i++) {
			if (all.get(i).boostable == target) {
				all.remove(all.get(i));
				ret = true;
				i--;
			}
		}
		return ret;
	}
	
	public void replace(int oldI, Booster nnew, Boostable target) {
		BoostSpec boost = new BoostSpec(nnew, target, null);
		all.replace(oldI, boost);
	}
	
	public boolean removeFirst(Boostable target) {
		boolean ret = false;
		for (int i = 0; i < all.size(); i++) {
			if (all.get(i).boostable == target) {
				all.remove(all.get(i));
				ret = true;
				i--;
			}
		}
		return ret;
	}
	
	public void pushWeak(Boostable target, double value, boolean isMul, Object path) {
		Booster w = new BoosterImp.DUMMY(info, value, isMul);
		push(target.key, w, path.toString(), true);
		
	}
	
	public void pushPromise(Boostable target, BValue pValue, double value, boolean isMul){
		if (pValue == null)
			pValue = BValue.DUMMY;
		Booster bb = new BoosterWrap(pValue, info, value, isMul);
		push(target.key, bb, "", false);
	}
	
	public void push(String key, Json json, BValue pValue){
		push(key, json, pValue, null);
	}
	
	public void push(String key, Json json, BValue pValue, CharSequence append){
		push(key, json, pValue, append, true);
	}
	
	public void push(String key, Json json, BValue pValue, CharSequence append, boolean allowMul, String... notallowed){
		
		if (!json.has(key)) {
			return;
		
		}
		
		json = json.json(key);
		
		if (pValue == null)
			pValue = BValue.DUMMY;

		
		for (String k : json.keys()) {
			
			boolean isMul = false;
			double value = json.d(k);
			String path = json.path() + ", line" + json.line(k);
			
			if (k.endsWith(MUL)) {
				if (!allowMul) {
					json.error("Only ADD is allowed in this context", k);
				}
				isMul = true;
				k = k.substring(0, k.length()-MUL.length());
			}else if (k.endsWith(ADD)) {
				k = k.substring(0, k.length()-ADD.length());
			}else {
				json.error("Malformed value. Must be a string that ends with " + ADD + " (addition), or " + MUL + (" (Multiplication)"), k);
			}
			
			for (String na : notallowed) {
				if (na.equalsIgnoreCase(k))
					json.error("This key is special in this context and not allowed!", k);
			}
			BSourceInfo i = info;
			if (append != null)
				i = new BSourceInfo(i.name, append, i.icon);
			Booster bb = new BoosterWrap(pValue, i, value, isMul);
			
			push(k, bb, path, false);
			
		}
	}
	
	public void push(Json json, BValue pValue){
		push(BOOSTING.KEY, json, pValue);
	}
	
	public LIST<BoostSpec> all(){
		return all;
	}
	
	private static int htab = 7;
	
	
	public void hover(GUI_BOX text, BOOSTABLE_O t) {
		hover(text, t, DicMisc.¤¤Effects);
	}
	
	public void hover(GUI_BOX text, BOOSTABLE_O t, CharSequence name) {
		GBox b = (GBox) text;
		if (name != null)
			b.textLL(name);
		b.NL();
		
		for (int si = 0; si < all().size(); si++) {
			BoostSpec s = all().get(si);
			if (hover(b, s, s.get(t), 0)) {
				b.tab(htab+2);
				b.add(GFORMAT.iOrF(b.text(), s.booster.from()).color(COLOR.WHITE65));
				GText te = b.text();
				te.color(COLOR.WHITE65);
				te.add('<').add('>');
				b.add(te);
				b.add(GFORMAT.iOrF(b.text(), s.booster.to()).color(COLOR.WHITE65));
				b.NL();
			}
			
			
		}
		
		
	}
	private static  Class<? extends BOOSTABLE_O> fType;
	private static  Class<? extends BOOSTABLE_O> fTypeNo;
	private static BOOLEANO<BoostSpec> filType = new BOOLEANO<BoostSpec>() {
		@Override
		public boolean is(BoostSpec t) {
			return t.booster.has(fType) && !t.booster.has(fTypeNo);
		}
	};
	
	public void hover(GUI_BOX text, double input, BTYPE<?> type, BTYPE<?> notype, int catMask) {
		fType = type.cl;
		fTypeNo = notype.cl;
		for (BoostSpec l : all()) {
			if (filType.is(l)) {
				hover(text, input, type.name, filType, catMask);
				return;
			}
				
		}
		
	}
	
	public void hover(GUI_BOX text, double input, int catMask) {
		hover(text, input, DicMisc.¤¤Effects, catMask);
	}
	
	public void hover(GUI_BOX text, double input, CharSequence name, int catMask) {
		hover(text, input, name, null, catMask);
	}
	
	public void hoverDetailed(GUI_BOX text, double input, CharSequence name, BOOLEANO<BoostSpec> filter, int catMask) {
		GBox b = (GBox) text;
		if (name != null)
			b.textLL(name);
		b.NL();
		
		for (BoostSpec l : all()) {
			if (filter != null && !filter.is(l))
				continue;
			double d = l.booster.getValue(input);
			if ((l.boostable.cat.typeMask & catMask) != 0 && hover(b, l, d, 0)) {
				
				b.tab(9);
				GText t = b.text();
				t.add('/');
				t.add(l.booster.getValue(1.0), 2);
				b.add(t);
				b.NL();
			}
		}
		b.NL();
	}
	
	public void hover(GUI_BOX text, double input, CharSequence name, BOOLEANO<BoostSpec> filter, int catMask) {
		GBox b = (GBox) text;
		if (name != null)
			b.textLL(name);
		b.NL();
		
		int tab = 0;
		
		for (BoostSpec l : all()) {
			if (filter != null && !filter.is(l))
				continue;
			double d = l.booster.getValue(input);
			if ((l.boostable.cat.typeMask & catMask) != 0 && hover(b, l, d, tab)) {
				if (tab >= 1) {
					tab = 0;
					b.NL();
				}else {
					tab++;;
				}
			}
		}
		b.NL();
	}
	
	public boolean hover(GBox b, BoostSpec l, double d, int tab) {
		
		if (l.boostable.name == null || l.boostable.name.length() == 0)
			return false;
		
		COLOR c = GCOLOR.T().INACTIVE;
		
		GText t = b.text();
		
		
		if (l.booster.isMul) {
			if (d < 1)
				c = GCOLOR.T().IBAD;
			else if (d > 1)
				c = GCOLOR.T().IGOOD;
			d -= 1;
			GFORMAT.percInc(t, d);
		}else {
			if (d < 0)
				c = GCOLOR.T().IBAD;
			else if (d > 0)
				c = GCOLOR.T().IGOOD;
			if (d == (int) d)
				GFORMAT.iIncr(t, (int)d);
			else
				GFORMAT.f0(t, d);
		}
		
		b.tab(tab*(htab+2));
		b.add(l.boostable.icon.small);
		b.add(b.text().color(c).add(l.tName));
		b.tab(tab*(htab+2)+htab);
		
		t.color(c);
		b.add(t);
		return true;
	}

	private void push(String key, Booster factor, String path, boolean isWeak) {
		if (li == null) {
			li = new PromiseList(this);
			BOOSTING.waiting.add(li);
		}
		li.push(key, factor, path, isWeak);
		
	}

	
	private static class PromiseList implements ACTION{
		
		public final LinkedList<Promise> all = new LinkedList<>();
		public final BoostSpecs coll;
		
		
		PromiseList(BoostSpecs coll){
			this.coll = coll;
		}

		@Override
		public void exe() {
			
			LinkedList<Promise> all = new LinkedList<>();
			
			for (Promise p : this.all) {
				
				Entry bos = BOOSTING.getE(p.key);

				if (bos == null) {
					String m = p.path + System.lineSeparator() + "no BOOSTABLE " + " named : " + p.key;
					if (BOOSTING.hasErrored) {
						LOG.ln(m);
					}else {
						BOOSTING.hasErrored = true;
						GAME.Warn(m + System.lineSeparator() + "Available:" + System.lineSeparator() + BOOSTING.available());
					}
					continue;
				}
				
				all.add(p);
			}
			
			for (Promise p : all) {
				Entry bos = BOOSTING.getE(p.key);
				if (p.isWeak) {
					for (Boostable b : bos.all)
						add(p, b);
				}
			}
			
			for (Promise p : all) {
				Entry bos = BOOSTING.getE(p.key);
				if (!p.isWeak && bos.isMaster) {
					for (Boostable b : bos.all)
						add(p, b);
				}
			}
			
			for (Promise p : all) {
				Entry bos = BOOSTING.getE(p.key);
				if (!p.isWeak && !bos.isMaster) {
					for (Boostable b : bos.all)
						add(p, b);
				}
			}
			
			coll.li = null;
		}
		
		private void add(Promise p, Boostable b) {
			BoostSpec boost = new BoostSpec(p.factor, b, null);
			for (BoostSpec bb : coll.all) {
				if (boost.isSameAs(bb)) {
					
					coll.all.remove(bb);
					if (coll.connect) {
						bb.boostable.removeFactor(bb);
					}
					break;
				}
			}
			
			coll.all.add(boost);
			if (coll.connect)
				b.addFactor(boost);
		}
		
		private void push(String key, Booster factor, String path, boolean isWeak) {
			
//			BSourceInfo info = coll.info;
//			if (append != null)
//				info = new BSourceInfo(coll.info.name, append, coll.info.icon);
			
			Promise p = new Promise(key, factor, path, isWeak);
			for (Promise pp : all) {
				if (pp.key == key && pp.factor.isMul == factor.isMul)
					all.remove(pp);
			}
			all.add(p);
		}

		
		private static class Promise{
			
	//		public final BSourceInfo info;
	//		public CharSequence append;
			public final String key;
			public final String path;
			public final Booster factor;
			public final boolean isWeak;
			
			Promise(String key, Booster factor, String path, boolean isWeak){
				this.key = key;
				this.factor = factor;
				this.path = path;
				this.isWeak = isWeak;
			//	this.info = info;
			}
			
		}
		
	}


	public double max(Boostable bo) {
		double m = 0;
		for (BoostSpec s : all())
			if (s.boostable == bo)
				m = Math.max(m, s.booster.max());
		return m;
	}
	
	
	
	
}
