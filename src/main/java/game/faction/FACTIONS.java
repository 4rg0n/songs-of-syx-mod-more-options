package game.faction;

import game.GAME.GameResource;
import game.Profiler;
import game.VERSION;
import game.faction.diplomacy.Diplomacy;
import game.faction.npc.FactionNPC;
import game.faction.npc.UpdaterNPC;
import game.faction.npc.ruler.ROpinions;
import game.faction.player.PRel;
import game.faction.player.Player;
import game.faction.trade.TradeManager;
import game.time.TIME;
import init.D;
import init.RES;
import snake2d.LOG;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;
import snake2d.util.misc.ACTION;
import snake2d.util.sets.ArrayList;
import snake2d.util.sets.KeyMap;
import snake2d.util.sets.LIST;
import util.updating.IUpdater;
import view.interrupter.IDebugPanel;
import world.log.WLogger;
import world.regions.Region;
import world.regions.data.RD;

import java.io.IOException;

public class FACTIONS extends GameResource {

	public static final int MAX = 64;
	public static final int NPCS_MAX = MAX-1;
	private static FACTIONS self;
	
	
	private final IUpdater updater = new IUpdater(MAX, TIME.days().bitSeconds()/4) {

		@Override
		protected void update(int i, double timeSinceLast) {
			if (all.get(i).isActive() && all.get(i).capitolRegion() != null)
				all.get(i).update(timeSinceLast);
			
		}
	};
	
	private final ArrayList<Faction> all = new ArrayList<>(MAX);
	private final ArrayList<Faction> active = new ArrayList<>(MAX);
	private final ArrayList<FactionNPC> activeNPC = new ArrayList<>(MAX-1);
	private final ArrayList<FactionNPC> dormantNPC = new ArrayList<>(MAX - 1);
	private final Player player;
	private final FactionResource npcManager;
	public final UpdaterNPC ncpUpdater;
	private FactionNPC otherFaction;
	
	private Diplomacy diplomacy;

	private static CharSequence ¤¤sim = "Simulating factions";
	
	static {
		D.ts(FACTIONS.class);
	}
	
	public FACTIONS(KeyMap<Double> boosts) throws IOException{
		self = this;
		this.player = new Player(all, boosts);
		ncpUpdater = new UpdaterNPC();
		for (int i = 1; i < MAX; i++) {
			dormantNPC.add(new FactionNPC(all, ncpUpdater));
			
		}
		active.add(player);
		otherFaction = dormantNPC.get(0);

		npcManager = new TradeManager(this);
		diplomacy = new Diplomacy();
		
		new Initer(all);
		FactionProfileFlusher.load(FACTIONS.player());
		ROpinions.init();
		
		IDebugPanel.add("Factions Prime", new ACTION() {
			
			@Override
			public void exe() {
				prime();
			}
		});
	}
	
	@Override
	protected void save(FilePutter file) {
		for (Faction f : all)
			f.save(file);
		updater.save(file);
		npcManager.save(file);
		diplomacy.save(file);
		file.i(otherFaction.index());
		
	}

	@Override
	protected void load(FileGetter file) throws IOException {
		activeNPC.clear();
		active.clear();
		dormantNPC.clear();
		
		for (Faction f : all) {
			f.load(file);
			if (f.isActive())
				active.add(f);
			if (f instanceof FactionNPC) {
				if (f.isActive())
					activeNPC.add((FactionNPC) f);
				else
					dormantNPC.add((FactionNPC) f);
			}
		}
		
		for (int i = 0; i < activeNPC.size(); i++) {
			if (activeNPC.get(i).realm().regions() == 0) {
				LOG.ln("here2!");
				for (Faction.FactionActivityListener li: Faction.FactionActivityListener.all)
					li.remove(activeNPC.get(i));
				((Faction)activeNPC.get(i)).setActive(false);
				activeNPC.remove(i);
				i--;
			}else if (activeNPC.get(i).realm().capitol() == null) {
				LOG.ln("here2!");
				for (Faction.FactionActivityListener li: Faction.FactionActivityListener.all)
					li.remove(activeNPC.get(i));
				((Faction)activeNPC.get(i)).setActive(false);
				activeNPC.remove(i);
				i--;
			}
		}
		
		updater.load(file);
		npcManager.load(file);
		diplomacy.load(file);
		
		if (!VERSION.versionIsBefore(64, 41))
			otherFaction = (FactionNPC) all.get(file.i());
		
	}
	
	

