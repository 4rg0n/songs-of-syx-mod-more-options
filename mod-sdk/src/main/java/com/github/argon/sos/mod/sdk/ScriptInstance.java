package com.github.argon.sos.mod.sdk;

import com.github.argon.sos.mod.sdk.phase.PhaseManager;
import com.github.argon.sos.mod.sdk.phase.Phases;
import com.github.argon.sos.mod.sdk.phase.state.State;
import com.github.argon.sos.mod.sdk.phase.state.StateManager;
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
 * Some calls for the {@link PhaseManager} originate from here.
 */
@RequiredArgsConstructor
public final class ScriptInstance implements SCRIPT.SCRIPT_INSTANCE {

	private final Phases scriptPhases;
	private final StateManager stateManager;

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
		ModSdkModule.gameApis().save().setCurrent(filePutter);
		// intentionally not calling phaseManager.onGameSaved()
	}

	@Override
	public void load(FileGetter fileGetter) throws IOException {
		State state = stateManager.getState();
		state.setGameSaveLoaded(true);
		ModSdkModule.gameApis().save().setCurrent(fileGetter);
		// intentionally not calling phaseManager.onGameLoaded()

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