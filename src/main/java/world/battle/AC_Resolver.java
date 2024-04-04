package world.battle;

import game.GAME;
import game.faction.FACTIONS;
import game.faction.Faction;
import game.faction.diplomacy.DIP;
import game.faction.npc.FactionNPC;
import game.faction.royalty.opinion.ROPINIONS;
import init.config.Config;
import init.race.RACES;
import init.resources.RESOURCES;
import lombok.Setter;
import settlement.main.SETT;
import settlement.stats.Induvidual;
import settlement.stats.STATS;
import settlement.stats.equip.EquipBattle;
import snake2d.util.misc.CLAMP;
import snake2d.util.sets.ArrayListGrower;
import view.main.VIEW;
import world.army.AD;
import world.army.ADSupplies.ADArtillery;
import world.army.ADSupply;
import world.army.WDIV;
import world.army.util.WDivGeneration;
import world.battle.spec.WBattleResult;
import world.battle.spec.WBattleResult.BATTLE_RESULT;
import world.battle.spec.WBattleSide;
import world.battle.spec.WBattleSiege;
import world.battle.spec.WBattleUnit;
import world.map.regions.Region;
import world.region.RD;
import world.region.RDOutput.RDResource;
import world.region.pop.RDRace;

import java.util.Arrays;

public class AC_Resolver {

	// MODDED
	@Setter
	private static double playerLootMulti = 1.0;
	// MODDED
	@Setter
	private static double enemyLootMulti = 1.0;

	private final BSide aa = new BSide();
	private final BSide bb = new BSide();
	
	private final PBattle pbattle;
	private final PBattleSiege pSiege;
	private final AD_Shipper shipper;
	private final AC_URetreat retreater;
	private final AC_UReg regger;
	
	AC_Resolver(PUnitFactory uf){
		
		this.shipper = new AD_Shipper();
		this.pbattle = new PBattle(uf, shipper);
		this.regger = new AC_UReg();
		this.pSiege = new PBattleSiege(uf, shipper, regger);
		this.retreater = new AC_URetreat();
		
	}
	
	public void battle(Side winner, int[] wlosses, Side looser, int[] llosses) {
		BattleListener.notify(winner, wlosses, looser, llosses);
		BSide w = aa.init(winner, wlosses);
		BSide l = bb.init(looser, llosses);
		l.extract(llosses, null);
		autoLosses(w, wlosses);
		autoLosses(l, llosses);
		retreater.retreat(l.side);
		
		if (w.side.isPlayer) {
			pbattle.init(w, l, BATTLE_RESULT.VICTORY, false);
		}else if (l.side.isPlayer) {
			pbattle.init(l, w, BATTLE_RESULT.DEFEAT, false);
		}else {
			shipper.ship(w.side, l.side, l.capturedRaces, l.lostRes);
		}
		
	}
	
	public void manualBattle(Side player, Induvidual[][] survivors, Side enemy, int[] elosses, int[] ecaptured, BATTLE_RESULT type) {
		int [] plosses = new int[player.divs()];
		for (int i = 0; i < player.divs(); i++) {
			plosses[i] = player.div(i).men()-survivors[i].length;
		}
		
		
		BSide p = aa.init(player, plosses);
		BSide e = bb.init(enemy, elosses);
		
		if (type == BATTLE_RESULT.VICTORY) {
			BattleListener.notify(player, plosses, enemy, elosses);
			
			e.extract(elosses, ecaptured, null);
			
			//shipper.ship(p.side, e.side, e.capturedRaces, e.lostRes);
		}else {
			BattleListener.notify(enemy, elosses, player, plosses);
			p.extract(plosses, null);
			
			//shipper.ship(e.side, p.side, p.capturedRaces, p.lostRes);
		}
		
		for (int i = 0; i < player.divs(); i++) {
			WDIV div = player.div(i);
			if (div == null)
				continue;
			div.resolve(survivors[i]);
		}
		
		for (int i = 0; i < enemy.divs(); i++) {
			WDIV div = enemy.div(i);
			if (div == null)
				continue;
			int am = CLAMP.i(div.men()-(elosses[i]), 0, div.men());
			div.resolve(am, 0);
		}
		
		if (type == BATTLE_RESULT.VICTORY) {
			e.extract(elosses, null);
			pbattle.init(p, e, type, false);
		}else {
			p.extract(plosses, null);
			pbattle.init(p, e, type, false);
		}
	}
	
