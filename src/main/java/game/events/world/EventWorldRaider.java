package game.events.world;

import game.GAME;
import game.events.EVENTS.EventResource;
import game.faction.FACTIONS;
import game.faction.FCredits.TYPE;
import game.time.TIME;
import init.D;
import init.boostable.BOOSTABLES;
import init.config.Config;
import init.race.RACES;
import init.race.Race;
import init.resources.RESOURCES;
import init.sprite.UI.UI;
import lombok.Setter;
import settlement.main.SETT;
import settlement.stats.STATS;
import settlement.stats.standing.STANDINGS;
import snake2d.util.datatypes.COORDINATE;
import snake2d.util.datatypes.DIR;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;
import snake2d.util.misc.ACTION;
import snake2d.util.misc.CLAMP;
import snake2d.util.rnd.RND;
import snake2d.util.sprite.text.Str;
import util.data.DOUBLE;
import util.dic.DicRes;
import util.gui.misc.GButt;
import util.gui.misc.GText;
import util.info.INFO;
import view.ui.message.Message;
import view.ui.message.MessageText;
import view.world.IDebugPanelWorld;
import world.WORLD;
import world.army.WARMYD;
import world.army.WARMYD.WArmySupply;
import world.army.WDivRegional;
import world.entity.army.WArmy;
import world.regions.Region;
import world.regions.data.RD;
import world.regions.data.pop.RDRace;

import java.io.IOException;
import java.io.Serializable;

public class EventWorldRaider extends EventResource{

	private static CharSequence ¤¤protection = "¤Protection?";
	private static CharSequence ¤¤pay = "¤Pay Up";
	private static CharSequence ¤¤decline = "¤Decline Offer";
	private static CharSequence ¤¤protectionD = "¤Hello friend. We are a band of noble people, keeping the peace in your area. As you know, there are evil forces out in the world, bent on destruction and conquest. We however, are lovers of peace, and would like to offer our protective services. But soldiers have to eat, so we kindly ask you to ship us {1} {2} and lets say 10% of your goods in order for us to keep the peace. It's quite the bargain. We await your reply and looking forward to hearing from you within 1 day.";
	private static CharSequence ¤¤proInfo = "¤Our scouts report these people have a force somewhere around {0} soldiers with {1}% training and {2}% equipment. Paying the sum is a safe bet. Declining might lead to trouble.";
	private static CharSequence ¤¤Rebels = "¤Rebels!";
	private static CharSequence ¤¤HasSpawned = "¤An army of rebels have been spotted. They are marching towards our capitol.";
	private static CharSequence ¤¤protectionFail = "¤Hello again, you must have forgot to pay. Very unwise. Never mind, we will now have to come and collect what is owed ourselves. I will see you soon.";
	private static CharSequence ¤¤Chance = "¤Raid Chance"; 
	private static CharSequence ¤¤ChanceD = "¤The chances of getting raided yearly. Raids spawn from a neighbouring rebel region. If there are none, chances will be low. This chance goes up with your population, riches, and size of your kingdom. It goes down by with your garrison size and victories, although when they occur, they will be better equipped and trained to take you on. Raiders can be payed off to avoid a confrontation."; 
	
	private double timeUntilNext = 16;
	private Timer event;
	
	static {
		D.ts(EventWorldRaider.class);
	}
	
	EventWorldRaider(){
		
		IDebugPanelWorld.add("Raid", new ACTION() {
			
			@Override
			public void exe() {
				if (event != null)
					return;
				spawn();
				if (event != null)
					event.second = TIME.currentSecond()-TIME.secondsPerDay*0.75;
			}
		});

	}
	
	@Override
	protected void save(FilePutter file) {
		file.d(timeUntilNext);
		file.bool(event != null);
		if (event != null) {
			file.object(event);
		}
	}
	
	@Override
	protected void load(FileGetter file) throws IOException {
		timeUntilNext = file.d();
		if (file.bool()) {
			event = (Timer) file.object();
		}
	}
	
	@Override
	protected void clear() {
		timeUntilNext = 16;
		event = null;
	}
	
	public final DOUBLE CHANCE = new Chance();

	public void lockChance(double chance) {
		Chance raidChance = (Chance) CHANCE;

		raidChance.setLocked(true);
		raidChance.setD(chance);
	}

