package com.github.argon.sos.mod.sdk.log.writer;

import com.github.argon.sos.mod.sdk.util.StringUtil;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;

/**
 * Base class for {@link LogWriter}s.
 */
@RequiredArgsConstructor
public abstract class AbstractLogWriter implements LogWriter {
    /**
     * Contains the prefix for each log message. E.g. your shortened mod name.
     */
    protected final String prefix;
    /**
     * Contains the message format for writing the log messages.
     */
    protected final String messageFormat;

    /**
     * Used when logging fails for some reason. E.g. a malformed message.
     *
     * @param formatMsg used for logging
     * @param args used in the log message
     * @param exception thrown exception
     */
    protected void problemLogging(String formatMsg, Object[] args, Throwable exception) {
        System.err.println("PROBLEM WHILE LOGGING!");
        System.err.println("formatMsg: " + formatMsg);
        System.err.println(StringUtil.toString(StringUtil.stringifyValues(args)));

        exception(exception);
    }

    /**
     * Returns the current timestamp.
     *
     * @return current timestamp
     */
    protected String timestamp() {
        return LocalTime.now().toString();
    }
}
