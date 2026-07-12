package com.github.argon.sos.mod.sdk.game.error;

/**
 * Used to force the game to a log output into the error logs
 */
public class DumpLogsException extends Throwable {
    /**
     * Creates a new {@link DumpLogsException} with a message.
     *
     * @param message describing why the logs are dumped
     * @see Throwable#Throwable(String)
     */
    public DumpLogsException(String message) {
        super(message);
    }
}
