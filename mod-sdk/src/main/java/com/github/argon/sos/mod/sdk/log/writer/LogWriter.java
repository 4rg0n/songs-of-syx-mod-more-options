package com.github.argon.sos.mod.sdk.log.writer;

/**
 * Interface for all log writers.
 */
public interface LogWriter {
    /**
     * Prints a log message as an error.
     *
     * @param loggerName usually the class name the logger is responsible for
     * @param msgPrefix for the message. Usually the log level.
     * @param formatMsg the log message
     * @param args to replace in the message
     */
    void error(String loggerName, String msgPrefix, String formatMsg, Object[] args);

    /**
     * Prints a log message as a normal message.
     *
     * @param loggerName usually the class name the logger is responsible for
     * @param msgPrefix for the message. Usually the log level.
     * @param formatMsg the log message
     * @param args to replace in the message
     */
    void log(String loggerName, String msgPrefix, String formatMsg, Object[] args);

    /**
     * Formats and prints a {@link Throwable} exception.
     *
     * @param exception to format and print
     */
    void exception(Throwable exception);
}
