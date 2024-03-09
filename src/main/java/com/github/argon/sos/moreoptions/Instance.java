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
 *
 * Some calls for the {@link com.github.argon.sos.moreoptions.phase.PhaseManager} originate from here.
 *
 * todo add game state object: containing flags from here; save info; mod info;... (how refresh state? o.o)
 */
@RequiredArgsConstructor
final class Instance implements SCRIPT.SCRIPT_INSTANCE {

	private final static Logger log = Loggers.getLogger(Instance.class);

	private boolean initGameRunning = false;
	private boolean initGamePresent = false;
	private boolean newGameSession = true;

	private final Phases scriptPhases;

	@Override
	public void update(double v) {
		if (!initGameRunning) {
			initGameRunning = true;
			scriptPhases.initGameUpdating();
		}

		if (!initGamePresent && !VIEW.inters().load.isActivated()) {
			initGamePresent = true;
			scriptPhases.initGameUiPresent();
		}

		scriptPhases.onGameUpdate(v);
	}

	@Override
	public void save(FilePutter filePutter) {
		scriptPhases.onGameSaved(filePutter.getPath());
	}

	@Override
	public void load(FileGetter fileGetter) throws IOException {
		scriptPhases.onGameSaveLoaded(Paths.get(fileGetter.getPath()));

		if (newGameSession) {
			newGameSession = false;
			log.debug("Game just started");
			scriptPhases.initNewGameSession();
		} else {
			scriptPhases.onGameSaveReloaded();
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