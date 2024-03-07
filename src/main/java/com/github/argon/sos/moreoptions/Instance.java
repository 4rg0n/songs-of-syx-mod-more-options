package com.github.argon.sos.moreoptions;

import com.github.argon.sos.moreoptions.phase.Phases;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import lombok.RequiredArgsConstructor;
import script.SCRIPT;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;
import view.main.VIEW;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Represents one instance of the "script".
 * See {@link SCRIPT.SCRIPT_INSTANCE} for some documentation
 */
@RequiredArgsConstructor
final class Instance implements SCRIPT.SCRIPT_INSTANCE {

	private final static Logger log = Loggers.getLogger(Instance.class);

	private boolean initGameRunning = false;

	private boolean initGamePresent = false;

	private boolean newGameSession = true;

	private final Phases phases;

	@Override
	public void update(double v) {
		if (!initGameRunning) {
			initGameRunning = true;
			phases.initGameUpdating();
		}

		if (!initGamePresent && !VIEW.inters().load.isActivated()) {
			initGamePresent = true;
			phases.initGameUiPresent();
		}

		phases.onGameUpdate(v);
	}

	@Override
	public void save(FilePutter filePutter) {
		phases.onGameSaved(filePutter.getPath());
	}

	@Override
	public void load(FileGetter fileGetter) throws IOException {
		phases.onGameSaveLoaded(Paths.get(fileGetter.getPath()));

		if (newGameSession) {
			newGameSession = false;
			log.debug("Game just started");
			phases.initNewGameSession();
		} else {
			phases.onGameSaveReloaded();
		}
	}

	/**
	 * Reset initialisation states
	 */
	public void reset() {
		this.initGamePresent = false;
		this.initGameRunning = false;
	}
}