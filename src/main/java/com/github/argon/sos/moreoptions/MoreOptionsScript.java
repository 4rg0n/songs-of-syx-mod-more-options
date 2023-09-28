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