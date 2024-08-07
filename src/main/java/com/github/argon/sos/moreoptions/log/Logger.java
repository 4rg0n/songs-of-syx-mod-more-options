package com.github.argon.sos.moreoptions.log;


import com.github.argon.sos.moreoptions.log.writer.LogWriter;
import com.github.argon.sos.moreoptions.log.writer.StdOut;
import com.github.argon.sos.moreoptions.util.StringUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

/**
 * For printing messages with different log {@link Level}s to the system output
 */
@Getter
public class Logger {

    public static final String PREFIX_MOD = "MO";
    private final static Level DEFAULT_LEVEL = Loggers.getRootLevel();
    private final static String LOG_MSG_FORMAT = "[%s|%s]%s[%s] %s";
    private final static int NAME_DISPLAY_MAX_LENGTH = 32;

    private final String name;
    private final String shortName;
    private final String displayName;

    @Setter
    private Level level;
    @Setter
    private LogWriter writer;

    public Logger(Class<?> clazz) {
        this(clazz, DEFAULT_LEVEL);
    }

    public Logger(Class<?> clazz, Level level) {
        this.name = clazz.getCanonicalName();
        this.shortName = StringUtil.shortenName(clazz);
        this.displayName = StringUtil.cutOrFill(shortName, NAME_DISPLAY_MAX_LENGTH, false);
        this.level = level;
        this.writer = new StdOut(PREFIX_MOD, LOG_MSG_FORMAT, displayName);
    }

    public boolean isLevel(Level level) {
        return (this.level.getValue() > level.getValue());
    }

    public void info(String formatMsg, Object... args) {
        log(Level.INFO, formatMsg, args);
    }

    public void debug(String formatMsg, Object... args) {
        log(Level.DEBUG, formatMsg, args);
    }

    public void trace(String formatMsg, Object... args) {
        log(Level.TRACE, formatMsg, args);
    }

    public void warn(String formatMsg, Object... args) {
        log(Level.WARN, formatMsg, args);
    }

    public void error(String formatMsg, Object... args) {
        logErr(Level.ERROR, formatMsg, args);
    }

    public void critical(String formatMsg, Object... args) {
        logErr(Level.CRIT, formatMsg, args);
    }


    private void log(Level level, String formatMsg, Object... args) {
        if (isLevel(level)) {
            return;
        }

        Throwable ex = extractThrowable(args);

        if (ex != null) {
            args = Arrays.copyOf(args, args.length - 1);

            doLog(levelText(level), formatMsg, args);
            writer.exception(ex);
        } else {
            doLog(levelText(level), formatMsg, args);
        }
    }

    private void logErr(Level level, String formatMsg, Object... args) {
        if (isLevel(level)) {
            return;
        }

        Throwable ex = extractThrowable(args);

        if (ex != null) {
            args = Arrays.copyOf(args, args.length - 1);
            doLogErr(levelText(level), formatMsg, args);
            writer.exception(ex);
        } else {
            doLogErr(levelText(level), formatMsg, args);
        }
    }

    private void doLogErr(String msgPrefix, String formatMsg, Object[] args) {
        writer.error(msgPrefix, formatMsg, args);
    }

    private void doLog(String prefix, String formatMsg, Object[] args) {
        writer.log(prefix, formatMsg, args);
    }

    private String levelText(Level level) {
        return level.getName();
    }

    private Throwable extractThrowable(Object[] args) {
        Object lastArg = null;
        int lastPos = args.length - 1;

        if (lastPos >= 0) {
            lastArg = args[lastPos];
        }

        if (lastArg instanceof Throwable) {
            return (Throwable) lastArg;
        }

        return null;
    }
}
