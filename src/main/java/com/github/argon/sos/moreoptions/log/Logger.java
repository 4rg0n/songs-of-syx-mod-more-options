package com.github.argon.sos.moreoptions.log;


import lombok.Getter;
import lombok.Setter;
import snake2d.LOG;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.logging.Level;

import static com.github.argon.sos.moreoptions.util.StringUtil.*;

/**
 * trace {@link Level#FINER}
 * debug {@link Level#FINE}
 * info {@link Level#INFO}
 * warn {@link Level#WARNING}
 * error {@link Level#SEVERE}
 */
public class Logger {

    public static final String PREFIX_MOD = "MO";
    private final static Level DEFAULT_LEVEL = Level.INFO;
    private final static String LOG_MSG_FORMAT = "[%s|%s]%s[%s] %s";
    private final static int NAME_DISPLAY_MAX_LENGTH = 32;

    @Getter
    private final String name;
    @Getter
    private final String shortName;
    @Getter
    private final String displayName;

    @Getter
    @Setter
    private Level level;

    public Logger(Class<?> clazz) {
        this(clazz, DEFAULT_LEVEL);
    }

    public Logger(Class<?> clazz, Level level) {
        this.name = clazz.getCanonicalName();
        this.shortName = shortenName(clazz);
        this.displayName = cutOrFill(shortName, NAME_DISPLAY_MAX_LENGTH, false);
        this.level = level;
    }

    public boolean isLevel(Level level) {
        return (this.level.intValue() > level.intValue());
    }

    public void info(String formatMsg, Object... args) {
        log("INFO", Level.INFO, formatMsg, args);
    }

    public void debug(String formatMsg, Object... args) {
        log("DEBUG", Level.FINE, formatMsg, args);
    }

    public void trace(String formatMsg, Object... args) {
        log("TRACE", Level.FINER, formatMsg, args);
    }

    public void warn(String formatMsg, Object... args) {
        log("WARN", Level.WARNING, formatMsg, args);
    }

    public void error(String formatMsg, Object... args) {
        logErr("ERROR", Level.SEVERE, formatMsg, args);
    }


    private void log(String msgPrefix, Level level, String formatMsg, Object... args) {
        if (isLevel(level)) {
            return;
        }

        Throwable ex = extractThrowable(args);

        if (ex != null) {
            args = Arrays.copyOf(args, args.length - 1);

            doLog(msgPrefix, formatMsg, args);
            printException(ex);
        } else {
            doLog(msgPrefix, formatMsg, args);
        }
    }

    private void doLog(String msgPrefix, String formatMsg, Object[] args) {
        LOG.ln(String.format(LOG_MSG_FORMAT,
            PREFIX_MOD,
            timestamp(),
            displayName,
            msgPrefix,
            String.format(formatMsg, stringifyValues(args))));
    }

    private void logErr(String msgPrefix, Level level, String formatMsg, Object... args) {
        if (isLevel(level)) {
            return;
        }

        Throwable ex = extractThrowable(args);

        if (ex != null) {
            args = Arrays.copyOf(args, args.length - 1);
            doLogErr(msgPrefix, formatMsg, args);
            printException(ex);
        } else {
            doLogErr(msgPrefix, formatMsg, args);
        }
    }

    private void doLogErr(String msgPrefix, String formatMsg, Object[] args) {
        LOG.err((String.format(LOG_MSG_FORMAT,
            PREFIX_MOD,
            timestamp(),
            displayName,
            msgPrefix,
            String.format(formatMsg, stringifyValues(args)))));
    }

    private void printException(Throwable ex) {
        System.out.println("\n" + ex.getMessage());
        ex.printStackTrace(System.out);
        System.out.println();
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

    private String timestamp() {
        return LocalTime.now().toString();
    }
}
