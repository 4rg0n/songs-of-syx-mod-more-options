package com.github.argon.sos.moreoptions;

import com.github.argon.sos.moreoptions.game.SCRIPT;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import init.error.ErrorHandler;
import lombok.RequiredArgsConstructor;
import snake2d.Errors;

@RequiredArgsConstructor
public class MoreOptionsErrorHandler<T extends SCRIPT<?>> extends ErrorHandler {

    private final static Logger log = Loggers.getLogger(MoreOptionsErrorHandler.class);

    private final T script;

    @Override
    public void handle(Throwable throwable, String dump) {
        script.crash(throwable);
        super.handle(throwable, dump);
    }

    @Override
    public void handle(Errors.DataError dataError, String dump) {
        script.crash(dataError);
        super.handle(dataError, dump);
    }

    @Override
    public void handle(Errors.GameError gameError, String dump) {
        script.crash(gameError);
        super.handle(gameError, dump);
    }
}
