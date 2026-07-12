package com.github.argon.sos.mod.sdk.log;

import com.github.argon.sos.mod.sdk.log.writer.LogWriter;
import com.github.argon.sos.mod.sdk.log.writer.StdOutWriter;
import lombok.Getter;
import snake2d.LOG;

import java.util.*;

/**
 * For creating, registering and receiving {@link Logger}s.
 */
public class Loggers {

    /**
     * Creates a new {@link Loggers}.
     */
    public Loggers() {
    }

    private final static Map<String, Logger> loggers = new HashMap<>();
    private final static Set<LogWriter> writers = new LinkedHashSet<>();

    static {
        registerWriter(new StdOutWriter(Logger.PREFIX_MOD, Logger.LOG_MSG_FORMAT));
    }

    /**
     * Log level used when nothing was configured.
     */
    public final static Level LOG_LEVEL_DEFAULT = Level.INFO;

    @Getter
    private static Level rootLevel = LOG_LEVEL_DEFAULT;

    /**
     * Will register an additional writer.
     * Each newly created Logger will receive all registered writers.
     *
     * @param writer to register
     */
    public static void registerWriter(LogWriter writer) {
        writers.add(writer);
    }

    /**
     * Adds a writer to all currently registered loggers.
     *
     * @param writer to add
     */
    public static void addWriter(LogWriter writer) {
        loggers.forEach((name, logger) -> logger.addWriter(writer));
    }

    /**
     * Returns a registered Logger. Or register it when not already present.
     *
     * @param clazz for which the logger is responsible
     * @return the registered logger
     */
    public static Logger getLogger(Class<?> clazz) {
        if (!loggers.containsKey(clazz.getName())) {
            Logger logger = new Logger(clazz);
            writers.forEach(logger::addWriter);
            loggers.put(clazz.getName(), logger);
        }

        return loggers.get(clazz.getName());
    }

    /**
     * Changes the log level of the given logger
     *
     * @param clazz of the logger to change
     * @param level to set
     */
    public static void setLevel(Class<?> clazz, Level level) {
        setLevels(clazz.getName(), level);
    }

    /**
     * Sets the log level of all registered loggers
     *
     * @param level to set
     */
    public static void setLevels(Level level) {
        if (level.equals(rootLevel)) {
            return;
        }

        LOG.ln("Setting loggers root level: " + level);
        rootLevel = level;
        loggers.forEach((name, logger) -> logger.setLevel(level));
    }

    /**
     * Sets the level of all loggers which class name starts with (including package name)
     *
     * @param nameStartsWith prefix the logger's class name must start with
     * @param level to set
     */
    public static void setLevels(String nameStartsWith, Level level) {
        loggers.forEach((name, logger) -> {
            if (name.startsWith(nameStartsWith)) {
                logger.setLevel(level);
            }
        });
    }
}
