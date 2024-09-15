package com.github.argon.sos.mod.sdk.log.writer;

import com.github.argon.sos.mod.sdk.util.StringUtil;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;

@RequiredArgsConstructor
public abstract class AbstractLogWriter implements LogWriter {
    protected final String prefix;
    protected final String messageFormat;
    protected final String name;

    protected void problemLogging(String formatMsg, Object[] args, Throwable e) {
        System.err.println("PROBLEM WHILE LOGGING!");
        System.err.println("formatMsg: " + formatMsg);
        System.err.println(StringUtil.toString(StringUtil.stringifyValues(args)));

        exception(e);
    }

    protected String timestamp() {
        return LocalTime.now().toString();
    }
}
