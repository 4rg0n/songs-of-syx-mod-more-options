package init.boostable;

import init.biomes.CLIMATES;
import init.race.RACES;
import init.race.Race;
import lombok.Setter;
import settlement.army.Div;
import settlement.entity.humanoid.HCLASS;
import settlement.entity.humanoid.Humanoid;
import settlement.main.SETT;
import settlement.stats.Induvidual;
import snake2d.util.sets.INDEXED;
import snake2d.util.sprite.SPRITE;
import util.info.INFO;

import java.util.Arrays;

public class BOOSTABLE extends INFO implements INDEXED{
	
	public final int index;
	public final String key;
	final SPRITE icon;
	// MODDED: was final; must be changeable
	@Setter
	public double defAdd;
	double[] climates = new double[CLIMATES.ALL().size()];

	BOOSTABLE(String key, double defAdd, CharSequence name, CharSequence desc, SPRITE icon){
		super(name, desc);
		index = BOOSTABLES.self.all.add(this);
		this.icon = icon;
		this.key = key;
		this.defAdd = defAdd;
		Arrays.fill(climates, 1);
	}



	@Override
	public int index() {
		return index;
	}
	
	public SPRITE icon(){
		return icon;
	}
	
	@Override
	public String toString() {
		return "B:" + key; 
	}
	
	public double get(Humanoid h) {
		return get(h.indu());
	}
	
	public double get(Induvidual v) {
		if (v.player())
			return BOOSTABLES.player().get(this, v);
		return BOOSTABLES.enemy().get(this, v);
	}
	
	public double max(Induvidual v) {
		if (v.player())
			return BOOSTABLES.player().max(this);
		return BOOSTABLES.enemy().max(this);
	}
	
	public double get(Div v) {
		if (v.army() != SETT.ARMIES().enemy())
			return BOOSTABLES.player().get(this, v);
		return BOOSTABLES.enemy().get(this, v);
	}
	
	public double get(HCLASS c, Race r) {
		return BOOSTABLES.player().get(this, c, r);
	}
	
	public double race(Race race) {
		return RACES.bonus().mul(this, race)*(defAdd+RACES.bonus().add(this, race));
	}
	
	public double raceMax() {
		return RACES.bonus().maxMul(this)*(defAdd+RACES.bonus().maxAdd(this));
	}
	
	
}