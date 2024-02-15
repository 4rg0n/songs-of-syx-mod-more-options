package game.boosting;

import game.faction.FACTIONS;
import game.faction.Faction;
import game.faction.npc.NPCBonus;
import game.faction.npc.ruler.Royalty;
import init.race.POP_CL;
import init.race.Race;
import settlement.army.Div;
import settlement.stats.Induvidual;
import world.regions.Region;

public interface BValue {

	public double vGet(Region reg);
	
	public double vGet(Induvidual indu);
	
	public double vGet(Div div);
	
	public double vGet(Faction f);
	
	public double vGet(NPCBonus bonus);
	
	public double vGet(POP_CL reg);
	
	public default double vGet(POP_CL reg, int daysBack) {
		return vGet(reg);
	}
	
	
	
	public double vGet(Royalty roy);
	
	public double vGet(Race race);
	
	public boolean has(Class<? extends BOOSTABLE_O> b);
	
	public static final BValue DUMMY = new BValueSimple() {
		
		@Override
		public double vGet(Faction f) {
			return 0;
		}
	};
	
	
	public interface BValueSimple extends BValue {
		
		@Override
		public default double vGet(Region reg) {
			if (reg.faction()!= null)
				return vGet(reg.faction());
			return 0;
		}
		
		@Override
		public default double vGet(Induvidual indu) {
			return vGet(indu.faction());
		}
		
		@Override
		public default double vGet(Div div) {
			return vGet(div.army().faction());
		}
		
		@Override
		public double vGet(Faction f);
		
		@Override
		public default double vGet(POP_CL reg) {
			if (reg.cl != null && !reg.cl.player)
				return vGet(FACTIONS.otherFaction());
			return vGet(FACTIONS.player());
		}
		
		@Override
		public default double vGet(Royalty roy) {
			return vGet(FACTIONS.player());
		}
		
		@Override
		default double vGet(Race race) {
			return 0;
		}
		
		@Override
		default double vGet(NPCBonus bonus) {
			return vGet(bonus.faction);
		}
		
		@Override
		public default boolean has(Class<? extends BOOSTABLE_O> b) {
			return b != Race.class;
		}
		
		
	}
}
