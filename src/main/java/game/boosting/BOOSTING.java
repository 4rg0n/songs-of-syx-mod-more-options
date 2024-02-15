package game.boosting;


import game.GAME;
import game.GameDisposable;
import game.faction.Faction;
import game.faction.npc.NPCBonus;
import game.faction.npc.ruler.Royalty;
import init.race.POP_CL;
import init.race.RACES;
import init.race.Race;
import settlement.army.Div;
import settlement.stats.Induvidual;
import snake2d.Errors;
import snake2d.util.misc.ACTION;
import snake2d.util.sets.*;
import snake2d.util.sprite.SPRITE;
import util.dic.DicArmy;
import util.dic.DicGeo;
import util.dic.DicMisc;
import world.regions.Region;

import java.io.IOException;
import java.util.Comparator;

public abstract class BOOSTING {

	public static final String KEY = "BOOST";
	
	private static final KeyMap<Entry> map = new KeyMap<>();
	public static final LinkedList<ACTION> waiting = new LinkedList<>();
	private static final LinkedList<ACTION> connecters = new LinkedList<>();
	private static final ArrayListGrower<Boostable> all = new ArrayListGrower<>();
	static boolean hasErrored = false;
	private static BTypes types;	
	
	static {
		new GameDisposable() {
			
			@Override
			protected void dispose() {
				clear();
			}
		};
	}
	
	static void clear() {
		map.clear();
		waiting.clear();
		connecters.clear();
		all.clear();
		types =  new BTypes();
	}

	public static void init(GAME game) throws IOException {
		clear();
		BOOSTABLES.init();
		BoostableCat.init();
	}
	
	public static void fin(GAME game) {
		
		for (ACTION a : waiting)
			a.exe();
		for (ACTION a : connecters) {
			a.exe();
		}
		
		ArrayList<Boostable> all = new ArrayList<>(BOOSTING.all.size());
		all.add(BOOSTING.all);
		
		all.sort(new Comparator<Boostable>() {
			
			@Override
			public int compare(Boostable o1, Boostable o2) {
				int c = (""+o1.cat.name).compareTo(""+o2.cat.name);
				if (c == 0) {
					return (""+o1.name).compareTo(""+o2.name);
				}
				return c;
			}
		});
		
		BOOSTING.all.clear();
		BOOSTING.all.add(all);
		
		
		waiting.clear();
		connecters.clear();
	}
	
	public static LIST<Boostable> ALL(){
		return all;
	}
	
	public static BTypes TYPES() {
		return types;
	}
	
	public static void connecter(ACTION a) {
		connecters.add(a);
	}
	
	public static String available() {
		String s = "";
		for (String ss : map.keysSorted()) {
			s += ss;
			s += "  - " + (map.get(ss).isMaster ? "collection" : map.get(ss).all.get(0).name); 
			s += System.lineSeparator();
		}
		return s;
	}
	
	public static Boostable push(String key, double baseValue, CharSequence name, CharSequence desc, SPRITE icon, BoostableCat cat) {
		return push(key, baseValue, name, desc, icon, cat, -10000000);
	}
	
	public static Boostable push(String key, double baseValue, CharSequence name, CharSequence desc, SPRITE icon, BoostableCat cat, double minValue) {
		if (key.charAt(0) == '_')
			key = key.substring(1);
		key = cat.prefix + key;
		
		if (map.containsKey(key))
			throw new Errors.GameError("Another boostable with the same key exists " + key);
		Boostable b = new Boostable(all.size(), key, baseValue, name, desc, icon, cat, minValue);
		
		all.add(b);
		Entry e = new Entry(b, false);
		
		map.put(key, e);
		
		return b;
	}
	
	public static LIST<Boostable> get(String key){
		Entry e = map.get(key);
		if (e == null)
			return null;
		return e.all;
	}
	
	static Entry getE(String key){
		return map.get(key);
	}
	
	public static void addToMaster(String masterKey, Boostable b){
		
		masterKey = b.cat.prefix + masterKey;
		if (!map.containsKey(masterKey)) {
			Entry e = new Entry(b, true);
			map.put(masterKey, e);
			return;
		}
		
		Entry e = map.get(masterKey);
		
		if (!e.isMaster || e.all.get(0).cat != b.cat)
			throw new RuntimeException("must be the same category " + masterKey + " " + e.isMaster);
		
		e.all.add(b);

	}
	
	public static void addToMaster(Boostable b){
		
		String masterKey = b.cat.prefix.substring(0, b.cat.prefix.length()-1);
		if (!map.containsKey(masterKey)) {
			Entry e = new Entry(b, true);
			map.put(masterKey, e);
			return;
		}
		
		Entry e = map.get(masterKey);
		
		if (!e.isMaster || e.all.get(0).cat != b.cat)
			throw new RuntimeException("must be the same category " + masterKey + " " + e.isMaster);
		
		e.all.add(b);

	}

	private BOOSTING() {
		
	}

	static class Entry {
		
		public final ArrayListGrower<Boostable> all = new ArrayListGrower<>();
		public final boolean isMaster;
		
		Entry(Boostable b, boolean isMaster){
			all.add(b);
			this.isMaster = isMaster;
		}
		
	}
	
	
	
	public static class BTypes {
		
		BTypes(){
			
		}
		
		
		private final ArrayListGrower<BTYPE<?>> all = new ArrayListGrower<>();
		public final BTYPE<Region> REGION = new BTYPE<Region>(all, Region.class, DicGeo.¤¤Region){
			@Override
			public double value(Region t, Boostable b, BValue v) {
				return v.vGet(t);
			}
		};
		public final BTYPE<Induvidual> INDU = new BTYPE<Induvidual>(all, Induvidual.class, DicMisc.¤¤Subject){
			@Override
			public double value(Induvidual t, Boostable b, BValue v) {
				return v.vGet(t);
			}
		};
		public final BTYPE<Div> DIV = new BTYPE<Div>(all, Div.class, DicArmy.¤¤Army){
			@Override
			public double value(Div t, Boostable b, BValue v) {
				return v.vGet(t);
			}
		};
		public final BTYPE<Faction> FACTION = new BTYPE<Faction>(all, Faction.class, DicGeo.¤¤Realm){
			@Override
			public double value(Faction t, Boostable b, BValue v) {
				return v.vGet(t);
			}
		};
		public final BTYPE<POP_CL> POP = new BTYPE<POP_CL>(all, POP_CL.class, DicGeo.¤¤Realm){
			@Override
			public double value(POP_CL t, Boostable b, BValue v) {
				return v.vGet(t);
			}
		};
		public final BTYPE<Royalty> ROYALTY = new BTYPE<Royalty>(all, Royalty.class, DicGeo.¤¤Realm){
			@Override
			public double value(Royalty t, Boostable b, BValue v) {
				return v.vGet(t);
			}
		};
		public final BTYPE<Race> RACE = new BTYPE<Race>(all, Race.class, RACES.name()){
			@Override
			public double value(Race t, Boostable b, BValue v) {
				return v.vGet(t);
			}
		};
		public final BTYPE<NPCBonus> NPC = new BTYPE<NPCBonus>(all, NPCBonus.class, DicGeo.¤¤Realm){
			@Override
			public double value(NPCBonus t, Boostable b, BValue v) {
				return v.vGet(t);
			}
		};
		
		public LIST<BTYPE<?>> ALL(){
			return all;
		}
		
	}

	
}