	public void conquer(Side winner, int[] wlosses, Side looser, int[] llosses, Region reg) {

		BattleListener.notify(winner, wlosses, looser, llosses);
		BattleListener.notify(winner, reg);
		
		if (reg == FACTIONS.player().capitolRegion()) {
	
			ArrayListGrower<WDivGeneration> divs = new ArrayListGrower<>();
			for (int i = 0; i < winner.divs(); i++) {
				divs.add(winner.div(i).generate());
			}
			
			
			Faction f = winner.unit(0).faction();
			
			SETT.INVADOR().invade(winner.unit(0).coo.x(), winner.unit(0).coo.y(), divs, (FactionNPC) f, true);
			
			for (SideUnit u : winner.us) {
				if (u.a() != null)
					u.a().disband();
			}
			return;
		}
		
		BSide w = aa.init(winner, wlosses);
		BSide l = bb.init(looser, llosses);
		l.extract(llosses, reg);
		autoLosses(w, wlosses);
		autoLosses(l, llosses);
		retreater.retreat(l.side);

		if (w.side.isPlayer) {
			pSiege.init(w, l, reg);
		}else {
			regger.processConqured(winner, Rnd.f(), reg, winner.faction);
		}
		
		
	}
	
	public void retreat(Side retreater, int[] rlosses, Side winner, int[] wlosses) {
		
		BattleListener.notify(winner, wlosses, retreater, rlosses);
		BSide w = aa.init(winner, wlosses);
		BSide l = bb.init(retreater, rlosses);
		l.extract(rlosses, null);
		autoLosses(w, wlosses);
		autoLosses(l, rlosses);
		
		this.retreater.retreat(l.side);
		
		if (w.side.isPlayer) {
			pbattle.init(w, l, BATTLE_RESULT.VICTORY, true);
		}else if (l.side.isPlayer) {
			pbattle.init(l, w, BATTLE_RESULT.RETREAT, false);
		}else {
			shipper.ship(w.side, l.side, l.capturedRaces, l.lostRes);
		}
		
	}

	
	private static void autoLosses(BSide s, int[] losses) {
		
		for (int i = 0; i < s.side.divs(); i++) {
			
			WDIV div = s.side.div(i);
			if (div == null)
				continue;
			
			int survivors = div.men()-losses[i];
			survivors = CLAMP.i(survivors, 0, div.men());
			
			double xp = 0;
			
			if (survivors > 0) {
				xp = (double)0.1*div.men()/survivors;
				xp += div.experience();
				xp = CLAMP.d(xp, 0, 1);
			}
			div.resolve(survivors, xp);
		}

	}

	
	private static class PBattle extends WBattleResult {

		private final PUnitFactory uf;
		private final AD_Shipper shipper;
		
		private BSide sPlayer;
		private BSide sEnemy;
		
		PBattle(PUnitFactory uf, AD_Shipper shipper) {
			this.player = new WBattleSide();
			this.enemy = new WBattleSide();
			this.uf = uf;
			this.shipper = shipper;
		}
		
		public void init(BSide player, BSide enemy, BATTLE_RESULT result, boolean eretreats) {
			this.result = result;
			this.sPlayer = player;
			this.sEnemy = enemy;
			uf.clear();
			initSide(uf, player, this.player);
			initSide(uf, enemy, this.enemy);

			// MODDED
			if (result == BATTLE_RESULT.VICTORY) {
				setLoot(this, enemy, 1 * AC_Resolver.playerLootMulti);
			}else {
				setLoot(this, player, -1 * AC_Resolver.enemyLootMulti);
			}
			VIEW.world().UI.battle.result(this, eretreats);
			
		}
		
