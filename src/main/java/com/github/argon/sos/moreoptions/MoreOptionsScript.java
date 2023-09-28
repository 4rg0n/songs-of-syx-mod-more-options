package com.github.argon.sos.moreoptions;

import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.SCRIPT;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.MoreOptionsModal;
import com.github.argon.sos.moreoptions.ui.UIGameConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import util.info.INFO;

import java.util.logging.Level;

/**
 * Entry point for the mod.
 * See {@link SCRIPT} for some documentation.
 */
@NoArgsConstructor
@SuppressWarnings("unused") // used by the game via reflection
public final class MoreOptionsScript implements SCRIPT<MoreOptionsConfig> {

	private final static Logger log = Loggers.getLogger(MoreOptionsScript.class);

	public final static INFO MOD_INFO = new INFO("More Options", "TODO");

	@Getter
	private final ConfigStore configStore = ConfigStore.getInstance();
	@Getter
	private final MoreOptionsConfigurator configurator = MoreOptionsConfigurator.getInstance();
	@Getter
	private final GameApis gameApis = GameApis.getInstance();
	@Getter
	private UIGameConfig uiGameConfig;

	private Instance instance;

	@Override
	public CharSequence name() {
		return MOD_INFO.name;
	}

	@Override
	public CharSequence desc() {
		return MOD_INFO.desc;
	}

	@Override
	public void initBeforeGameCreated() {
		Loggers.setLevels(Level.FINEST);
		log.debug("PHASE: initBeforeGameCreated");

		uiGameConfig = new UIGameConfig(
			new MoreOptionsModal(configStore),
			gameApis,
			configurator,
			configStore
		);

		// add title and description for some options (I don't know all of them :x)
		Dictionary.getInstance()
			// Settlement Events
			.add("event.settlement.disease", "Animal diseases", "A disease can spread among your pastures and decimate your population.")
			.add("event.settlement.slaver", "Slaver Visits", "A slaver will visit your settlement regularly for buying and selling slaves")
			.add("event.settlement.riot", "Riots", "Riots can break out among your citizen.")
			.add("event.settlement.uprising", "Slave Revolts", "Slaves can revolt against you.")
			.add("event.settlement.killer", "Serial Killer", "A serial killer can kill your citizens until captured.")
			.add("event.settlement.temperature", "Temperature", "The temperature can fluctuate drastically.")
			.add("event.settlement.farm", "Farm Production", "Farms can produce less or more randomly.")
			.add("event.settlement.pasture", "Pasture Production", "Pastures can produce less or more randomly.")
			.add("event.settlement.orchard", "Orchard Production", "Orchards can produce less or more randomly.")
			.add("event.settlement.fish", "Fishery Production", "Fisheries can produce less or more randomly.")
			.add("event.settlement.raceWars", "Race Wars", "A fight between different races in your settlement can break out.")
			.add("event.settlement.advice", "Advices", "If you are low on workers, someone gets sick, loyalty is low or your first crime happens.")
			.add("event.settlement.accident", "Accidents", "Workers can harm themselves at workplaces.")
			// World Events
			.add("event.world.factionExpand", "Realm Expand", "Realms will expand to other territories.")
			.add("event.world.factionBreak", "Realm Collapse", "Realms can collapse under certain conditions.")
			.add("event.world.popup", "Popup", "???")
			.add("event.world.war", "War Realms", "Realms will fight with each other")
			.add("event.world.warPlayer", "War Player", "Realms will declare war against a player.")
			.add("event.world.warPeace", "War Peace", "Realms can end war under certain conditions.")
			.add("event.world.raider", "Raider Attacks", "Raiders will attack your settlement.")
			.add("event.world.rebellion", "Rebellion", "Citizens of other realms can rebel when they are unhappy.")
			.add("event.world.plague", "Plague", "A plague can spread among realms.")
			// Weather
			.add("weather.thunder", "Thunder", "Flashes on the screen.")
			.add("weather.ice", "Ice", "Ice floating on water sources.")
			.add("weather.rain", "Rain", "Rain particles moving through the screen.")
			.add("weather.clouds", "Clouds", "Cloud shadows on the ground.")
			.add("weather.snow", "Snow", "Snow laying on ground")
			.add("weather.moisture", "Moisture", "Moisture laying on ground")
			.add("weather.wind", "Wind", "Wind waving through flora.")
			.add("weather.growth", "Growth", null)
			.add("weather.growthRipe", "Growth Ripe", null)
			// Ambience Sounds
			.add("sounds.ambience.nature", "Nature", null)
			.add("sounds.ambience.night", "Night", null)
			.add("sounds.ambience.rain", "Rain", null)
			.add("sounds.ambience.wind", "Wind", null)
			.add("sounds.ambience.thunder", "Thunder", null)
			.add("sounds.ambience.water", "Water", null)
			.add("sounds.ambience.windhowl", "Wind Howl", null)
			.add("sounds.ambience.windTrees", "Wind Trees", null)
			// Settlement Sounds
			.add("sounds.settlement._AXE", "Axe", null)
			.add("sounds.settlement._DIG", "Dig", null)
			.add("sounds.settlement._STONE", "Stone", null)
			.add("sounds.settlement._PAIN", "Pain", null)
			.add("sounds.settlement._SLEEP", "Sleep", null)
			.add("sounds.settlement._DECAY", "Decay", null)
			.add("sounds.settlement._BUILD", "Build", null)
			.add("sounds.settlement._SWORD", "Sword", null)
			.add("sounds.settlement._SQUISH", "Squish", null)
			// Room Sounds
			.add("sounds.room.SQUISH", "Squish", null)
			.add("sounds.room.DIG", "Dig", null)
			.add("sounds.room.STONE", "Stone", null)
			.add("sounds.room.TEMPLE_AMINION", "Temple Aminion", null)
			.add("sounds.room.CATAPULT_HIT", "Catapult Hit", null)
			.add("sounds.room.CATAPULT_RELEASE", "Catapult Release", null)
			.add("sounds.room.CATAPULT_WORK", "Catapult Work", null)
			.add("sounds.room.ARROW_HIT", "Arrow Hit", null)
			.add("sounds.room.ARROW_RELEASE", "Arrow Release", null)
			.add("sounds.room.MACHINE", "Machine", null)
			.add("sounds.room.FART", "Fart", null)
			.add("sounds.room.LIBRARY", "Library", null)
			.add("sounds.room.CRANK", "Crank", null)
			.add("sounds.room.METAL", "Metal", null)
			.add("sounds.room.TEXTILE", "Textile", null)
			.add("sounds.room.AXE", "Axe", null)
			.add("sounds.room.FABRIC", "Fabric", null)
			.add("sounds.room.BUILD", "Build", null)
		;
	}

