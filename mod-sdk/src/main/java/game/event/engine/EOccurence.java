package game.event.engine;

import game.faction.Faction;
import game.time.TIME;
import game.values.GVALUES;
import game.values.Lockable;
import init.race.RACES;
import init.race.Race;
import init.type.CLIMATE;
import init.type.CLIMATES;
import init.type.TERRAIN;
import init.type.TERRAINS;
import settlement.main.SETT;
import settlement.stats.STATS;
import snake2d.util.file.Json;
import snake2d.util.gui.GUI_BOX;
import util.dic.Dic;
import util.gui.misc.GBox;
import util.info.GFORMAT;

// MODDED: made public
public final class EOccurence {

	public final Lockable<Faction> plockable = GVALUES.FACTION.LOCK.push();
	public final double[] coccurence = new double[CLIMATES.ALL().size()];
	public final double[] roccurence = new double[RACES.all().size()];
	public final double[] toccurence = new double[TERRAINS.ALL().size()];
	public final int maxSpawns;
	public double onlyAfterTime;
	
	
	public EOccurence(Json data, EventCollection engine, Event parent) {
		
		if (data.has("OCCURENCE")) {
			data = data.json("OCCURENCE");
			CLIMATES.MAP().readFill("CLIMATE", coccurence, data, 0, 10000000);
			RACES.map().readFill("RACE", roccurence, data, 0, 10000000);
			TERRAINS.MAP().readFill("TERRAIN", toccurence, data, 0, 100000);
			maxSpawns = data.i("MAX_SPAWNS", 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
			
			onlyAfterTime = data.dTry("ONLY_AFTER_DAYS", 0, 100000, 0)*TIME.secondsPerDay;
			plockable.push(data);
			data.checkUnused();
			
		}else {
			maxSpawns = 10;
		}
		
		
	}

	public double occurence(Race race) {
		double occ = 0;
		
		for (TERRAIN t : TERRAINS.ALL()) {
			occ += toccurence[t.index()]*SETT.WORLD_AREA().info.get(t).getD();	
		}
		CLIMATE climate = SETT.ENV().climate();
		occ *= coccurence[climate.index()]*roccurence[race.index()];
		return occ;
	}
	
	public double race(Race race) {
		return roccurence[race.index()];
	}
	
	public double occurence() {
		double occ = 0;
		
		for (TERRAIN t : TERRAINS.ALL()) {
			double d = toccurence[t.index()]*SETT.WORLD_AREA().info.get(t).getD();
			if (d > occ)
				occ = d;
		}
		
		double raM = 0;
		double tot = 1 + STATS.POP().POP.data(null).get(null);
		for (Race r : RACES.all()) {
			double d = roccurence[r.index()]*STATS.POP().POP.data().get(r)/tot;
			if (d > raM)
				raM = d;
		}
		occ *= coccurence[SETT.ENV().climate().index()]*raM;
		return occ;
	}

	public void hover(GUI_BOX box) {
		
		GBox b = (GBox) box;
		
		b.NL();
		b.textLL(Dic.¤¤Occurrence);
		b.NL();
		int tt = 0;
		
		for (TERRAIN t : TERRAINS.ALL()) {
			if (tt > 6) {
				tt = 0;
				b.NL();
			}
			b.add(t.icon());
			b.add(GFORMAT.f0(b.text(), toccurence[t.index()]));
		}
		b.NL();
		for (Race rr : RACES.all()) {
			if (tt > 6) {
				tt = 0;
				b.NL();
			}
			b.add(rr.appearance().icon);
			b.add(GFORMAT.f0(b.text(), roccurence[rr.index()]));
		}
		b.NL();
		CLIMATE climate = SETT.ENV().climate();
		b.textLL(CLIMATES.INFO().name);
		b.tab(6);
		b.add(GFORMAT.f0(b.text(), coccurence[climate.index()]));
		b.NL();
		b.textSLL(Dic.¤¤Total);
		b.tab(6);
		b.add(GFORMAT.f0(b.text(), occurence()));
		
		b.sep();
	}
	
}
