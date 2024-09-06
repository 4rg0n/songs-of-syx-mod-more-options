package com.github.argon.sos.moreoptions.game;

import com.github.argon.sos.moreoptions.phase.Phases;
import com.github.argon.sos.moreoptions.phase.state.State;
import com.github.argon.sos.moreoptions.phase.state.StateManager;
import lombok.RequiredArgsConstructor;
import script.SCRIPT;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;
import view.main.VIEW;

import java.io.IOException;

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

	private final Phases scriptPhases;
	private final StateManager stateManager = StateManager.getInstance();

	@Override
	public void update(double v) {
		State state = stateManager.getState();

		if (!state.isInitGameUpdating()) {
			state.setInitGameUpdating(true);
			scriptPhases.initGameUpdating();
		}

		scriptPhases.onGameUpdate(v);

		if (!state.isInitGameUiPresent() && !VIEW.inters().load.isActivated()) {
			state.setInitGameUiPresent(true);
			scriptPhases.initGameUiPresent();
		}

		if (!state.isInitSettlementUiPresent() && VIEW.s() != null && VIEW.s().isActive()) {
			state.setInitSettlementUiPresent(true);
			scriptPhases.initSettlementUiPresent();
		}
	}

	@Override
	public void save(FilePutter filePutter) {
		State state = stateManager.getState();
		state.setGameSaved(true);
		scriptPhases.onGameSaved(filePutter.path);
	}

	@Override
	public void load(FileGetter fileGetter) throws IOException {
		State state = stateManager.getState();
		state.setGameSaveLoaded(true);
		scriptPhases.onGameSaveLoaded(fileGetter.path);

		if (state.isNewGameSession()) {
			state.setNewGameSession(false);
			state.setNewGame(false);
			scriptPhases.initNewGameSession();
		} else {
			state.setGameSaveReloaded(true);
			scriptPhases.onGameSaveReloaded();
		}
	}
}