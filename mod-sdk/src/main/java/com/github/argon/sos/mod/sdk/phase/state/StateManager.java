package com.github.argon.sos.mod.sdk.phase.state;

import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StateManager {

    @Getter
    private final State state = new State();

    public void reset() {
        state.setInitGameUiPresent(false);
        state.setInitGameUpdating(false);
        state.setInitSettlementUiPresent(false);
        state.setInitWorldUiPresent(false);
        state.setNewGame(true);
        state.setGameSaved(false);
        state.setGameSaveLoaded(false);
        state.setGameSaveReloaded(false);
    }
}