	public void setChanceMulti(double multi) {
		Chance raidChance = (Chance) CHANCE;
		raidChance.setMulti(multi);
	}

	protected static int getRebelAmount() {
		int men = RD.MILITARY().garrison.get(FACTIONS.player().capitolRegion());
		men += (STATS.POP().POP.data().get(null)-men)/30;

		for (WArmy a2 : FACTIONS.player().armies().all()) {
			if (a2.region() == FACTIONS.player().capitolRegion()) {
				men += WARMYD.men(null).get(a2);
			}else if(a2.region() != null && a2.region().faction() == FACTIONS.player())
				men += 0.5* WARMYD.men(null).get(a2);
			else
				men += 0.25* WARMYD.men(null).get(a2);
		}

		if (men < 10)
			men = 10;

		return men;
	}

	@Override
	protected void update(double ds) {
		
		if (event != null) {
			if (event.hasExpired()) {
				
				WArmy a = spawn(event.size, event.training, event.gear);
				event = null;
				if (a != null) {
					a.besiege(FACTIONS.player().capitolRegion());
					MessageText m = new MessageText(¤¤Rebels, ¤¤protectionFail);
					m.paragraph(Str.TMP.clear().add(¤¤HasSpawned));
					SETT.INVADOR().increaseWins();
					m.send();
				}
			}
		}else {
			
			timeUntilNext -= ds*CHANCE.getD()*TIME.secondsPerDayI;
			
			if (timeUntilNext < 0) {
				timeUntilNext += 16;
				spawn();
			}
		}
	}
	
	
	private void spawn() {
		
		double garrison = getRebelAmount();
		garrison *= 1 + 0.1*RND.rFloat()*SETT.INVADOR().wins();
		garrison *= 1 + RND.rFloat()*0.1;
		if (garrison < 10)
			garrison = 10;

		
		double q = 0.1 + SETT.INVADOR().wins()/10.0;
		q = CLAMP.d(q, 0, 1);
		
		double training = CLAMP.d(q*RND.rFloat1(0.2), 0, 1);
		double gear = CLAMP.d(q*RND.rFloat1(0.2), 0, 1);
		
		
		int ransom = (int) (RESOURCES.ALL().size()*2.0*STATS.POP().POP.data().get(null)*RND.rFloat1(0.2));
		
		Timer timer = new Timer(ransom, (int)garrison, training, gear);
		event = timer;
		new MProtection(timer).send();
	}


	
	private WArmy spawn(int men, double training, double gear) {
		COORDINATE c = WORLD.PATH().rndDist(FACTIONS.player().capitolRegion().cx(), FACTIONS.player().capitolRegion().cy(), 5);
		
		if (c == null)
			return null;
		
		WArmy a = WORLD.ARMIES().createRebel(c.x(), c.y());
		if (a == null) {
			GAME.Notify(c.x() + " " + c.y());
			return null;
		}
		
		double raceTot = 0;
		Race biggest = RACES.all().get(0);
		int b = 0;
		
		Region start = WORLD.REGIONS().map.get(c);
		if (start == null)
			start = FACTIONS.player().capitolRegion();
		
		for (RDRace r : RD.RACES().all) {
			raceTot += r.pop.get(start);
			if (r.pop.get(start) > b) {
				biggest = r.race;
			}
		}
		
		double menLeft = 0;
		for (RDRace r : RD.RACES().all) {
		
			menLeft += men*r.pop.get(start)/raceTot;
			
			while (menLeft > 2 && a.divs().canAdd()) {
				
				
				int am = CLAMP.i((int)menLeft, 0, Config.BATTLE.MEN_PER_DIVISION);
				
				WDivRegional d = WORLD.ARMIES().regional().create(r.race, (double)am/Config.BATTLE.MEN_PER_DIVISION, 0,0, a);
				d.randomize(gear, (int)(training*15));
				d.menSet(d.menTarget());
				menLeft -= a.divs().get(a.divs().size()-1).menTarget();
			}
		}
		
		while (WARMYD.men(null).get(a) < men && a.divs().canAdd()) {
			
			
			int am = CLAMP.i((men-WARMYD.men(null).get(a)), 0, Config.BATTLE.MEN_PER_DIVISION);
			if (am < 1)
				break;
			
			WDivRegional d = WORLD.ARMIES().regional().create(FACTIONS.player().race(), (double)am/Config.BATTLE.MEN_PER_DIVISION, 0,0, a);
			d.randomize(gear, (int)(training*15));
			d.menSet(d.menTarget());
		}
		
		a.name.clear().add(biggest.info.armyNames.rnd());
		
		for (WArmySupply s : WARMYD.supplies().all) {
			s.current().set(a, s.max(a));
		}
		
		return a;
	}
	
