package com.github.argon.sos.moreoptions.game;

import com.github.argon.sos.moreoptions.phase.Phases;
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
public final class ScriptInstance implements SCRIPT.SCRIPT_INSTANCE {

	/**
	 * Whether the game started calling its update() methods
	 */
	private boolean initGameUpdating = false;

	/**
	 * Whether the games ui is displayed
	 */
	private boolean initGameUiPresent = false;

	/**
	 * Whether this is a new game session or not (not a new game)
	 */
	private boolean newGameSession = true;

	private final Phases scriptPhases;

	@Override
	public void update(double v) {
		if (!initGameUpdating) {
			initGameUpdating = true;
			scriptPhases.initGameUpdating();
		}

		if (!initGameUiPresent && !VIEW.inters().load.isActivated()) {
			initGameUiPresent = true;
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
			scriptPhases.initNewGameSession();
		} else {
			scriptPhases.onGameSaveReloaded();
		}
	}

	/**
	 * Reset initialisation states
	 */
	public void reset() {
		this.initGameUiPresent = false;
		this.initGameUpdating = false;
	}
}