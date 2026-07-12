package com.github.argon.sos.mod.sdk.game.error;

import com.github.argon.sos.mod.sdk.phase.Phases;
import lombok.RequiredArgsConstructor;
import snake2d.Errors;

/**
 * For custom handling of game crashes
 *
 * @param <Script> type of the mod script
 */
@RequiredArgsConstructor
public class ErrorHandler<Script extends Phases> extends util.error.ErrorHandler {

    private final Script script;

    /**
     * Handles thrown exceptions ({@link Throwable})
     *
     * @param throwable thrown exception
     * @param dump log to dump
     */
    @Override
    public void handle(Throwable throwable, String dump) {
        script.onCrash(throwable);
        super.handle(throwable, dump);
    }

    /**
     * Handles game data errors ({@link snake2d.Errors.DataError})
     *
     * @param dataError occurred error
     * @param dump log to dump
     */
    @Override
    public void handle(Errors.DataError dataError, String dump) {
        script.onCrash(dataError);
        super.handle(dataError, dump);
    }

    /**
     * Handles game errors ({@link snake2d.Errors.GameError})
     *
     * @param gameError occurred error
     * @param dump log to dump
     */
    @Override
    public void handle(Errors.GameError gameError, String dump) {
        script.onCrash(gameError);
        super.handle(gameError, dump);
    }
}
