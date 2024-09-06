package com.github.argon.sos.mod.sdk.phase.state;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class State {
    private boolean initGameUpdating = false;
    private boolean initGameUiPresent = false;
    private boolean initWorldUiPresent = false;
    private boolean initSettlementUiPresent = false;

    private boolean newGameSession = true;
    private boolean newGame = true;
    private boolean gameSaveReloaded = false;
    private boolean gameSaveLoaded = false;
    private boolean gameSaved = false;
}