	/**
	 * BUG!: Method will be executed TWICE by the game
	 * (will be fixed in v65 =))
	 */
	@Override
	public SCRIPT_INSTANCE createInstance() {
		log.debug("PHASE: createInstance");
		if (instance == null) {
			log.debug("Creating Mod Instance");

			// try to load from file and merge with defaults; or use whole defaults
			MoreOptionsConfig defaultConfig = configStore.getDefault();
			MoreOptionsConfig config = configStore.loadConfig(defaultConfig)
				.orElseGet(() -> {
					log.info("No config file loaded. Using default.");
					log.trace("Default: %s", defaultConfig);
					return defaultConfig;
				});
			configStore.setCurrentConfig(config);

			// we want to apply the config as soon as possible
			configurator.applyConfig(config);
			uiGameConfig.initDebug();

			instance = new Instance(this);
		}

		// or else the init methods won't be called again when a save game is loaded
		instance.reset();
		return instance;
	}

	@Override
	public void initGameRunning() {

	}

	@Override
	public void initGamePresent() {
		MoreOptionsConfig moreOptionsConfig = configStore.getCurrentConfig()
			.orElse(configStore.getDefault());

		// build and init UI (only possible if the UI is present)
		uiGameConfig.initUi(moreOptionsConfig);
	}

	@Override
	public void initGameSaveLoaded(MoreOptionsConfig config) {

	}
}

//!(e instanceof EventAdvisor) && !(e instanceof Tutorial) && !(e instanceof EventWorldExpand) && !(e instanceof EventWorldBreakoff) && !(e instanceof EventWorldWar) && !(e instanceof EventWorldPeace) && !(e instanceof EventSlaver)}