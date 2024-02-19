package com.github.argon.sos.moreoptions;

import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.SCRIPT;
import com.github.argon.sos.moreoptions.game.api.GameApis;
import com.github.argon.sos.moreoptions.game.api.GameEventsApi;
import com.github.argon.sos.moreoptions.game.ui.Modal;
import com.github.argon.sos.moreoptions.log.Level;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.BackupModal;
import com.github.argon.sos.moreoptions.ui.MoreOptionsModal;
import com.github.argon.sos.moreoptions.ui.UIGameConfig;
import init.paths.ModInfo;
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
public final class MoreOptionsScript implements SCRIPT<MoreOptionsConfig> {

	private final static Logger log = Loggers.getLogger(MoreOptionsScript.class);

	public final static INFO MOD_INFO = new INFO("More Options", "Adds more options to the game :)");

	@Getter
	private final static ConfigStore configStore = ConfigStore.getInstance();
	@Getter
	private final MoreOptionsConfigurator configurator = MoreOptionsConfigurator.getInstance();
	@Getter
	private final GameApis gameApis = GameApis.getInstance();

	@Getter
	private final Dictionary dictionary = Dictionary.getInstance();

	@Getter
	private UIGameConfig uiGameConfig;

	private Instance instance;
	private Modal<MoreOptionsModal> moreOptionsModal;
	private ModInfo modInfo;

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
		Level level = configStore.initMetaInfo().getLogLevel();
		Loggers.setLevels(level);

		log.debug("PHASE: initBeforeGameCreated");
		Errors.setHandler(new MoreOptionsErrorHandler<>(this));
		GameEventsApi.initLazy();

		// load backup config
		configStore.loadBackupConfig().ifPresent(configStore::setBackupConfig);

		modInfo = gameApis.modApi().getCurrentMod().orElse(null);
		uiGameConfig = new UIGameConfig(
			modInfo,
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

			// try to get current config and merge with defaults; or use whole defaults
			MoreOptionsConfig config = configStore.initConfig();

			// don't apply when there's a backup
			if (!configStore.getBackupConfig().isPresent()) {
				configurator.applyConfig(config);
			}
			moreOptionsModal = new Modal<>(MOD_INFO.name.toString(),
				new MoreOptionsModal(configStore, modInfo));
			uiGameConfig.initDebug(moreOptionsModal, configStore);

			// add description from game boosters
			gameApis.boosterApi().getAllBoosters()
				.values().forEach(dictionary::add);

			instance = new Instance(this);
		}

		// or else the init methods won't be called again when a save game is loaded
		instance.reset();
		return instance;
	}

	@Override
	public void initGameRunning() {
		log.debug("PHASE: initGameRunning");
	}

	@Override
	public void initGamePresent() {
		log.debug("PHASE: initGamePresent");

		// config should already be loaded or use default
		MoreOptionsConfig moreOptionsConfig = configStore.getCurrentConfig()
			.orElse(configStore.getDefaultConfig());

		uiGameConfig.init(moreOptionsModal, moreOptionsConfig);
		uiGameConfig.inject(moreOptionsModal);

		Optional<MoreOptionsConfig> backupConfig = configStore.getBackupConfig();
		// show backup dialog?
		if (backupConfig.isPresent()) {
			log.debug("Backup config present at %s", backupConfig.get().getFilePath());
			Modal<BackupModal> backupModal = new Modal<>(MOD_INFO.name.toString(), new BackupModal());
			Modal<MoreOptionsModal> backupMoreOptionsModal = new Modal<>(MOD_INFO.name.toString(),
				new MoreOptionsModal(configStore, modInfo));

			configStore.setCurrentConfig(backupConfig.get());
			uiGameConfig.initForBackup(backupModal, backupMoreOptionsModal, moreOptionsModal, backupConfig.get());

			backupModal.show();
		} else {
			moreOptionsModal.getSection().applyConfig(moreOptionsConfig);
		}

		// fixme Disease and Raid Event chance shall go from 100% (game default) to 10000%
		//       So players could lower the chance or increase it as they like

		// todo add a MoreOptionsViewModel inbetween? for easier mapping? mapstruct?

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

		// re-apply config when new game is loaded (only when there's no backup)
		if (!configStore.getBackupConfig().isPresent()) {
			configurator.applyConfig(config);
		}
	}

	@Override
	public void update(double seconds) {
//		log.trace("PHASE: update");
	}

	@Override
	public void crash(Throwable throwable) {
		log.warn("Game crash detected!");
		log.debug("", throwable);

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