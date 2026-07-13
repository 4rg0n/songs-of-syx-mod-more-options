package com.github.argon.sos.mod.sdk.phase.state;

import lombok.*;

/**
 * Contains various game state checks.
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class State {
    /**
     * Whether the game update loop has started.
     */
    @Builder.Default
    private boolean initGameUpdating = false;

    /**
     * Whether the game ui is present.
     */
    @Builder.Default
    private boolean initGameUiPresent = false;

    /**
     * Whether the world ui is present.
     */
    @Builder.Default
    private boolean initWorldUiPresent = false;

    /**
     * Whether the settlement ui is present.
     */
    @Builder.Default
    private boolean initSettlementUiPresent = false;

    /**
     * Whether this is a new game session and not a loaded save game.
     */
    @Builder.Default
    private boolean newGameSession = true;

    /**
     * Whether this is a new game.
     */
    @Builder.Default
    private boolean newGame = true;

    /**
     * Whether the game was reloaded from a save.
     */
    @Builder.Default
    private boolean gameSaveReloaded = false;

    /**
     * Whether a game was loaded from a save.
     */
    @Builder.Default
    private boolean gameSaveLoaded = false;

    /**
     * Whether the game was saved.
     */
    @Builder.Default
    private boolean gameSaved = false;

    /**
     * Whether the game is in a battle state.
     */
    @Builder.Default
    private boolean battle = false;
}
