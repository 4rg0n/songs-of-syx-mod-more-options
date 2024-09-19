package settlement.room.main.copy;

import init.paths.PATHS;
import settlement.main.SETT;
import settlement.path.AVAILABILITY;
import settlement.room.main.*;
import settlement.room.main.construction.ConstructionInit;
import settlement.room.main.furnisher.Furnisher;
import settlement.room.main.furnisher.FurnisherItem;
import settlement.room.main.furnisher.FurnisherItemGroup;
import settlement.room.main.placement.UtilWallPlacability;
import settlement.room.main.util.RoomAreaWrapper.RoomWrap;
import settlement.tilemap.terrain.TBuilding;
import snake2d.util.datatypes.DIR;
import snake2d.util.file.Json;
import snake2d.util.file.JsonE;
import snake2d.util.sets.ArrayList;
import snake2d.util.sets.ArrayListGrower;
import snake2d.util.sets.LIST;

import java.util.LinkedList;

public final class SavedPrints {

	private final LIST<ArrayListGrower<SavedPrint>> all;

	SavedPrints(ROOMS rooms){
		
		ArrayList<ArrayListGrower<SavedPrint>> all = new ArrayList<>(rooms.AMOUNT_OF_BLUEPRINTS);
		while(all.hasRoom())
			all.add(new ArrayListGrower<SavedPrint>());
		this.all = all;
		
		try {
			Json[] jsons = new Json(PATHS.local().PROFILE.get("SavedPrints")).jsons("BLUEPRINTS");
			for (Json j : jsons) {
				SavedPrint p = new SavedPrint(rooms, j);
				if (p.blue != null && p.blue.constructor() != null && p.blue.constructor().canBeCopied() && p.check.equals(SavedPrint.check(p.blue)))
					add(p);
			}
			
		}catch(Exception e) {
			e.printStackTrace(System.out);
			save();
		}
		
	}
	
	private void save() {
		
		LinkedList<JsonE> all = new LinkedList<>();
		
		for (LIST<SavedPrint> ll : this.all) {
			for (SavedPrint l : ll)
				all.add(l.save());
		}
		JsonE[] jsons = new JsonE[all.size()];
		int i = 0;
		for (JsonE e : all)
			jsons[i++] = e;
		
		JsonE json = new JsonE();
		json.add("BLUEPRINTS", jsons);
		
		
			
		try {
			if (!PATHS.local().PROFILE.exists("SavedPrints"))
				PATHS.local().PROFILE.create("SavedPrints");
			
			json.save(PATHS.local().PROFILE.get("SavedPrints"));
			
		}catch(Exception e) {
			e.printStackTrace(System.out);
			all.clear();
		}
	}
	
	public SavedPrint push(Room ins, int mx, int my) {
		if (canAdd(ins)) {
			SavedPrint p = new SavedPrint(ins, mx, my);
			add(p);
			save();
			return p;
		}
		return null;
	}
	
	public void remove(SavedPrint print) {
		all.get(print.blue.index()).remove(print);
		save();
	}

	// MODDED: made public
	public void add(SavedPrint p) {
		all.get(p.blue.index()).add(p);
	}
	
	public LIST<SavedPrint> all(RoomBlueprint p){
		return all.get(p.index());
	}
	
	public boolean canAdd(Room ins) {
		if (ins.constructor() == null)
			return false;
		return canAdd(ins.constructor().blue());
	}
	
	public boolean canAdd(RoomBlueprint b) {
		if (b instanceof RoomBlueprintIns<?>) {
			RoomBlueprintImp bb = (RoomBlueprintImp) b;
			if (bb.constructor() == null)
				return false;
			if (!bb.constructor().usesArea())
				return false;
			if (!bb.constructor().canBeCopied())
				return false;
			return true;
		}
		return false;
	}
	
	public static class SavedPrint {
		
		public CharSequence name;
		public final RoomBlueprintImp blue;
		public final String check;
		public final int width,height;
		public final TBuilding structure;
		private final int[] data;
		private static final RoomWrap wrap = new RoomWrap();

