package com.github.argon.sos.mod.sdk.log.writer;

public interface LogWriter {
    void error(String loggerName, String msgPrefix, String formatMsg, Object[] args);
    void log(String loggerName, String msgPrefix, String formatMsg, Object[] args);
    void exception(Throwable exception);
}
