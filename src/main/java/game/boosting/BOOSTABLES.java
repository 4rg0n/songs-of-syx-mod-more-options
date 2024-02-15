package game.boosting;

import init.D;
import init.paths.PATHS;
import init.paths.PATHS.ResFolder;
import init.sprite.SPRITES;
import init.sprite.UI.Icon;
import init.sprite.UI.UI;
import snake2d.util.file.Json;
import snake2d.util.sets.*;
import snake2d.util.sprite.SPRITE;
import util.dic.DicMisc;
import util.info.INFO;
import util.keymap.RCollection;

import java.io.IOException;

public class BOOSTABLES {

	static BOOSTABLES self;
	final ArrayList<Boostable> all = new ArrayList<>(512);
	
	{
		D.gInit(this);
	}
	
	private final Physics physics = new Physics();
	private final Battle battle  = new Battle();
	private final Behaviour behaviour = new Behaviour();
//	private final Rates rates = new Rates();
	private final Civic civics = new Civic();
	private final Start start = new Start();
	
	
	public final BoostableCat ROOMS = new BoostableCollection("ROOM", DicMisc.¤¤Buildings);
	public final LIST<BoostableCat> collections = new ArrayList<>(physics, behaviour, battle, civics, ROOMS, start);

	
	private BOOSTABLES() throws IOException{
		self = this;
	}
	
	static void init() throws IOException {
		new BOOSTABLES();
		
	}
	
	public static Physics PHYSICS() {
		return self.physics;
	}
	
	public static Battle BATTLE() {
		return self.battle;
	}
	
	public static Behaviour BEHAVIOUR() {
		return self.behaviour;
	}
//	
//	public static Rates RATES() {
//		return self.rates;
//	}
	
	public static Civic CIVICS() {
		return self.civics;
	}
	
	public static BoostableCat ROOMS() {
		return self.ROOMS;
	}
	
	public static Start START() {
		return self.start;
	}
	
	public static LIST<Boostable> all(){
		return self.all;
	}
	
//	public static LIST<Boostable> military(){
//		return self.military;
//	}
	
	public static LIST<BoostableCat> colls(){
		return self.collections;
	}
	

	
	public static final class Physics extends BoostableCollection{
		
		Physics(){
			super("PHYSICS", D.g("Physics"));
		}
		
		public final Boostable MASS = make("MASS", 80.0, UI.icons().s.law,
				D.g("PHYSICS_MASS", "Weight"),
				D.g("PHYSICS_MASS_D", "The Weight of a subject."));

			public final Boostable STAMINA = make("STAMINA", 1.0, UI.icons().s.heat,
				D.g("PHYSICS_STAMINA", "Stamina"),
				D.g("PHYSICS_STAMINA_D", "How far and a subject can walk or run before needing to rest."));

			public final Boostable SPEED = make("SPEED", 4.5, UI.icons().s.arrow_right,
				D.g("PHYSICS_SPEED", "Speed"),
				D.g("PHYSICS_SPEED_D", "The speed of a subject, expressed in tiles per second."));

			public final Boostable ACCELERATION = make("ACCELERATION", 3.0, UI.icons().s.speed,
				D.g("PHYSICS_ACCELERATION", "Acceleration"),
				D.g("PHYSICS_ACCELERATION_D", "How fast a subject speeds up"));

			public final Boostable HEALTH = make("HEALTH", 1.0, UI.icons().s.heart,
				D.g("PHYSICS_HEALTH", "Health"),
				D.g("PHYSICS_HEALTH_D", "General health of subject. Determines the subject's ability to fend off a disease and heal injuries."));

			public final Boostable DEATH_AGE = make("DEATH_AGE", 100.0, UI.icons().s.death,
				D.g("PHYSICS_DEATH_AGE", "Lifespan"),
				D.g("PHYSICS_DEATH_AGE_D", "The maximum amount of years a subject can live for."));

			public final Boostable RESISTANCE_HOT = make("RESISTANCE_HOT", 0.8, UI.icons().s.heat,
				D.g("PHYSICS_RESISTANCE_HOT", "Heat Resistance"),
				D.g("PHYSICS_RESISTANCE_HOT_D", "The ability for a subject to endure hot temperatures."));

			public final Boostable RESISTANCE_COLD = make("RESISTANCE_COLD", 0.5, UI.icons().s.ice,
				D.g("PHYSICS_RESISTANCE_COLD", "Cold Resistance"),
				D.g("PHYSICS_RESISTANCE_COLD_D", "The ability for a subject to endure cold temperatures."));
			
			public final Boostable SOILING = make("SOILING", 0.125, UI.icons().s.ice,
					D.g("PHYSICS_SOILING", "Soiling"),
					D.g("PHYSICS_SOILING_D", "The rate at which a subject becomes dirty."));
		
	}
	
	public static final class Battle extends BoostableCollection{
		
