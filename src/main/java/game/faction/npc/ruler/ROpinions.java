//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package game.faction.npc.ruler;

import com.github.argon.sos.moreoptions.MoreOptionsScript;
import com.github.argon.sos.moreoptions.game.booster.MoreOptionsBooster;
import game.GAME;
import game.boosting.BOOSTABLE_O;
import game.boosting.BOOSTING;
import game.boosting.BSourceInfo;
import game.boosting.BoostSpec;
import game.boosting.Boostable;
import game.boosting.BoostableCat;
import game.boosting.BoosterImp;
import game.faction.FACTIONS;
import game.faction.Faction;
import game.faction.diplomacy.DWar;
import game.faction.npc.FactionNPC;
import init.D;
import init.race.POP_CL;
import init.resources.RESOURCES;
import init.sprite.SPRITES;
import init.sprite.UI.UI;
import java.util.Iterator;
import settlement.stats.STATS;
import snake2d.util.misc.CLAMP;
import snake2d.util.rnd.RND;
import snake2d.util.sets.ArrayListGrower;
import snake2d.util.sets.LISTE;
import snake2d.util.sprite.SPRITE;
import util.dic.DicArmy;
import util.dic.DicGeo;
import util.dic.DicMisc;
import world.regions.data.RD;
import world.regions.data.pop.RDRace;

public class ROpinions {
	private static ROpinions self;
	private final ArrayListGrower<OpinionFactorE> allD = new ArrayListGrower();
	public final Boostable boostable;
	private final OpinionFactorE warDeclare;
	private final OpinionFactorE favours;
	private final OpinionFactorE peace;
	private final OpinionFactorE diplomacy;
	private final OpinionFactorE flattery;
	private final OpinionFactorE assasination;
	private final OpinionFactorE trade;
	private final OpinionFactorE liberation;

	public static void init() {
		new ROpinions();
	}

