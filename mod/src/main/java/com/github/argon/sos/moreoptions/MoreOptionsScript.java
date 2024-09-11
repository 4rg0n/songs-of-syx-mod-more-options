package com.github.argon.sos.moreoptions;

import com.github.argon.sos.mod.sdk.AbstractScript;
import com.github.argon.sos.mod.sdk.ModSdkModule;
import com.github.argon.sos.mod.sdk.game.ui.Window;
import com.github.argon.sos.mod.sdk.log.Level;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.phase.Phase;
import com.github.argon.sos.mod.sdk.phase.PhaseManager;
import com.github.argon.sos.moreoptions.config.ConfigApplier;
import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.ModProperties;
import com.github.argon.sos.moreoptions.config.domain.ConfigMeta;
import com.github.argon.sos.moreoptions.ui.BackupDialog;
import com.github.argon.sos.moreoptions.ui.UiConfig;
import init.paths.PATHS;
import lombok.NoArgsConstructor;
import util.info.INFO;

import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Entry point for the mod.
 */
@NoArgsConstructor
@SuppressWarnings("unused") // used by the game via reflection
public final class MoreOptionsScript extends AbstractScript {

	private final static Logger log = Loggers.getLogger(MoreOptionsScript.class);

	public final static INFO MOD_INFO = new INFO("More Options", "Adds more options to the game :)");
	private final ConfigStore configStore = ModModule.configStore();
	private final ConfigApplier configApplier = ModModule.configApplier();
	private final UiConfig uiConfig = ModModule.uiConfig();
	public final static Path MORE_OPTIONS_PROFILE = PATHS.local().PROFILE.get().resolve(MoreOptionsScript.MOD_INFO.name.toString());

	public final static Path MORE_OPTIONS_CONFIG = PATHS.local().SETTINGS.get().resolve("MoreOptions.txt");

	@Override
	public CharSequence name() {
		return MOD_INFO.name;
	}

	@Override
	public CharSequence desc() {
		return MOD_INFO.desc;
	}

	@Override
	protected void registerPhases(PhaseManager phaseManager) {
        phaseManager
            .register(Phase.INIT_BEFORE_GAME_CREATED, ModModule.configStore())
			.register(Phase.INIT_MOD_CREATE_INSTANCE, ModModule.gameApis().boosters())
            .register(Phase.INIT_MOD_CREATE_INSTANCE, ModModule.uiConfig())
            .register(Phase.INIT_SETTLEMENT_UI_PRESENT, ModModule.configStore())
            .register(Phase.INIT_SETTLEMENT_UI_PRESENT, ModModule.uiConfig())

			.register(Phase.INIT_BEFORE_GAME_CREATED, ModSdkModule.metricExporter())
			.register(Phase.ON_GAME_SAVE_RELOADED, ModSdkModule.metricExporter())
			.register(Phase.ON_CRASH, ModSdkModule.metricScheduler())

            .register(Phase.ON_GAME_SAVE_LOADED, ModModule.configStore())
            .register(Phase.ON_GAME_SAVED, ModModule.configStore())
            .register(Phase.ON_GAME_SAVE_RELOADED, ModModule.gameApis().boosters())
            .register(Phase.ON_CRASH, ModModule.configStore());
	}

	@Override
	public void initBeforeGameCreated() {
		propertiesStore.bind(ModProperties.class, Paths.get("mo-mod.properties"), true);

		super.initBeforeGameCreated();
		// force the use of the environment log level when present
		configApplier.setEnvLogLevel(getEnvLogLevel());
	}

	@Override
	public Level initLogLevel() {
		return configStore.getConfigMeta()
			.map(ConfigMeta::getLogLevel)
			.orElse(Loggers.LOG_LEVEL_DEFAULT);
	}

	@Override
	public void onViewSetup() {
		try {
			super.onViewSetup();

			Window<BackupDialog> backupDialog = uiConfig.getBackupDialog();
			// show backup dialog?
			if (backupDialog != null) {
				backupDialog.show();
			} else {
				// apply loaded config
				configApplier.applyToGame(configStore.getCurrentConfig());
			}
		} catch (Exception e) {
			ModModule.messages().errorDialog(e);
		}
    }

	@Override
	public boolean forceInit() {
		return true;
	}
}

