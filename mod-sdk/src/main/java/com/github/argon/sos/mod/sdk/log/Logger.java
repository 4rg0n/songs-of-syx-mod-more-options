package com.github.argon.sos.mod.sdk.log;


import com.github.argon.sos.mod.sdk.log.writer.LogWriter;
import com.github.argon.sos.mod.sdk.util.ClassUtil;
import com.github.argon.sos.mod.sdk.util.ExceptionUtil;
import com.github.argon.sos.mod.sdk.util.StringUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * For printing messages with different log {@link Level}s to the system output
 */
@Getter
public class Logger {

    public static final String PREFIX_MOD = "MOD";
    public final static Level DEFAULT_LEVEL = Loggers.getRootLevel();
    public final static String LOG_MSG_FORMAT = "[%s|%s]%s[%s] %s";
    private final static int NAME_DISPLAY_MAX_LENGTH = 32;

    private final String name;
    private final String shortName;
    private final String displayName;

    @Setter
    private Level level;
    private final static Set<LogWriter> writers = new LinkedHashSet<>();

    public Logger(Class<?> clazz) {
        this(clazz, DEFAULT_LEVEL);
    }

    public Logger(Class<?> clazz, Level level) {
        this.name = clazz.getCanonicalName();
        this.shortName = ClassUtil.shortenClassName(clazz);
        this.displayName = StringUtil.cutOrFill(shortName, NAME_DISPLAY_MAX_LENGTH, false);
        this.level = level;
    }

    public void addWriter(LogWriter writer) {
        writers.add(writer);
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