	private ROpinions() {
		this.boostable = BOOSTING.push("OPINION", 0.0, DicGeo.¤¤Opinion, DicGeo.¤¤OpinionD, UI.icons().s.soso, BoostableCat.WORLD);
		self = this;
		D.gInit(ROpinions.class);
		this.warDeclare = new OpinionFactorE(this.allD, D.g("WarD", "Declarations of war"), UI.icons().s.sword, 0.0, -2.0) {
			protected double pUpdate(Royalty roy, double value, double ds) {
				value -= ds / 18432.0;
				value = CLAMP.d(value, 0.0, 1.0);
				return value;
			}

			public double getP(double value, Royalty t) {
				return CLAMP.d(value, 0.0, 1.0);
			}
		};
		this.favours = new OpinionFactorE(this.allD, DicMisc.¤¤Favours, UI.icons().s.crown, -10.0, 10.0) {
			protected double pUpdate(Royalty roy, double value, double ds) {
				double d = ds / 1179648.0;
				if (value < 0.0) {
					value += d;
					if (value > 0.0) {
						value = 0.0;
					}
				} else if (value > 0.0) {
					value -= d;
					if (value < 0.0) {
						value = 0.0;
					}
				}

				value = CLAMP.d(value, -1.0, 3.0);
				return value;
			}

			public double getP(double value, Royalty t) {
				return 0.5 + value * 0.5;
			}
		};
		this.peace = new OpinionFactorE(this.allD, DicArmy.¤¤Peace, UI.icons().s.sprout, 0.0, 10.0) {
			protected double pUpdate(Royalty roy, double value, double ds) {
				value -= ds / 147456.0;
				value = CLAMP.d(value, 0.0, 100.0);
				return value;
			}

			public double getP(double value, Royalty t) {
				return value;
			}
		};
		this.diplomacy = new OpinionFactorE(this.allD, DicMisc.¤¤Diplomacy, UI.icons().s.flags, -10.0, 10.0) {
			protected double pUpdate(Royalty roy, double value, double ds) {
				double d = ds / 1179648.0;
				if (value < 0.0) {
					value += d;
					if (value > 0.0) {
						value = 0.0;
					}
				} else if (value > 0.0) {
					value -= d;
					if (value < 0.0) {
						value = 0.0;
					}
				}

				value = CLAMP.d(value, -1.0, 3.0);
				return value;
			}

			public double getP(double value, Royalty t) {
				return 0.5 + 0.5 * value;
			}
		};
		this.flattery = new OpinionFactorE(this.allD, D.g("Flattery"), UI.icons().s.heart, 0.0, 4.0) {
			protected double pUpdate(Royalty roy, double value, double ds) {
				double d = ds / 18432.0;
				value -= d;
				value = CLAMP.d(value, 0.0, 1.0);
				return value;
			}

			public double getP(double value, Royalty t) {
				return value;
			}
		};
		this.assasination = new OpinionFactorE(this.allD, D.g("Assassination"), UI.icons().s.alert, 0.0, -10.0) {
			protected double pUpdate(Royalty roy, double value, double ds) {
				double d = ds / 18432.0;
				value -= d;
				value = CLAMP.d(value, 0.0, 1.0);
				return value;
			}

			public double getP(double value, Royalty t) {
				return value;
			}
		};
		DWar.DWarListener var10000 = new DWar.DWarListener() {
			protected void exe(Faction a, Faction b, boolean war) {
				if (war) {
					if (a == FACTIONS.player() && b instanceof FactionNPC) {
						ROpinions.declareWar((FactionNPC)b);
					} else if (b == FACTIONS.player() && a instanceof FactionNPC) {
						ROpinions.declareWar((FactionNPC)a);
					}
				}

			}
		};
		(new BoosterImp(new BSourceInfo(D.g("Kinship"), UI.icons().s.human), 0.5, 1.5, true) {
			public double vGet(Royalty roy) {
				double d = -1.0 + 2.0 * roy.induvidual.race().pref().race(FACTIONS.player().race());
				d *= 1.0 - roy.trait(RTraits.get().tolerance);
				d = (d + 1.0) / 2.0;
				return CLAMP.d(d, 0.0, 1.0);
			}

			public double vGet(Faction f) {
				return 0.0;
			}

			public boolean has(Class<? extends BOOSTABLE_O> o) {
				return o == Royalty.class;
			}
		}).add(this.boostable);
		final BoostSpec threat = (new BoosterImp(new BSourceInfo(D.g("Threat"), UI.icons().s.muster), 1.0, -10.0, false) {
			public double vGet(Royalty roy) {
				double res = 0.0;
				res += 0.7 * (double)(FACTIONS.player().realm().all().size() - 1) / 32.0;
				res += 0.3 * (double)STATS.POP().POP.data().get(null) / 40000.0;
				return res;
			}

			public double vGet(Faction f) {
				return 0.0;
			}

			public boolean has(Class<? extends BOOSTABLE_O> o) {
				return o == Royalty.class;
			}
		}).add(this.boostable);
		final BoostSpec wealth = (new BoosterImp(new BSourceInfo(D.g("Wealth"), UI.icons().s.money), 0.0, -5.0, false) {
			public double vGet(Royalty roy) {
				double res = FACTIONS.player().credits().credits() / (140000.0 * (double)RESOURCES.ALL().size());
				return Math.min(res, 1.0);
			}

			public double vGet(Faction f) {
				return 0.0;
			}

			public boolean has(Class<? extends BOOSTABLE_O> o) {
				return o == Royalty.class;
			}
		}).add(this.boostable);
		(new BoosterImp(new BSourceInfo(D.g("Cruelty"), UI.icons().s.death), 0.0, -2.0, false) {
			public double vGet(Royalty roy) {
				RDRace r = RD.RACE(roy.induvidual.race());
				double d = r.massacre.realm.getD(FACTIONS.player()) + r.exile.realm.getD(FACTIONS.player()) * 0.5 + r.sanction.realm.getD(FACTIONS.player()) * 0.25;
				d *= roy.trait(RTraits.get().mercy);
				return d;
			}

			public double vGet(Faction f) {
				return 0.0;
			}

			public boolean has(Class<? extends BOOSTABLE_O> o) {
				return o == Royalty.class;
			}
		}).add(this.boostable);
		this.trade = new OpinionFactorE(this.allD, DicGeo.¤¤TradePartner, UI.icons().s.wheel, 0.0, 2.0, false) {
			protected double pUpdate(Royalty roy, double value, double ds) {
				if (FACTIONS.DIP().trades(FACTIONS.player(), roy.court.faction)) {
					double d = roy.trait(RTraits.get().competence);
					value += d * ds / 18432.0;
				} else {
					value -= ds / 73728.0;
				}

				value = CLAMP.d(value, 0.0, 1.0);
				return value;
			}

			public double getP(double value, Royalty t) {
				return value;
			}
		};
		OpinionFactorE var3 = new OpinionFactorE(this.allD, DicArmy.¤¤War, UI.icons().s.fist, 0.0, -5.0) {
			protected double pUpdate(Royalty roy, double value, double ds) {
				if (roy.isKing() && FACTIONS.DIP().war.is(FACTIONS.player(), roy.court.faction)) {
					value += ds / 18432.0;
				} else {
					value -= ds / 73728.0;
				}

				value = CLAMP.d(value, 0.0, 1.0);
				return value;
			}

			public double getP(double value, Royalty t) {
				return value;
			}
		};
		this.liberation = new OpinionFactorE(this.allD, DicArmy.¤¤Vassal, UI.icons().s.happy, 0.0, 100.0) {
			protected double pUpdate(Royalty roy, double value, double ds) {
				value -= value * ds / 147456.0;
				value = CLAMP.d(value, 0.0, 1.0);
				return value;
			}

			public double getP(double value, Royalty t) {
				double d = wealth.get(t) + threat.get(t);
				d = -d;
				if (d <= 0.0) {
					return 0.0;
				} else {
					double v = d * value * 2.0;
					v = CLAMP.d(v, 0.0, d + 1.0);
					return v / 100.0;
				}
			}
		};

		(new MoreOptionsBooster(new BSourceInfo(D.g("More Options"), UI.icons().s.cog), 0.0, 10000, false) {
			public boolean has(Class<? extends BOOSTABLE_O> o) {
				return o == Royalty.class;
			}
		}).add(this.boostable);
	}