		public final Boostable OFFENCE = make("OFFENCE_SKILL", 0.2, UI.icons().s.sword,
			D.g("BATTLE_OFFENCE_SKILL", "Offence Skill"),
			D.g("BATTLE_OFFENCE_SKILL_D", "A soldier's offensive skill determines the ability to land a successful hit on an enemy and evade the enemy's armour."));
		
		public final Boostable DEFENCE = make("DEFENCE_SKILL", 0.2, UI.icons().s.shield,
			D.g("BATTLE_DEFENCE_SKILL", "Defence Skill"),
			D.g("BATTLE_DEFENCE_SKILL_D", "A soldiers defensive skill is used when attacked frontally to block or dodge an enemy's attack."));
		
		public final Boostable BLUNT_ATTACK = make("BLUNT_ATTACK", 40, UI.icons().s.fist,
			D.g("BATTLE_BLUNT_DAMAGE", "Force"),
			D.g("BATTLE_BLUNT_DAMAGE_D", "The force of any attack. The force is multiplied on the damage types when attacking. Force also creates knock-back that can disrupt enemy formations and demoralize them."));
		
		public final Boostable BLUNT_DEFENCE = make("BLUNT_DEFENCE", 40, UI.icons().s.squatter,
			D.g("BATTLE_BLUNT_DEFENCE", "Hit Points"),
			D.g("BATTLE_BLUNT_DEFENCE_D", "Ability to absorb damage."));
			
		public final Boostable ATTACK_RATE = make("ATTACK_RATE", 1.0, UI.icons().s.sword,
				D.g("ATTACK_RATE", "Offence Skill"),
				D.g("ATTACK_RATE_D", "The rate at which a soldier performs melee attacks."));
			
		public final Boostable MORALE = make("MORALE", 4.0, UI.icons().s.standard,
			D.g("BATTLE_MORALE", "Morale"),
			D.g("BATTLE_MORALE_D", "A soldier's morale is what determines how long, and against what odds and setbacks, it will fight for before taking flight."));
		
		public Tuple<Boostable, Boostable> SKILL = new Tuple.TupleImp<Boostable, Boostable>(OFFENCE, DEFENCE);
		public Tuple<Boostable, Boostable> BLUNT = new Tuple.TupleImp<Boostable, Boostable>(BLUNT_ATTACK, BLUNT_DEFENCE);
			
		public final LIST<BDamage> DAMAGES;
		public final RCollection<BDamage> DAMAGE_COLL;
		
		Battle() throws IOException{
			super("BATTLE", D.g("Battle"));
			
			
			{
				ResFolder p = PATHS.STATS().folder("damage");
				LinkedList<BDamage> pairs = new LinkedList<>();
				
				int index = 0;
				LinkedList<BDamage> da = new LinkedList<>();
				
				for (String f : p.init.getFiles()) {
					BDamage d = new BDamage(index++, this, f, new Json(p.init.get(f)), new Json(p.text.get(f)));
					pairs.add(d);
					da.add(d);
				}
				
				
				KeyMap<BDamage> map = new KeyMap<BDamage>();
				for (BDamage pa : pairs)
					map.put(pa.key, pa);
				
				DAMAGE_COLL = new RCollection<BDamage>("DAMAGE", map) {
					
					@Override
					public BDamage getAt(int index) {
						return DAMAGES.get(index);
					}
					
					@Override
					public LIST<BDamage> all() {
						return DAMAGES;
					}
				};
				
				this.DAMAGES = new ArrayList<BDamage>(pairs);
				
			}
			
		}

	}
	
	public static final class BBoost implements INDEXED{
		
		private final int index;
		public Boostable b;
		
		private BBoost(Boostable b, int index){
			this.b = b;
			this.index = index;
		}

		@Override
		public int index() {
			return index;
		}
		
	}
	
	public static final class BDamage implements INDEXED{
		
		private final int index;
		public final String key;
		public Boostable attack;
		public Boostable defence;
		public final CharSequence name;
		
		
		private BDamage(int index, BoostableCat cat, String key, Json data, Json text) throws IOException{
			this.key = key;
			name = text.text("NAME");
			attack = make(cat, key, "ATTACK", data, text);
			defence = make(cat, key, "DEFENCE", data, text);
			this.index = index;
		}
		
		private Boostable make(BoostableCat cat, String key, String pp, Json data, Json text) throws IOException {
			key += "_" + pp;
			data = data.json(pp);
			
			double dd = data.d("DEFAULT_VALUE", 0, 100000);
			SPRITE icon = SPRITES.icons().get(data);
			INFO info = new INFO(text.json(pp));
			return BOOSTING.push(key, dd, info.name, info.desc, icon, cat);
		}

		@Override
		public int index() {
			return index;
		}
		
	}
	
	
	
	public static final class Behaviour extends BoostableCollection{
		
		Behaviour(){
			super("BEHAVIOUR", D.g("Behaviour"));
		}
		
