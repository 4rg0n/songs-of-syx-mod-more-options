package com.github.argon.sos.moreoptions;

import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.SCRIPT;
import com.github.argon.sos.moreoptions.game.api.GameApis;
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
	private Modal<MoreOptionsModal> moreOptionsModal;
	private ModInfo modInfo;

	@Override
	public CharSequence name() {
		return MOD_INFO.name;
	}

	@Override
	public CharSequence desc() {
		return MOD_INFO.desc;
	}

	static {
		Loggers.setLevels(Level.DEBUG);
	}

	@Override
	public void initBeforeGameCreated() {
		log.debug("PHASE: initBeforeGameCreated");

		Errors.setHandler(new MoreOptionsErrorHandler<>(this));

		// load config from file
		configStore.loadConfig()
			.ifPresent(currentConfig -> {
				Loggers.setLevels(currentConfig.getLogLevel());
				configStore.setCurrentConfig(currentConfig);
			});

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

			// try get current config and merge with defaults; or use whole defaults
			MoreOptionsConfig defaultConfig = configStore.getDefaults().get();
			MoreOptionsConfig config = configStore.getCurrentConfig()
				.map(currentConfig -> configStore.mergeMissing(currentConfig, defaultConfig))
				.orElseGet(() -> {
					log.info("No config file loaded. Using default.");
					log.trace("Default: %s", defaultConfig);
					return defaultConfig;
				});
			configStore.setCurrentConfig(config);

			// don't apply when there's a backup
			if (!configStore.getBackupConfig().isPresent()) {
				configurator.applyConfig(config);
			}
			moreOptionsModal = new Modal<>(MOD_INFO.name.toString(),
				new MoreOptionsModal(configStore::getCurrentConfig, modInfo));
			uiGameConfig.initDebug(moreOptionsModal);

			// add description from game boostables
			gameApis.boosterApi().getAllBoosters()
				.values().forEach(dictionary::add);

			instance = new Instance(this);
//			throw new RuntimeException("BOOM!");
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

		Optional<MoreOptionsConfig> backupConfig = configStore.getBackupConfig();
		// config should already be loaded or use default
		MoreOptionsConfig moreOptionsConfig = configStore.getCurrentConfig()
			.orElse(configStore.getDefaults().get());

		uiGameConfig.init(moreOptionsModal, moreOptionsConfig);
		uiGameConfig.inject(moreOptionsModal);

		if (backupConfig.isPresent()) {
			// show backup dialog
			Modal<BackupModal> backupModal = new Modal<>(MOD_INFO.name.toString(), new BackupModal(), true);
			Modal<MoreOptionsModal> moreOptionsModal = new Modal<>(MOD_INFO.name.toString(),
				new MoreOptionsModal(configStore::getCurrentConfig, modInfo), true);

			uiGameConfig.initForBackup(backupModal, moreOptionsModal, backupConfig.get());
			moreOptionsModal.getSection().applyConfig(backupConfig.get());
			configStore.setCurrentConfig(backupConfig.get());
			backupModal.show();
		} else {
			moreOptionsModal.getSection().applyConfig(moreOptionsConfig);
		}

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
		log.trace("PHASE: update");
	}

	@Override
	public void crash(Throwable throwable) {
		log.warn("Game crash detected!");
		log.debug("", throwable);

		try {
			// backup and delete config file
			configStore.getCurrentConfig().ifPresent(config -> {
				if (configStore.createBackupConfig(config)) {
					log.warn("Backup config file created at: %s",
						ConfigStore.backupConfigPath());
				}

				if (configStore.deleteConfig()) {
					log.warn("Deleted possible faulty config file at: %s",
						ConfigStore.configPath());
				}
			});
		} catch (Exception e) {
			log.error("Something bad happened while trying to handle game crash", e);
		}
	}
}