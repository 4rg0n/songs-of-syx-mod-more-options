package com.github.argon.sos.moreoptions;

import com.github.argon.sos.mod.sdk.log.Level;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.moreoptions.aspect.annotation.OnErrorShowDialog;
import com.github.argon.sos.moreoptions.config.ConfigApplier;
import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.domain.ConfigMeta;
import com.github.argon.sos.moreoptions.game.AbstractScript;
import com.github.argon.sos.moreoptions.game.json.GameJsonStore;
import com.github.argon.sos.moreoptions.game.ui.Window;
import com.github.argon.sos.mod.sdk.i18n.I18nMessages;
import com.github.argon.sos.moreoptions.metric.MetricExporter;
import com.github.argon.sos.moreoptions.metric.MetricScheduler;
import com.github.argon.sos.mod.sdk.phase.Phase;
import com.github.argon.sos.mod.sdk.phase.PhaseManager;
import com.github.argon.sos.moreoptions.ui.BackupDialog;
import com.github.argon.sos.moreoptions.ui.UiConfig;
import com.github.argon.sos.moreoptions.ui.msg.Notificator;
import init.paths.PATHS;
import lombok.NoArgsConstructor;
import util.info.INFO;

import java.nio.file.Path;


/**
 * Entry point for the mod.
 */
@NoArgsConstructor
@SuppressWarnings("unused") // used by the game via reflection
public final class MoreOptionsScript extends AbstractScript {

	private final static Logger log = Loggers.getLogger(MoreOptionsScript.class);

	public final static INFO MOD_INFO = new INFO("More Options", "Adds more options to the game :)");
	private final ConfigStore configStore = ConfigStore.getInstance();
	private final ConfigApplier configApplier = ConfigApplier.getInstance();
	private final UiConfig uiConfig = UiConfig.getInstance();
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
            .register(Phase.INIT_BEFORE_GAME_CREATED, GameJsonStore.getInstance())
            .register(Phase.INIT_BEFORE_GAME_CREATED, MetricExporter.getInstance())
            .register(Phase.INIT_BEFORE_GAME_CREATED, I18nMessages.getInstance())
            .register(Phase.INIT_BEFORE_GAME_CREATED, ConfigStore.getInstance())
            .register(Phase.INIT_MOD_CREATE_INSTANCE, UiConfig.getInstance())
            .register(Phase.INIT_SETTLEMENT_UI_PRESENT, ConfigStore.getInstance())
            .register(Phase.INIT_SETTLEMENT_UI_PRESENT, UiConfig.getInstance())

            .register(Phase.ON_GAME_SAVE_LOADED, ConfigStore.getInstance())
            .register(Phase.ON_GAME_SAVED, ConfigStore.getInstance())
            .register(Phase.ON_GAME_SAVE_RELOADED, MetricExporter.getInstance())
			.register(Phase.ON_GAME_UPDATE, Notificator.getInstance())
            .register(Phase.ON_CRASH, MetricScheduler.getInstance())
            .register(Phase.ON_CRASH, ConfigStore.getInstance());
	}

	@Override
	public void initBeforeGameCreated() {
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
	@OnErrorShowDialog
	public void onViewSetup() {
		super.onViewSetup();

        Window<BackupDialog> backupDialog = uiConfig.getBackupDialog();
        // show backup dialog?
        if (backupDialog != null) {
            backupDialog.show();
        } else {
            // apply loaded config
            configApplier.applyToGame(configStore.getCurrentConfig());
        }
    }

	@Override
	public boolean forceInit() {
		return true;
	}
}