		public final Boostable LAWFULNESS = make("LAWFULNESS", 1.0, UI.icons().s.law,
				D.g("BEHAVIOUR_LAWFULNESS", "Lawfulness"),
				D.g("BEHAVIOUR_LAWFULNESS_D", "A lawful citizen is one that is reluctant to commit crime."));

			public final Boostable SUBMISSION = make("SUBMISSION", 1.0, UI.icons().s.slave,
				D.g("BEHAVIOUR_SUBMISSION", "Submission"),
				D.g("BEHAVIOUR_SUBMISSION_D", "Submission is useful for slaves. Submissive slaves will be reluctant to revolt and you can mistreat them more."));

			public final Boostable LOYALTY = make("LOYALTY", 1.0, UI.icons().s.column,
				D.g("BEHAVIOUR_LOYALTY", "Loyalty"),
				D.g("BEHAVIOUR_LOYALTY_D", "Increases the Loyalty of your citizens, preventing riots and usurping."));
			
			public final Boostable HAPPI = make("HAPPINESS", 1.0, UI.icons().s.heart,
					D.g("BEHAVIOUR_HAPPINESS", "Happiness"),
					D.g("BEHAVIOUR_HAPPINESS_D", "Increases the happiness of your subjects. Happy subjects will be more loyal and submissive."));

			public final Boostable SANITY = make("SANITY", 1.0, UI.icons().s.crazy,
				D.g("BEHAVIOUR_SANITY", "Sanity"),
				D.g("BEHAVIOUR_SANITY_D", "Determines the chance a subject will become deranged during its lifetime."));
	}
	
	public static final class Start extends BoostableCollection{
		
		Start(){
			super("START", D.g("Start"));
		}
		
		public final Boostable LANDING = make("LANDING", 0.0, UI.icons().s.arrowUp,
				D.g("START_LANDING", "Settle"),
				D.g("START_LANDING_D", "Increases your starting subjects and resources."));

		public final Boostable KNOWLEDGE = make("KNOWLEDGE", 0.0, UI.icons().s.admin,
			D.g("START_KNOWLEDGE", "Knowledge"),
			D.g("START_KNOWLEDGE_D", "Starting knowledge"));
		

	}
	
	public static final class Civic extends BoostableCollection{
		
		Civic(){
			super("CIVIC", D.g("Civic"));
		}
		
		public final Boostable MAINTENANCE = make("MAINTENANCE", 1.0, UI.icons().s.degrade,
				D.g("CIVIC_MAINTENANCE", "Robustness"),
				D.g("CIVIC_MAINTENANCE_D", "Decreases the rate at which our buildings degrade."));

//			public final Boostable TRADE = make("TRADE", 1.0, UI.icons().s.money,
//				D.g("CIVIC_TRADE", "Bartering"),
//				D.g("CIVIC_TRADE_D", "Improves the transport fee for distant trades."));

			public final Boostable SPOILAGE = make("SPOILAGE", 1.0, UI.icons().s.fly,
				D.g("CIVIC_SPOILAGE", "Conservation"),
				D.g("CIVIC_SPOILAGE_D", "Decreases the spoilage rate of goods"));

			public final Boostable ACCIDENT = make("ACCIDENT", 1.0, UI.icons().s.boom,
				D.g("CIVIC_ACCIDENT", "Safety"),
				D.g("CIVIC_ACCIDENT_D", "Decreases the chance of work related accidents."));

			public final Boostable HYGINE = make("HYGINE", 1.0, UI.icons().s.heart,
				D.g("CIVIC_HYGINE", "Hygiene"),
				D.g("CIVIC_HYGINE_D", "Determines chance of diseases."));

			public final Boostable FURNITURE = make("FURNITURE", 1.0, UI.icons().s.bed,
				D.g("CIVIC_FURNITURE", "Furnishing"),
				D.g("CIVIC_FURNITURE_D", "Decreases the rate at which subjects use up the furniture of their homes."));

			public final Boostable RAIDING = make("RAIDING", 1.0, UI.icons().s.citizen,
				D.g("CIVIC_RAIDING", "Pacifism"),
				D.g("CIVIC_RAIDING_D", "Decreases the chances of us being raided."));
			
			public final Boostable DEFALTION = make("DEFLATION", 1, UI.icons().s.money,
					D.g("Deflation"), 
					D.g("deflationD", "Decreases the inflation of your treasury, allowing you to save up more."));
			
			public final Boostable IMMIGRATION = make("IMMIGRATION", 1, UI.icons().s.human,
					D.g("Immigration"),
					D.g("ImmigrationD", "Increases the speed at which people from the outside immigrates into our city")
					);
		
	}
	
	private static class BoostableCollection extends BoostableCat{


		BoostableCollection(String key, CharSequence name){
			super(key + "_", name, "", TYPE_SETT);
		}
		
		Boostable make(String key, double vv, Icon icon, CharSequence name, CharSequence desc) {
			Boostable b = BOOSTING.push(key, vv, name, desc, icon, this);
			return b;
		}

	}
	
}