		private static void initSide(PUnitFactory uf, BSide s, WBattleSide bs) {
			Arrays.fill(bs.artilleryPieces, 0);
			bs.coo.set(s.side.us.get(0).coo);
			bs.losses = 0;
			bs.lossesRetreat = 0;
			bs.men = 0;
			bs.powerBalance = 0;
			bs.units.clear();
			
			for (int ui = 0; ui < s.side.us.size(); ui++) {
				WBattleUnit u = uf.next(s.side.us.get(ui));
				bs.units.add(u);
				u.losses = s.unitLosses[ui];
				bs.losses += u.losses;
				bs.men += u.men;
			}
		}
		
		private static void setLoot(WBattleResult res, BSide side, double d) {
			for (int i = 0; i < res.capturedRaces.length; i++) {
				res.capturedRaces[i] = (int) (side.capturedRaces[i]*d);
			}
			for (int i = 0; i < res.lostResources.length; i++) {
				res.lostResources[i] = (int) (side.lostRes[i]*d);
			}
		}

		@Override
		public void accept(int[] enslave, int[] resources) {
			if (result == BATTLE_RESULT.VICTORY)
				shipper.ship(sPlayer.side, sEnemy.side, enslave, resources);
			else
				shipper.ship(sEnemy.side, sPlayer.side, sPlayer.capturedRaces, sPlayer.lostRes);
			
		}
	}
	
	private static class PBattleSiege extends WBattleSiege.Result {

		private final AC_UReg regger;
		private final PUnitFactory uf;
		private final AD_Shipper shipper;
		private BSide sPlayer;
		private BSide sEnemy;
		
		PBattleSiege(PUnitFactory uf, AD_Shipper shipper, AC_UReg regger) {
			this.player = new WBattleSide();
			this.enemy = new WBattleSide();
			this.uf = uf;
			this.shipper = shipper;
			this.regger = regger;
		}
		
		public void init(BSide player, BSide enemy, Region reg) {
			this.result = BATTLE_RESULT.VICTORY;
			boolean surrender = RD.MILITARY().garrison(reg) == 0;
			this.sPlayer = player;
			this.sEnemy = enemy;
			this.besiged = reg;
			uf.clear();
			PBattle.initSide(uf, player, this.player);
			PBattle.initSide(uf, enemy, this.enemy);
			PBattle.setLoot(this, enemy, 1);
		
			
			VIEW.world().UI.battle.result(this, surrender);
		}

		@Override
		public void accept(int[] enslave, int[] resources) {

			
			
		}

		@Override
		public void occupy(double plunderAmount, int[] enslave, int[] resources) {
			debug(plunderAmount, enslave, resources);
			shipper.ship(sPlayer.side, sEnemy.side, enslave, resources);
			regger.processConqured(sPlayer.side, plunderAmount, besiged, FACTIONS.player());
			
		}

		@Override
		public void abandon(double plunderAmount, int[] enslave, int[] resources) {
			debug(plunderAmount, enslave, resources);
			shipper.ship(sPlayer.side, sEnemy.side, enslave, resources);
			regger.processConqured(sPlayer.side, plunderAmount, besiged, null);
		}

		@Override
		public void puppet(double plunderAmount, int[] enslave, int[] resources) {
			debug(plunderAmount, enslave, resources);
			shipper.ship(sPlayer.side, sEnemy.side, enslave, resources);
			
			
			regger.processConqured(sPlayer.side, plunderAmount, besiged, null);
			
			FactionNPC f = FACTIONS.activateNext(besiged, true);
			f.generate(RD.RACES().get(FACTIONS.player().race()), true);
			ROPINIONS.OTHER().liberate(f);
			DIP.VASSAL().set(f, FACTIONS.player());
			GAME.events().world.dip.dismissWelcome(f);
			
		}
		