	static OpinionData createData() {
		return new OpinionData(self.allD.size());
	}

	public static double current(FactionNPC f) {
		return current(f.court().king().roy());
	}

	public static double tradeCost(FactionNPC f) {
		if (FACTIONS.DIP().war.is(f, FACTIONS.player())) {
			return 0.95;
		} else {
			double tt = 1.0 - self.trade.getP(0.0, f.court().king().roy());
			tt = 0.5 + 0.5 * tt;
			double op = current(f.court().king().roy());
			if (op < 0.0) {
				op = 0.5;
			} else {
				op = 0.5 / (1.0 + op * 2.0);
			}

			return op * tt;
		}
	}

	public static Boostable GET() {
		return self.boostable;
	}

	public static double current(Royalty roy) {
		return self.boostable.get(roy);
	}

	public static double attackValue(FactionNPC f) {
		double d = current(f.court().king().roy());
		d *= 1.0 + f.court().king().roy().trait(RTraits.get().war);
		return d < 0.0 ? -d / 10.0 : 0.0;
	}

	public static void update(Royalty roy, double ds) {
		for(int i = 0; i < self.allD.size(); ++i) {
			((OpinionFactorE)self.allD.get(i)).update(roy, ds);
		}

	}

	public static void flatter(Royalty roy, double am) {
		self.flattery.inc(roy, am * (1.0 + roy.trait(RTraits.get().pride)));
	}

	public static double flattery(Royalty roy) {
		return self.flattery.vGet(roy) / self.flattery.max();
	}

	public static void assasinate(Royalty roy) {
		double ch = 0.5;
		ch = 1.0 - Math.sqrt(self.assasination.data(roy));
		ch *= 0.5;
		if (ch > (double)RND.rFloat()) {
			roy.kill(false);
			GAME.count().ROYALTIES_KILLED.inc(1);
		} else {
			self.assasination.inc(roy, 0.25);
		}

	}