		// MODDED: made public
		public SavedPrint(ROOMS rr, Json json){
			
			name = json.text("NAME");
			RoomBlueprint pp = rr.collection.tryGet(json.value("ROOM"));
			blue = pp instanceof RoomBlueprintImp ? (RoomBlueprintImp) pp : null;
			check = json.has("CHECK") ? json.value("CHECK") : "";
			width = json.i("WIDTH");
			height = json.i("HEIGHT");
			data = json.is("DATA");
			structure = SETT.TERRAIN().BUILDINGS.readTry("STRUCTURE", json);
		}

		// MODDED: made public
		public JsonE save() {
			JsonE j = new JsonE();
			j.addString("NAME", ""+name);
			j.add("ROOM", blue.key);
			j.add("STRUCTURE", structure == null ? "_" : structure.key);
			j.add("CHECK", check);
			j.add("WIDTH", width);
			j.add("HEIGHT", height);
			j.add("DATA", data);
			
			return j;
		}
		
		private static String check(RoomBlueprintImp blue) {
			String ch = "";
			Furnisher f = blue.constructor();
			ch += f.groups().size();
			for (FurnisherItemGroup g : f.groups()) {
				ch += g.min ;
				ch += g.max;
				ch += g.rotations();
				ch += g.size();
				for (int i = 0; i < g.size(); i++) {
					ch += g.item(i, 0).width();
					ch += g.item(i, 0).height();
				}
			}
			return ch;
		}
		
		SavedPrint(String name, SavedPrint other){
			
			this.name = name;
			blue = other.blue;
			check = other.check;
			width = other.width;
			height = other.height;
			data = other.data;
			structure = other.structure;
		}
		
		SavedPrint(Room ins, int mx, int my){
			wrap.init(ins, mx, my);
			name = ""+ins.name(mx, my);
			blue = ins.constructor().blue();
			check = check(blue);
			width = wrap.body().width()+2;
			height = wrap.body().height()+2;
			data = new int[width*height];
			structure = ConstructionInit.findStructure(mx, my);
			
			for (int dy = 0; dy < height; dy++)
				for (int dx = 0; dx < width; dx++) {
					int di = dx + dy*width;
					int x = dx + wrap.body().x1()-1;
					int y = dy + wrap.body().y1()-1;
					if (wrap.is(x, y)) {
						this.data[di] |= 0b1;
						if (SETT.TERRAIN().get(x, y).roofIs())
							this.data[di] |= 0b100;
						if (SETT.PATH().availability.get(x, y) != AVAILABILITY.ROOM) {
							this.data[di] |= 0b01000;
						}
						if (SETT.ROOMS().fData.isMaster.is(x, y)) {
							FurnisherItem it = SETT.ROOMS().fData.item.get(x, y);
							this.data[(dx) + (dy)*width] |= (it.index()+1)<<4;
						}
						
					}else {
						boolean is = false;
						for (DIR d : DIR.ALL) {
							if (wrap.is(x, y, d)) {
								is = true;
								break;
							}
						}
						if (is) {
							if (UtilWallPlacability.openingIsReal.is(x, y))
								this.data[di] |= 0b100;
							else if (UtilWallPlacability.wallisReal.is(x, y))
								this.data[di] |= 0b010;
						}
					}
					
				}
		}
		
		public boolean isRoom(int rx, int ry) {
			return (data[rx+ry*width] & 1) != 0;
		}
		
		public boolean isWall(int rx, int ry) {
			return (data[rx+ry*width] & 0b10) != 0;
		}
		
		public boolean isRoof(int rx, int ry) {
			return (data[rx+ry*width] & 0b100) != 0;
		}
		
		public boolean isSoldid(int rx, int ry) {
			return (data[rx+ry*width] & 0b1000) != 0;
		}
		
		public FurnisherItem item(int rx, int ry, RoomBlueprintImp blue) {
			int i = (data[rx+ry*width] & 0x0FFFF0) >> 4;
			if (i > 0) { 
				return blue.constructor().item(i-1);
			}
			return null;
		}
		
	}
	
}
