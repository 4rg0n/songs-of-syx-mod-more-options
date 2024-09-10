package com.github.argon.sos.mod.sdk.game.error;

/**
 * Used to force the game to a log output into the error logs
 */
public class DumpLogsException extends Throwable {
    public DumpLogsException(String message) {
        super(message);
    }
}
