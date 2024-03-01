package com.github.argon.sos.moreoptions;

import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.SCRIPT;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.game.ui.Modal;
import com.github.argon.sos.moreoptions.init.InitPhases;
import com.github.argon.sos.moreoptions.init.Initializer;
import com.github.argon.sos.moreoptions.log.Level;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.metric.MetricExporter;
import com.github.argon.sos.moreoptions.ui.BackupDialog;
import com.github.argon.sos.moreoptions.ui.MoreOptionsView;
import com.github.argon.sos.moreoptions.ui.UiGameConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import snake2d.Errors;
import util.info.INFO;

import java.util.Optional;


/**
 * Entry point for the mod.
 * See {@link SCRIPT} for some documentation.
 */
@NoArgsConstructor
@SuppressWarnings("unused") // used by the game via reflection
public final class MoreOptionsScript implements SCRIPT<MoreOptionsConfig>, InitPhases {

	private final static Logger log = Loggers.getLogger(MoreOptionsScript.class);

	public final static INFO MOD_INFO = new INFO("More Options", "Adds more options to the game :)");

	@Getter
	private final static ConfigStore configStore = ConfigStore.getInstance();
	@Getter
	private final MoreOptionsConfigurator configurator = MoreOptionsConfigurator.getInstance();
	@Getter
	private final GameApis gameApis = GameApis.getInstance();
	private final Initializer initializer = Initializer.getInstance();

	@Getter
	private final Dictionary dictionary = Dictionary.getInstance();

	@Getter
	private final UiGameConfig uiGameConfig = UiGameConfig.getInstance();

	private Instance instance;

	public final static Level LOG_LEVEL_DEFAULT = Level.TRACE;

	@Override
	public CharSequence name() {
		return MOD_INFO.name;
	}

	@Override
	public CharSequence desc() {
		return MOD_INFO.desc;
	}

	static {
		Loggers.setLevels(LOG_LEVEL_DEFAULT);
	}

	@Override
	public void initBeforeGameCreated() {
		initializer.initBeforeGameCreated();

		// determine log level
		String logLevelName = System.getenv("MO.LOG_LEVEL");
		Level level = Optional.ofNullable(logLevelName)
			.flatMap(Level::fromName)
			.orElseGet(() -> configStore.getMetaInfo().map(
				MoreOptionsConfig.Meta::getLogLevel)
				.orElse(LOG_LEVEL_DEFAULT));
		Loggers.setLevels(level);

		log.debug("PHASE: initBeforeGameCreated");
		// custom error handling
		Errors.setHandler(new MoreOptionsErrorHandler<>(this));
	}

	@Override
	public void initCreateInstance() {
		initializer.initCreateInstance();
	}


	/**
	 * BUG!: Method will be executed TWICE by the game
	 * (will be fixed in v65 =))
	 */
	@Override
	public SCRIPT_INSTANCE createInstance() {
		log.debug("PHASE: createInstance");
		if (instance == null) {
			initCreateInstance();
			log.debug("Creating Mod Instance");

			// try to get current config and merge with defaults; or use whole defaults
			MoreOptionsConfig config = configStore.getCurrentConfig();

			// add description from game boosters
			gameApis.booster().getBoosters()
				.values().forEach(moreOptionsBoosters -> dictionary.add(moreOptionsBoosters.getAdd()));

			instance = new Instance(this);
		}

		// or else the init methods won't be called again when a save game is loaded
		instance.reset();
		return instance;
	}

	@Override
	public void initGameRunning() {
		log.debug("PHASE: initGameRunning");
		initializer.initGameRunning();
	}

	@Override
	public void initGamePresent() {
		log.debug("PHASE: initGamePresent");
		initializer.initGamePresent();
		// config should already be loaded or use default
		MoreOptionsConfig moreOptionsConfig = configStore.getCurrentConfig();

		Modal<MoreOptionsView> moreOptionsModal = uiGameConfig.buildModal(MOD_INFO.name.toString(), moreOptionsConfig);
		uiGameConfig.initDebugActions(moreOptionsModal, configStore);
		uiGameConfig.inject(moreOptionsModal);

		Optional<MoreOptionsConfig> backupConfig = configStore.getBackupConfig();
		// show backup dialog?
		if (backupConfig.isPresent()) {
			log.debug("Backup config present at %s", backupConfig.get().getFilePath());
			Modal<BackupDialog> backupModal = new Modal<>(MOD_INFO.name.toString(), new BackupDialog());
			Modal<MoreOptionsView> backupMoreOptionsModal = uiGameConfig.buildModal(
				MOD_INFO.name + " Backup",
				backupConfig.get());

			configStore.setCurrentConfig(backupConfig.get());
			uiGameConfig.initBackupActions(backupModal, backupMoreOptionsModal, moreOptionsModal, backupConfig.get());

			backupModal.show();
		} else {
			configurator.applyConfig(moreOptionsConfig);
		}

		// FIXME
		//      * NOTHING

		// TODO
		//     * further testing

		// TODO FUTURE
		//  	is there a better way to streamline the process of adding new ui elements with their data and config?
		//      * better config mapping? Race Interaction JsonE and ObjectMapper?
		//      * easier mapping? mapstruct?

		// TODO add a MoreOptionsViewModel inbetween?
		// TODO experimental
//		gameApis.weatherApi().lockDayCycle(1, true);
	}

	@Override
	public void initGameSaveLoaded(MoreOptionsConfig config) {
		initializer.initGameSaveLoaded(config);
		// game will initialize new instances of the cached class references on load
		gameApis.events().clearCached();
		gameApis.sounds().clearCached();
		gameApis.weather().clearCached();
		gameApis.booster().clearCached();

		// re-apply config when new game is loaded (only when there's no backup)
		if (!configStore.getBackupConfig().isPresent()) {
			log.debug("Reapplying config because of game load.");
			configurator.applyConfig(config);
		}

		// start a new export file on load
		MetricExporter.getInstance().newExportFile();
	}

	@Override
	public void update(double seconds) {
//		log.trace("PHASE: update %s", seconds);
	}

	@Override
	public void crash(Throwable throwable) {
		log.warn("Game crash detected!");
		log.debug("", throwable);
		initializer.crash(throwable);

		try {
			// backup and delete config file
			if (configStore.createBackupConfig()) {
				log.warn("Backup config file successfully created at: %s",
					ConfigStore.backupConfigPath());

				// only delete original when backup config was created successfully
				if (configStore.deleteConfig()) {
					log.warn("Deleted possible faulty config file at: %s",
						ConfigStore.configPath());
				}
			}
		} catch (Exception e) {
			log.error("Something bad happened while trying to handle game crash", e);
		}
	}
}