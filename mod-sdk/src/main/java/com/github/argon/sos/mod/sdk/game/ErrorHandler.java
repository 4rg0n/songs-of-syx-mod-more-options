package com.github.argon.sos.mod.sdk.game;

import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.phase.Phases;
import lombok.RequiredArgsConstructor;
import snake2d.Errors;

/**
 * For custom handling of game crashes
 *
 * @param <Script> type of the mod script
 */
@RequiredArgsConstructor
public class ErrorHandler<Script extends Phases> extends init.error.ErrorHandler {

    private final static Logger log = Loggers.getLogger(ErrorHandler.class);

    private final Script script;

    @Override
    public void handle(Throwable throwable, String dump) {
        script.onCrash(throwable);
        super.handle(throwable, dump);
    }

    @Override
    public void handle(Errors.DataError dataError, String dump) {
        script.onCrash(dataError);
        super.handle(dataError, dump);
    }

    @Override
    public void handle(Errors.GameError gameError, String dump) {
        script.onCrash(gameError);
        super.handle(gameError, dump);
    }
}
