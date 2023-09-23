package com.github.argon.sos.moreoptions;

import com.github.argon.sos.moreoptions.config.ConfigStore;
import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.game.SCRIPT;
import com.github.argon.sos.moreoptions.game.api.GameUiApi;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.MoreOptionsModal;
import com.github.argon.sos.moreoptions.ui.UIGameConfig;
import lombok.NoArgsConstructor;
import util.info.INFO;

import java.util.logging.Level;

/**
 * Entry point for the mod.
 * See {@link SCRIPT} for some documentation.
 */
@NoArgsConstructor
@SuppressWarnings("unused") // used by the game via reflection
public final class MoreOptionsScript implements SCRIPT<Void> {

	public final static INFO MOD_INFO = new INFO("More Options", "TODO");

	private ConfigStore configStore = ConfigStore.getInstance();
	private Configurator configurator = Configurator.getInstance();

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
	}



	@Override
	public SCRIPT_INSTANCE createInstance() {
		return new Instance(this);
	}

	@Override
	public void initGameRunning() {

	}

	@Override
	public void initGamePresent() {
		MoreOptionsConfig moreOptionsConfig = configStore.getProfileConfig()
			.orElse(MoreOptionsConfig.builder().build());

		configStore.setCurrentConfig(moreOptionsConfig);
		configurator.applyConfig(moreOptionsConfig);

		UIGameConfig uiGameConfig = new UIGameConfig(
			new MoreOptionsModal(configStore),
			GameUiApi.getInstance(),
			configStore,
			configurator
		);

		uiGameConfig.init();
	}

	@Override
	public void initGameSaveLoaded(Void config) {

	}
}