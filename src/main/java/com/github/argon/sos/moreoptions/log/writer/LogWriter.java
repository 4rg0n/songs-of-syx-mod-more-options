package com.github.argon.sos.moreoptions.log.writer;

public interface LogWriter {
    void error(String msgPrefix, String formatMsg, Object[] args);
    void log(String msgPrefix, String formatMsg, Object[] args);
    void exception(Throwable exception);
}
