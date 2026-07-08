package com.github.argon.sos.mod.sdk.log;


import com.github.argon.sos.mod.sdk.log.writer.LogWriter;
import com.github.argon.sos.mod.sdk.util.ExceptionUtil;
import com.github.argon.sos.mod.sdk.util.StringUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * For printing messages with different log {@link Level}s to the set {@link LogWriter}s.
 */
@Getter
public class Logger {

    /**
     * Used as prefix for each log message.
     */
    public final static String PREFIX_MOD = "MOD";

    /**
     * Log {@link Level} used when nothing was set.
     */
    public final static Level DEFAULT_LEVEL = Loggers.getRootLevel();

    /**
     * The log message format.
     * Each %s will be replaced in order:
     * * PREFIX_MOD
     * * Current timestamp
     * * Classname
     * * Log level
     * * Log message
     */
    public final static String LOG_MSG_FORMAT = "[%s|%s]%s[%s] %s";

    /**
     * Max length of the classname to display.
     * When the name is longer than the given length, it will be shortened.
     */
    public final static int NAME_DISPLAY_MAX_LENGTH = 32;

    private final String name;
    private final String shortName;
    private final String displayName;

    @Setter
    private Level level;
    private final static Set<LogWriter> writers = new LinkedHashSet<>();

    /**
     * Creates a new {@link Logger} for the given class with the {@link Logger#DEFAULT_LEVEL} as log level.
     *
     * @param clazz for which this logger is responsible
     */
    public Logger(Class<?> clazz) {
        this(clazz, DEFAULT_LEVEL);
    }

    /**
     * Creates a new {@link Logger} for the given class with the given log {@link Level}.
     *
     * @param clazz for which this logger is responsible
     * @param level used when logging messages
     */
    public Logger(Class<?> clazz, Level level) {
        this.name = clazz.getCanonicalName();
        this.shortName = StringUtil.shortenClassName(clazz);
        this.displayName = StringUtil.cutOrFill(shortName, NAME_DISPLAY_MAX_LENGTH, false);
        this.level = level;
    }

    /**
     * Adds a writer to this logger.
     *
     * @param writer to add
     */
    public void addWriter(LogWriter writer) {
        writers.add(writer);
    }

    /**
     * Checks whether this logger would write messages for the given log {@link Level}.
     *
     * @param level to check
     * @return whether it would write messages for the given level
     */
    public boolean isLevel(Level level) {
        return (this.level.getValue() > level.getValue());
    }

    /**
     * Writes a log messages as {@link Level#INFO}.
     * The message can contain placeholders like %s or %d, which will be replaced with the given arguments.
     * @see String#format(String, Object...)
     *
     * The arguments can also contain a {@link Throwable} exception, which will be printed.
     * Example: <pre>log.info("This is an %s log", "info", new Exception())</pre>
     *
     * @param formatMsg message with or without placeholders
     * @param args to be replaced in the message or printed when it contains an exception
     */
    public void info(String formatMsg, Object... args) {
        log(Level.INFO, formatMsg, args);
    }

    /**
     * Writes a log messages as {@link Level#DEBUG}.
     * The message can contain placeholders like %s or %d, which will be replaced with the given arguments.
     * @see String#format(String, Object...)
     *
     * The arguments can also contain a {@link Throwable} exception, which will be printed.
     * Example: <pre>log.debug("This is a %s log", "debug", new Exception())</pre>
     *
     * @param formatMsg message with or without placeholders
     * @param args to be replaced in the message or printed when it contains an exception
     */
    public void debug(String formatMsg, Object... args) {
        log(Level.DEBUG, formatMsg, args);
    }

    /**
     * Writes a log messages as {@link Level#TRACE}.
     * The message can contain placeholders like %s or %d, which will be replaced with the given arguments.
     * @see String#format(String, Object...)
     *
     * The arguments can also contain a {@link Throwable} exception, which will be printed.
     * Example: <pre>log.trace("This is a %s log", "trace", new Exception())</pre>
     *
     * @param formatMsg message with or without placeholders
     * @param args to be replaced in the message or printed when it contains an exception
     */
    public void trace(String formatMsg, Object... args) {
        log(Level.TRACE, formatMsg, args);
    }

    /**
     * Writes a log messages as {@link Level#WARN}.
     * The message can contain placeholders like %s or %d, which will be replaced with the given arguments.
     * @see String#format(String, Object...)
     *
     * The arguments can also contain a {@link Throwable} exception, which will be printed.
     * Example: <pre>log.warn("This is a %s log", "warn", new Exception())</pre>
     *
     * @param formatMsg message with or without placeholders
     * @param args to be replaced in the message or printed when it contains an exception
     */
    public void warn(String formatMsg, Object... args) {
        log(Level.WARN, formatMsg, args);
    }

    /**
     * Writes a log messages as {@link Level#ERROR}.
     * The message can contain placeholders like %s or %d, which will be replaced with the given arguments.
     * @see String#format(String, Object...)
     *
     * The arguments can also contain a {@link Throwable} exception, which will be printed.
     * Example: <pre>log.error("This is a %s log", "error", new Exception())</pre>
     *
     * @param formatMsg message with or without placeholders
     * @param args to be replaced in the message or printed when it contains an exception
     */
    public void error(String formatMsg, Object... args) {
        logErr(Level.ERROR, formatMsg, args);
    }

    /**
     * Writes a log messages as {@link Level#CRIT}.
     * The message can contain placeholders like %s or %d, which will be replaced with the given arguments.
     * @see String#format(String, Object...)
     *
     * The arguments can also contain a {@link Throwable} exception, which will be printed.
     * Example: <pre>log.crit("This is a %s log", "crit", new Exception())</pre>
     *
     * @param formatMsg message with or without placeholders
     * @param args to be replaced in the message or printed when it contains an exception
     */
    public void crit(String formatMsg, Object... args) {
        logErr(Level.CRIT, formatMsg, args);
    }


    private void log(Level level, String formatMsg, Object... args) {
        if (isLevel(level)) {
            return;
        }

        Throwable ex = ExceptionUtil.extractThrowableLast(args);

        if (ex != null) {
            args = Arrays.copyOf(args, args.length - 1);

            doLog(levelText(level), formatMsg, args);
            writers.forEach(writer -> writer.exception(ex));
        } else {
            doLog(levelText(level), formatMsg, args);
        }
    }

    private void logErr(Level level, String formatMsg, Object... args) {
        if (isLevel(level)) {
            return;
        }

        Throwable ex = ExceptionUtil.extractThrowableLast(args);

        if (ex != null) {
            args = Arrays.copyOf(args, args.length - 1);
            doLogErr(levelText(level), formatMsg, args);
            writers.forEach(writer -> writer.exception(ex));
        } else {
            doLogErr(levelText(level), formatMsg, args);
        }
    }

    private void doLogErr(String msgPrefix, String formatMsg, Object[] args) {
        writers.forEach(writer -> writer.error(displayName, msgPrefix, formatMsg, args));
    }

    private void doLog(String prefix, String formatMsg, Object[] args) {
        writers.forEach(writer -> writer.log(displayName, prefix, formatMsg, args));
    }

    private String levelText(Level level) {
        return level.getName();
    }
}
