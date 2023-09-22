package com.github.argon.sos.moreoptions;

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
	private MoreOptionsConfig moreOptionsConfig;

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

		moreOptionsConfig = MoreOptionsConfig.builder()
			.weather(MoreOptionsConfig.Weather.builder().build())
			.ambienceSounds(MoreOptionsConfig.AmbienceSounds.builder()
				.nature(0)
				.windTrees(20)
				.wind(50)
				.build())
			.events(MoreOptionsConfig.Events.builder()
				.killer(false)
				.build())
			.build();

		Configurator configurator = new Configurator();

		configurator.applyConfig(moreOptionsConfig);

		return new Instance(this);
	}

	@Override
	public void initGameRunning() {

	}

	@Override
	public void initGamePresent() {
		UIGameConfig uiGameConfig = new UIGameConfig(
			new MoreOptionsModal(moreOptionsConfig),
			GameUiApi.getInstance()
		);
		uiGameConfig.init();
	}

	@Override
	public void initGameSaveLoaded(Void config) {

	}
}