	public static void liberate(FactionNPC f) {
		Iterator var2 = f.court().all().iterator();

		while(var2.hasNext()) {
			Royalty r = (Royalty)var2.next();
			self.liberation.set(r, r.isKing() ? 1.0 : 0.5);
		}

	}



	public static void makeDeal(FactionNPC f, double generousity) {
		Iterator var4 = f.court().all().iterator();

		while(var4.hasNext()) {
			Royalty r = (Royalty)var4.next();
			makeDeal(r, r.isKing() ? generousity : generousity * 0.25);
		}

	}

	public static void makeDeal(Royalty roy, double generousity) {
		double d = 0.5 + 0.5 * roy.trait(RTraits.get().honesty);
		self.diplomacy.inc(roy, generousity * d);
	}

	public static void makeDealRaw(Royalty roy, double generousity) {
		self.diplomacy.inc(roy, generousity / self.diplomacy.max());
	}

	public static void favour(FactionNPC f, double generousity) {
		generousity /= self.favours.max();
		Iterator var4 = f.court().all().iterator();

		while(var4.hasNext()) {
			Royalty r = (Royalty)var4.next();
			self.favours.inc(r, r.isKing() ? generousity : generousity * 0.25);
		}

	}

	public static double dealIncrease(FactionNPC f, double v) {
		double d = 0.5 + 0.5 * f.court().king().roy().trait(RTraits.get().honesty);
		return self.diplomacy.max() * v * d;
	}

	public static void declareWar(FactionNPC f) {
		Iterator var2 = f.court().all().iterator();

		while(var2.hasNext()) {
			Royalty r = (Royalty)var2.next();
			self.warDeclare.inc(r, r.isKing() ? 1.0 : 0.25);
			self.peace.set(r, 0.0);
		}

	}

	public static void makePeace(FactionNPC f) {
		double d = current(f);
		if (d < 0.0) {
			double p = DWar.peaceValue(f);
			double tar = -d;
			tar = -d + 0.5 + p;
			tar /= self.peace.max();
			self.peace.inc(f.court().king().roy(), tar);
		}

	}

	public static void trade(FactionNPC s, int price) {
		double d = (double)price / (15000.0 * (double)RESOURCES.ALL().size());
		Iterator var5 = s.court().all().iterator();

		while(var5.hasNext()) {
			Royalty r = (Royalty)var5.next();
			self.trade.inc(r, r.isKing() ? d : 0.25 * d);
		}

	}

	public abstract static class OpinionFactorE extends BoosterImp {
		private final int index;

		OpinionFactorE(LISTE<OpinionFactorE> allD, CharSequence name, SPRITE icon, double min, double max, boolean isMul) {
			super(new BSourceInfo(name, icon), min, max, isMul);
			this.index = allD.add(this);
			this.add(ROpinions.self.boostable);
		}

		OpinionFactorE(LISTE<OpinionFactorE> allD, CharSequence name, SPRITE icon, double min, double max) {
			this(allD, name, icon, min, max, false);
		}

		public abstract double getP(double var1, Royalty var3);

		public void inc(Royalty roy, double doo) {
			double[] var10000 = roy.data.data;
			int var10001 = this.index;
			var10000[var10001] += doo;
		}

		public void set(Royalty roy, double doo) {
			roy.data.data[this.index] = doo;
		}

		void update(Royalty roy, double ds) {
			roy.data.data[this.index] = this.pUpdate(roy, roy.data.data[this.index], ds);
		}

		double data(Royalty roy) {
			return roy.data.data[this.index];
		}

		protected abstract double pUpdate(Royalty var1, double var2, double var4);

		public double vGet(Royalty t) {
			return this.getP(t.data.data[this.index], t);
		}

		public double vGet(Faction f) {
			return 0.0;
		}

		public boolean has(Class<? extends BOOSTABLE_O> o) {
			return o == Royalty.class;
		}
	}
}
