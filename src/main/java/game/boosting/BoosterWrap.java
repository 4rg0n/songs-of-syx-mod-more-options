package game.boosting;

import game.faction.Faction;
import game.faction.npc.NPCBonus;
import game.faction.npc.ruler.Royalty;
import init.race.POP_CL;
import init.race.Race;
import settlement.army.Div;
import settlement.stats.Induvidual;
import world.regions.Region;

public class BoosterWrap extends Booster{

	private final BValue v;
	private final double from;
	private final double to;
	
	public BoosterWrap(BValue v, BSourceInfo info, double value, boolean isMul) {
		super(info, isMul);
		this.v = v;
		to = value;
		
		if (isMul) {
			from = 1;
		}else {
			from = 0;
		}
	}
	
	public BoosterWrap(BValue v, BSourceInfo info, double from, double to, boolean isMul) {
		super(info, isMul);
		this.v = v;
		this.from = from;
		this.to = to;
	}


	@Override
	public double vGet(Region reg) {
		return v.vGet(reg);
	}

	@Override
	public double vGet(Induvidual indu) {
		return v.vGet(indu);
	}

	@Override
	public double vGet(Div div) {
		return v.vGet(div);
	}

	@Override
	public double vGet(Faction f) {
		return v.vGet(f);
	}

	@Override
	public double vGet(POP_CL reg) {
		return v.vGet(reg);
	}

	@Override
	public double vGet(Royalty roy) {
		return v.vGet(roy);
	}

	@Override
	public boolean has(Class<? extends BOOSTABLE_O> o) {
		return v.has(o);
	}

	@Override
	public double vGet(NPCBonus bonus) {
		return v.vGet(bonus);
	}

	@Override
	public double vGet(Race race) {
		return v.vGet(race);
	}

	@Override
	public double from() {
		return from;
	}

	@Override
	public double to() {
		return to;
	}

	


}