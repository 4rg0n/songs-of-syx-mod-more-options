package com.github.argon.sos.moreoptions;

import com.github.argon.sos.moreoptions.phase.Phases;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import init.error.ErrorHandler;
import lombok.RequiredArgsConstructor;
import snake2d.Errors;

/**
 * For custom handling game crashes
 *
 * @param <Script> type of the mod script
 */
@RequiredArgsConstructor
public class CustomErrorHandler<Script extends Phases> extends ErrorHandler {

    private final static Logger log = Loggers.getLogger(CustomErrorHandler.class);

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
