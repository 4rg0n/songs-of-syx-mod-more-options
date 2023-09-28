package com.github.argon.sos.moreoptions;

import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import lombok.RequiredArgsConstructor;
import script.SCRIPT;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;
import view.main.VIEW;

import java.io.IOException;

/**
 * Represents one instance of the "script".
 * See {@link SCRIPT.SCRIPT_INSTANCE} for some documentation
 */
@RequiredArgsConstructor
final class Instance implements SCRIPT.SCRIPT_INSTANCE {

	private final static Logger log = Loggers.getLogger(Instance.class);

	private boolean initGameRunning = false;

	private boolean initGamePresent = false;

	private final MoreOptionsScript script;

	@Override
	public void update(double v) {
		if (!initGameRunning) {
			log.debug("PHASE: initGameRunning");
			script.initGameRunning();
			initGameRunning = true;
		}

		if (!initGamePresent && !VIEW.inters().load.isActivated()) {
			log.debug("PHASE: initGamePresent");
			script.initGamePresent();
			initGamePresent = true;
		}
	}

	@Override
	public void save(FilePutter filePutter) {
		log.debug("PHASE: save");
	}

	@Override
	public void load(FileGetter fileGetter) throws IOException {
		log.debug("PHASE: load");
		// Pass current config or use default
		MoreOptionsConfig config = script.getConfigStore().getCurrentConfig()
			.orElse(script.getConfigStore().getDefault());

		script.initGameSaveLoaded(config);
	}

	/**
	 * Reset initialisation states
	 */
	public void reset() {
		this.initGamePresent = false;
		this.initGameRunning = false;
	}
}