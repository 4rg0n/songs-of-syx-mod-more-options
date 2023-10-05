package com.github.argon.sos.moreoptions;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Getter(lazy = true)
    private final static ExceptionHandler instance = new ExceptionHandler();

    private final static Logger log = Loggers.getLogger(ExceptionHandler.class);

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("Ohoh!", e);
    }
}
