package game.boosting;

import snake2d.util.sets.ArrayListGrower;
import snake2d.util.sets.LIST;
import util.data.BOOLEANO;
import util.dic.DicMisc;
import util.dic.DicRes;

public class BoostableCat {
	
	public static final int TYPE_CRAP = 0b0001;
	public static final int TYPE_WORLD = 0b0010;
	public static final int TYPE_SETT = 0b0100;
	
	public final String prefix;
	public final CharSequence name;
	public final CharSequence desc;
	public final int typeMask;
	ArrayListGrower<Boostable> all = new ArrayListGrower<>();

	public static final BoostableCat WORLD_CIVICS = new BoostableCat("WORLD_", DicMisc.¤¤World + ": " + DicMisc.¤¤Civics, "", TYPE_WORLD);
	public static final BoostableCat WORLD_PRODUCTION = new BoostableCat("WORLD_", DicMisc.¤¤World + ": " + DicMisc.¤¤Production, "", TYPE_WORLD);
	public static final BoostableCat WORLD = new BoostableCat("WORLD_", DicMisc.¤¤World, "", TYPE_WORLD);
	public static final BoostableCat RELIGION = new BoostableCat("RELIGION_", DicMisc.¤¤Religion, "", TYPE_WORLD);
	public static final BoostableCat WORLD_DUMP = new BoostableCat("WORLD_", DicMisc.¤¤World + ": " + DicMisc.¤¤Misc, "", TYPE_CRAP);
	public static final BoostableCat OTHER = new BoostableCat("", DicMisc.¤¤Misc, "", TYPE_CRAP);
	
	public static final BoostableCat SETT_RESOURCE = new BoostableCat("RESOURCE_", DicRes.¤¤Resource, "", TYPE_SETT);
	
	public BoostableCat(String prefix, CharSequence name, CharSequence desc, int typeMask) {
		this.prefix = prefix;
		this.name = name;
		this.desc = desc;
		this.typeMask = typeMask;
	}
	
	public LIST<Boostable> all() {
		return all;
	}
	
	static void init() {
		WORLD_CIVICS.all.clear();
		WORLD_PRODUCTION.all.clear();
		WORLD.all.clear();
		RELIGION.all.clear();
		WORLD_DUMP.all.clear();
		OTHER.all.clear();
		SETT_RESOURCE.all.clear();
	}
	
	public final BOOLEANO<BoostSpec> filter = new BOOLEANO<BoostSpec>() {

		@Override
		public boolean is(BoostSpec t) {
			return t.boostable.cat == BoostableCat.this;
		}
		
	};
	
}