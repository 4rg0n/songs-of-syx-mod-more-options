package com.github.argon.sos.moreoptions.phase.state;

import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class StateManager {
    private final static Logger log = Loggers.getLogger(StateManager.class);

    @Getter(lazy = true)
    private final static StateManager instance = new StateManager();

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
