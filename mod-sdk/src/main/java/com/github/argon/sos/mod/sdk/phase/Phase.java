package com.github.argon.sos.mod.sdk.phase;

/**
 * See {@link Phases} for more.
 */
public enum Phase {
    // 1. Phase
    INIT_BEFORE_GAME_CREATED,
    // 2. Phase
    INIT_MOD_CREATE_INSTANCE,
    // 3. Phase
    ON_GAME_SAVE_LOADED,
    // 4. Phase
    ON_GAME_SAVE_RELOADED,
    // 5. Phase
    INIT_NEW_GAME_SESSION,
    // 6. Phase
    INIT_GAME_UPDATING,
    // 7. Phase
    ON_GAME_UPDATE,
    // 8. Phase
    INIT_GAME_UI_PRESENT,
    // 10. Phase
    INIT_SETTLEMENT_UI_PRESENT,
    ON_GAME_SAVED,
    ON_CRASH
}
