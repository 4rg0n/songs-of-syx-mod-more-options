package world.battle;

import lombok.Setter;
import snake2d.util.datatypes.COORDINATE;
import snake2d.util.misc.CLAMP;
import snake2d.util.rnd.RND;
import world.battle.ResolverSide.RCount;
import world.battle.Side.SideUnit;
import world.entity.army.WArmy;
import world.map.regions.Region;
import world.region.RD;

// MODDED: made public
public final class Resolver {

	// MODDED
	@Setter
	private static double playerLootMulti = 1.0;
	// MODDED
	@Setter
	private static double enemyLootMulti = 1.0;

	private final ResolverSide a;
	private final ResolverSide b;
	private final ResolverPlayer iplayer;
	private final RCount count = new RCount();
	
	Resolver(){
		iplayer = new ResolverPlayer();
		a = new ResolverSide();
		b = new ResolverSide();
	}
	
	void init(Side A, Side B) {
		
		init(A, 0, B, 0);
		
		ResolverSide winner = a;
		ResolverSide looser = b;
		if (b.powerBalance > a.powerBalance) {
			winner = b;
			looser = a;
		}
		
		if (looser.player) {
			if (looser.us.get(0).unit.a() != null) {
				COORDINATE ret = Util.retTile(looser.us.get(0).unit.a());
				if (ret == null) {
					// MODDED
					looser.us.get(0).count(count.clear(), 1.0 * enemyLootMulti, true);
				}else {
					looser.retreatCoo.set(ret);
					// MODDED
					looser.us.get(0).count(count.clear(), retreatValue(looser) * enemyLootMulti, true);
				}
			}
			looser.count(count.clear(), autoValue(looser), false);
			winner.count(count.clear(), autoValue(winner), false);
			iplayer.battle(looser, winner);
			return;
		}
		
		if (looser.side.us.get(0).r() != null) {
			return;
		}
		
		WArmy retreater = looser.side.us.get(0).a();
		
		double retValue = retreatValue(looser);
		if (retValue < 1) {
			COORDINATE ret = Util.retTile(retreater);
			
			if (ret != null) {
				
				retreater.teleport(ret.x(), ret.y());
				if (winner.player) {
					iplayer.enemyWithdraws(winner, looser);
					return;
				}
				looser.us.get(0).extract(retValue);
				BattleListener.notify(winner, looser);
				return;
			}
		}
		
		
		looser.count(count.clear(), autoValue(looser), false);
		winner.count(count.clear(), autoValue(winner), false);
		if (winner.player) {
			if (winner.us.get(0).unit.a() != null) {
				COORDINATE ret = Util.retTile(looser.us.get(0).unit.a());
				if (ret == null) {
					// MODDED
					looser.us.get(0).count(count.clear(), 1.0 * playerLootMulti, true);
				}else {
					// MODDED
					looser.us.get(0).count(count.clear(), retreatValue(looser) * playerLootMulti, true);
					
					looser.retreatCoo.set(ret);
				}
			}
			iplayer.battle(winner, looser);
			return;
		}

		BattleListener.notify(winner, looser);

		looser.extract(1.0);
		winner.extract(((1-winner.powerBalance)));
		
		
	}
	
	public static double retreatValue(ResolverSide retreater) {
		double d = (1-retreater.powerBalance);
		d = CLAMP.d(d, 0, 1);
		return d;
	}
	
	public static double autoValue(ResolverSide side) {
		if (side.powerBalance < 0.5)
			return 1.0;
		return CLAMP.d(1.0-side.powerBalance, 0, 1);
	}
	
	
	void besige(Side besieger, Side besieged, boolean first) {

		
		Region reg = besieged.us.get(0).r();
		double extra = RD.MILITARY().power.getD(reg)*RD.MILITARY().fort.getD(reg);
		
		init(besieger, 0, besieged, extra);
		b.us.get(0).defences = extra;

		if (a.player) {
			if (b.powerBalance > a.powerBalance) {
				a.count(count, 1.0, false);
				b.count(count, 1-b.powerBalance, false);
			}else {
				b.count(count, 1.0, false);
				a.count(count, 1-a.powerBalance, false);
			}
			iplayer.besige(a, b);
			return;
		}
		if (b.player && first) {
			b.count(count, 0, true);
			if (b.men() > 0) {
				iplayer.sallyOut(b, a);
				return;
			}
		}
		
		if (b.powerBalance > a.powerBalance) {
			return;
		}
		
		
		
		BattleListener.notify(besieger, reg); 
		if (b.player && reg.capitol()) {
			iplayer.invadeCapitol(a);
			return;
		}
		
		
		a.us.get(0).extract((1-a.powerBalance));
		b.us.get(0).extract(1.0);
		Util.conquer(a.side, RND.rFloat(),RND.rFloat(), reg, besieger.us.get(0).faction());
		
		
	}
	
	
	private void init(Side A, double powA, Side B, double powB) {
		
		if (A.us.size() == 0)
			throw new RuntimeException();
		
		if (B.us.size() == 0)
			throw new RuntimeException();
		
		if (!Util.enemies(A.us.get(0).faction(), B.us.get(0).faction()))
			throw new RuntimeException();
		
		for (SideUnit u : A.us) {
			powA += u.power();
		}

		for (SideUnit u : B.us) {
			powB += u.power();
		}
		
		double pI = 1.0/(powA+powB);
		
		a.init(A, powA*pI);
		b.init(B, powB*pI);

	}
	


	

	
}