	@Override
	protected void update(float ds, Profiler prof) {
		prof.logStart(updater.getClass());
		updater.update(ds);
		prof.logEnd(updater.getClass());
		
		prof.logStart(npcManager.getClass());
		npcManager.update(ds, null);
		prof.logEnd(npcManager.getClass());
		
		prof.logStart(player.getClass());
		player.updateSpecial(ds, prof);
		prof.logEnd(player.getClass());
	}
	
	public static Player player() {
		return self.player;
	}

	public static Faction getByIndex(int index) {
		return self.all.get(index);
	}
	
	public static Diplomacy DIP() {
		return self.diplomacy;
	}
	
//	public static TradePrices prices() {
//		return self.tradePrices;
//	}
//	
//	public static TradePrices pricesP() {
//		return self.tradePricesPotential;
//	}
	
	public void prime() {
		
		RES.loader().print(¤¤sim);
		
		int a = 50;
				
		for (int i = 0; i < a; i++) {
			RES.loader().print(¤¤sim + ": " + (int)(100*((i*2+a*2)/(double)(a*4))) + "%");
			
			for (FactionNPC f : FACTIONS.NPCs()) {
				RD.UPDATER().shipAll(f, 1.0);
				f.stockpile.update(f, TIME.secondsPerDay);
			}
			
			if (i % 4 == 0) {
				((TradeManager)self.npcManager).prime();
			}
		}
		((TradeManager)self.npcManager).prime();
	
	}
	
	public static FactionNPC activateNext(Region capitol) {
		
		debug();
		if (capitol.realm() != null)
			throw new RuntimeException();
		
		FactionNPC ff = self.dormantNPC.removeOrdered(0);
		
		capitol.fationSet(ff);
		
		
		self.activeNPC.add(ff);
		self.active.add(ff);
		
		
		
		
		capitol.info.name().clear().add(ff.name);
		
		((Faction)ff).setActive(true);
		
		ff.generate(null, true);
		
		for (Faction.FactionActivityListener li: Faction.FactionActivityListener.all)
			li.add(ff);
		
		debug();
		
		return ff;
		
	}
	
	public static LIST<FactionNPC> NPCs(){
		return self.activeNPC;
	}
	
	public static LIST<Faction> active(){
		return self.active;
	}
	
	public static int activateAvailable() {
		return self.dormantNPC.size();
	}
	
	public static void debug() {
		for (Faction f : self.activeNPC) {
			if (!f.isActive())
				throw new RuntimeException(f.isActive() + " " + f.realm().all().size() + " " + f.realm().capitol());
			if (f.realm().all().size() == 0)
				throw new RuntimeException(f.isActive() + " " + f.realm().all().size() + " " + f.realm().capitol());
			if (f.realm().capitol() == null)
				throw new RuntimeException(f.isActive() + " " + f.realm().all().size() + " " + f.realm().capitol());
		}
	}
	
	public static void remove(FactionNPC faction, boolean log) {
		
		debug();
		if (!faction.isActive())
			return;
		
		((Faction)faction).setActive(false);
		if (log)
			WLogger.factionDestroyed(faction);
		
		self.activeNPC.remove(faction);
		self.active.remove(faction);
		self.dormantNPC.add(faction);
		
		for (Faction.FactionActivityListener li: Faction.FactionActivityListener.all)
			li.remove(faction);
		

		
		debug();
	}

	public static void otherFactionSet(FactionNPC faction) {
		self.otherFaction = faction;
	}
	
	public static FactionNPC otherFaction() {
		return self.otherFaction;
	}
	
	public static PRel pRel() {
		return self.player.rel;
	}
}
