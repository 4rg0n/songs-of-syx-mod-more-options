package com.github.argon.sos.moreoptions;

import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.MoreOptionsV2Config;
import com.github.argon.sos.moreoptions.game.AbstractScript;
import com.github.argon.sos.moreoptions.game.ui.Modal;
import com.github.argon.sos.moreoptions.i18n.I18nMessages;
import com.github.argon.sos.moreoptions.phase.PhaseManager;
import com.github.argon.sos.moreoptions.phase.Phase;
import com.github.argon.sos.moreoptions.log.Level;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.metric.MetricExporter;
import com.github.argon.sos.moreoptions.metric.MetricScheduler;
import com.github.argon.sos.moreoptions.ui.BackupDialog;
import com.github.argon.sos.moreoptions.ui.UiConfig;
import lombok.NoArgsConstructor;
import util.info.INFO;


/**
 * Entry point for the mod.
 */
@NoArgsConstructor
@SuppressWarnings("unused") // used by the game via reflection
public final class MoreOptionsScript extends AbstractScript {
	private final static Logger log = Loggers.getLogger(MoreOptionsScript.class);
	public final static INFO MOD_INFO = new INFO("More Options", "Adds more options to the game :)");
	private final ConfigStore configStore = ConfigStore.getInstance();
	private final MoreOptionsConfigurator configurator = MoreOptionsConfigurator.getInstance();
	private final UiConfig uiConfig = UiConfig.getInstance();

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
		phaseManager.register(Phase.INIT_BEFORE_GAME_CREATED, ConfigStore.getInstance());
		phaseManager.register(Phase.INIT_BEFORE_GAME_CREATED, MetricExporter.getInstance());
		phaseManager.register(Phase.INIT_BEFORE_GAME_CREATED, I18nMessages.getInstance());
		phaseManager.register(Phase.INIT_MOD_CREATE_INSTANCE, UiConfig.getInstance());
		phaseManager.register(Phase.INIT_MOD_CREATE_INSTANCE, ConfigStore.getInstance());
		phaseManager.register(Phase.INIT_GAME_UI_PRESENT, UiConfig.getInstance());

		phaseManager.register(Phase.ON_GAME_SAVE_LOADED, ConfigStore.getInstance());
		phaseManager.register(Phase.ON_GAME_SAVED, ConfigStore.getInstance());
		phaseManager.register(Phase.ON_GAME_SAVE_RELOADED, MetricExporter.getInstance());
		phaseManager.register(Phase.ON_GAME_SAVE_RELOADED, MoreOptionsConfigurator.getInstance());
		phaseManager.register(Phase.ON_CRASH, MetricScheduler.getInstance());
		phaseManager.register(Phase.ON_CRASH, ConfigStore.getInstance());
	}

	@Override
	public Level initLogLevel() {
		return configStore.getMetaInfo()
			.map(MoreOptionsV2Config.Meta::getLogLevel)
			.orElse(Loggers.LOG_LEVEL_DEFAULT);
	}

	@Override
	public void initGameUiPresent() {
		// initialize ui stuff
		super.initGameUiPresent();
		Modal<BackupDialog> backupDialog = uiConfig.getBackupDialog();
		// show backup dialog?
		if (backupDialog != null) {
			backupDialog.show();
		} else {
			// apply loaded config
			configurator.applyConfig(configStore.getCurrentConfig());
		}

		// TODO experimental
//		gameApis.weatherApi().lockDayCycle(1, true);
	}
}