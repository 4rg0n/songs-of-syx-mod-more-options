package com.github.argon.sos.moreoptions;

import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.SCRIPT;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.log.Level;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.MoreOptionsModal;
import com.github.argon.sos.moreoptions.ui.UIGameConfig;
import init.paths.ModInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import util.info.INFO;



/**
 * Entry point for the mod.
 * See {@link SCRIPT} for some documentation.
 */
@NoArgsConstructor
@SuppressWarnings("unused") // used by the game via reflection
public final class MoreOptionsScript implements SCRIPT<MoreOptionsConfig> {

	private final static Logger log = Loggers.getLogger(MoreOptionsScript.class);

	public final static INFO MOD_INFO = new INFO("More Options", "Adds more options to the game :)");

	@Getter
	private final ConfigStore configStore = ConfigStore.getInstance();
	@Getter
	private final MoreOptionsConfigurator configurator = MoreOptionsConfigurator.getInstance();
	@Getter
	private final GameApis gameApis = GameApis.getInstance();

	@Getter
	private final Dictionary dictionary = Dictionary.getInstance();

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

	static {
		Loggers.setLevels(Level.INFO);
	}

	@Override
	public void initBeforeGameCreated() {
		log.debug("PHASE: initBeforeGameCreated");

		// load config from file
		configStore.loadConfig()
			.ifPresent(currentConfig -> {
				Loggers.setLevels(currentConfig.getLogLevel());
				configStore.setCurrentConfig(currentConfig);
			});

		ModInfo modInfo = gameApis.modApi().getCurrentMod().orElse(null);
		uiGameConfig = new UIGameConfig(
			new MoreOptionsModal(configStore, modInfo),
			gameApis,
			configurator,
			configStore
		);

		// load dictionary entries from file
		configStore.loadDictionary()
			.ifPresent(dictionary::addAll);
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

			// try get current config and merge with defaults; or use whole defaults
			MoreOptionsConfig defaultConfig = configStore.getDefault();
			MoreOptionsConfig config = configStore.getCurrentConfig()
				.map(currentConfig -> configStore.mergeMissing(currentConfig, defaultConfig))
				.orElseGet(() -> {
					log.info("No config file loaded. Using default.");
					log.trace("Default: %s", defaultConfig);
					return defaultConfig;
				});
			Loggers.setLevels(config.getLogLevel());
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
		// config should already be loaded or use default
		MoreOptionsConfig moreOptionsConfig = configStore.getCurrentConfig()
			.orElse(configStore.getDefault());

		// build and init UI (only possible if the UI is present)
		uiGameConfig.initUi(moreOptionsConfig);

		// todo experimental
//		gameApis.weatherApi().lockDayCycle(1, true);
	}

	@Override
	public void initGameSaveLoaded(MoreOptionsConfig config) {
		// game will initialize new instances of the cached class references on load
		gameApis.eventsApi().clearCached();
		gameApis.soundsApi().clearCached();
		gameApis.weatherApi().clearCached();
		gameApis.boosterApi().clearCached();

		// re-apply config when new game is loaded
		configurator.applyConfig(config);
	}

	@Override
	public void update(double seconds) {

	}
}