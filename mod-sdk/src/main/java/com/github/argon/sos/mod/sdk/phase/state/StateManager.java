package com.github.argon.sos.mod.sdk.phase.state;

import com.github.argon.sos.mod.sdk.game.action.Resettable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Holds the game {@link State}.
 */
@RequiredArgsConstructor
public class StateManager implements Resettable {

    @Getter
    private final State state = new State();

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        state.setInitGameUiPresent(false);
        state.setInitGameUpdating(false);
        state.setInitSettlementUiPresent(false);
        state.setInitWorldUiPresent(false);
        state.setNewGame(true);
        state.setNewGameSession(true);
        state.setGameSaved(false);
        state.setGameSaveLoaded(false);
        state.setGameSaveReloaded(false);
    }
}