		private void debug(double plunderAmount, int[] enslave, int[] resources) {
//			LOG.ln(plunderAmount);
//			for (Race r : RACES.all())
//				LOG.ln(r + " " + enslave[r.index]);
//			for (RESOURCE r : RESOURCES.ALL())
//				LOG.ln(r + " " + resources[r.index()]);
		}
	}

	
	private static class BSide {
		
		public Side side;
		public final int[] capturedRaces = new int[RACES.all().size()]; 
		public final int[] lostRes = new int[RESOURCES.ALL().size()];
		public final int[] unitLosses = new int[Config.BATTLE.DIVISIONS_PER_ARMY];
		public final int[] tmpCaptured = new int[RACES.all().size()];
		
		BSide init(Side side, int[] divLosses) {
			this.side = side;
			Arrays.fill(capturedRaces, 0);
			Arrays.fill(lostRes, 0);
			
			for (int i = 0; i < side.us.size(); i++) {
				unitLosses[i] = 0;
			}
			
			for (int i = 0; i < side.divs(); i++) {
				unitLosses[side.ui(i)] += divLosses[i];
			}
			return this;
		}
		
		void extract(int[] divLosses, int[] racesCaptured, Region conqured) {
			
			for (int i = 0; i < side.divs(); i++) {
				
				WDIV div = side.div(i);
				if (div == null)
					continue;
				
				for (int ei = 0; ei < STATS.EQUIP().BATTLE_ALL().size(); ei++) {
					EquipBattle e = STATS.EQUIP().BATTLE_ALL().get(ei);
					lostRes[e.resource.index()] += divLosses[i]*div.equip(e)*e.equipMax;
				}
			}
			
			for (int ui = 0; ui < side.us.size(); ui++) {
				SideUnit u = side.us.get(ui);
				if (u.a() != null) {
					for (int ei = 0; ei < STATS.EQUIP().BATTLE_ALL().size(); ei++) {
						EquipBattle e = STATS.EQUIP().BATTLE_ALL().get(ei);
						ADSupply su = AD.supplies().get(e);
						int am = lostRes[e.resource.index()];
						am = CLAMP.i(am, 0, su.current().get(u.a()));
						su.current().set(u.a(), su.current().get(u.a())-am);
					}
				}
			}
			
			for (int ui = 0; ui < side.us.size(); ui++) {
				SideUnit u = side.us.get(ui);
				if (u.a() != null) {
					double d = (double)unitLosses[ui]/u.men;
					for (int i = 0; i < RESOURCES.SUP().ALL().size(); i++) {
						ADSupply su = AD.supplies().get(RESOURCES.SUP().ALL().get(i));
						int am = (int) (su.current().get(u.a())*d);
						su.current().set(u.a(), am);
						lostRes[su.res.index()] += am;
					}
					for (ADArtillery aa : AD.supplies().arts()) {
						for (ADSupply su : aa.sups()) {
							int am = (int) (su.current().get(u.a())*d);
							su.current().set(u.a(), am);
							lostRes[su.res.index()] += am;
						}
						
						
					}
				}
			}
			
			for (int i = 0; i < capturedRaces.length; i++) {
				capturedRaces[i] = racesCaptured[i];
				
			}
			
			if (conqured != null) {
				
				for (RDResource res: RD.OUTPUT().all) {
					lostRes[res.res.index()] += res.loot(conqured)*8;
				}
				
				for (RDRace res: RD.RACES().all) {
					capturedRaces[res.race.index()] += res.pop.get(conqured)*0.2;
				}
			}
			
		}
		
		void extract(int[] losses, Region conqured) {
			
			Arrays.fill(tmpCaptured, 0);
			for (int i = 0; i < side.divs(); i++) {
				WDIV div = side.div(i);
				if (div == null)
					continue;
				tmpCaptured[div.race().index()] += (int) (losses[i]*Math.pow(Rnd.f(), 1.5));
			}
			extract(losses, tmpCaptured, conqured);
			
		}
	}


	public boolean canRetreat(Side side) {
		return retreater.canRetreat(side);
	}

}