	public static class Chance implements DOUBLE {
		private final INFO info = new INFO(¤¤Chance, ¤¤ChanceD);
		private int upI = -200;
		private double cache = 0;

		@Setter
		private boolean locked = false;

		@Setter
		private double multi = 1.0d;

		public void setD(double chance) {
			this.cache = chance;
		}

		@Override
		public double getD() {
			if (!locked && GAME.updateI() - upI > 200) {
				upI = GAME.updateI();



				cache = getChance()*TIME.years().bitConversion(TIME.days());
			}
			return cache * multi;
		}

		private double getChance() {
			double chance = 0.125*0.5;

			if (STATS.POP().POP.data().get(null) <= 50)
				return 0;

			if (FACTIONS.player().credits().credits() < 0)
				return 0;
			chance *= Math.pow(CLAMP.d((STATS.POP().POP.data().get(null)-50)/1500.0, 0, 1), 0.5);
			chance *= 0.5 + 0.5*STATS.GOVERN().RICHES.data().getD(null);
			chance *= 1.0 - 0.2*CLAMP.i(SETT.INVADOR().wins(), 0, 4);
			chance *= STANDINGS.CITIZEN().current();

			if (chance == 0)
				return 0;

			double armyValue = 4.0*getRebelAmount()/(STATS.POP().POP.data().get(null) + 1.0);
			armyValue = 1.0 - 0.95*CLAMP.d(armyValue, 0, 1);
			chance *= armyValue;
			return chance /= BOOSTABLES.CIVICS().RAIDING.get(null, null);
		}

		@Override
		public INFO info() {
			return info;
		};
	}
	
	private static class MProtection extends Message{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final Timer timer;
		
		public MProtection(Timer timer) {
			super(¤¤protection);
			this.timer = timer;
		}

		@Override
		protected RENDEROBJ makeSection() {
			GuiSection section = new GuiSection();
			
			Str t = Str.TMP;
			t.clear().add(¤¤protectionD);
			t.insert(1, timer.credits);
			t.insert(2, DicRes.¤¤Currs);
			
			section.add(new GText(UI.FONT().M, ""+t).setMaxWidth(WIDTH),0,0);
			
			
			t.clear().add(¤¤proInfo);
			t.insert(0, timer.size);
			t.insert(1, (int)(100*timer.training));
			t.insert(2, (int)(100*timer.gear));
			
			section.addDown(8, new GText(UI.FONT().M, ""+t).setMaxWidth(WIDTH));
			
			
			section.addRelBody(48, DIR.S, new GButt.ButtPanel(¤¤pay) {
				
				@Override
				protected void renAction() {
					activeSet(!timer.hasExpired());
				}
				
				@Override
				protected void clickA() {
					if (!timer.hasExpired()) {
						FACTIONS.player().credits().inc(-timer.credits, TYPE.MISC);
						SETT.ROOMS().STOCKPILE.removeFromEverywhere(0.1, -1l, FACTIONS.player().res().outTribute);
						GAME.events().world.raider.event = null;
						close();
					}
					
					
				}
				
			});
			
			section.addRelBody(4, DIR.S, new GButt.ButtPanel(¤¤decline) {
				
				@Override
				protected void renAction() {
					activeSet(!timer.hasExpired());
				}
				
				@Override
				protected void clickA() {
					close();
				}
				
			});
			
			
			return section;
		}
		
	}
	
	private static class Timer implements Serializable{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final int credits;
		private double second;
		private int size;
		private double training;
		private double gear;
		
		public Timer(int cash, int size, double training, double gear) {
			credits = cash;
			second = TIME.currentSecond();
			this.size = size;
			this.training = training;
			this.gear = gear;
		}
		
		public boolean hasExpired() {
			return TIME.currentSecond() - second >= TIME.secondsPerDay;
		}
		
	}
}
