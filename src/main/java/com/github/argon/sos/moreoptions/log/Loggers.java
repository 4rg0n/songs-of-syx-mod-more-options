package com.github.argon.sos.moreoptions.log;

import java.util.HashMap;
import java.util.Map;

/**
 * For registering and receiving Loggers.
 */
public class Loggers {
    private final static Map<String, Logger> loggers = new HashMap<>();

    /**
     * Returns a registered Logger. Or registers it when not already present.
     */
    public static Logger getLogger(Class<?> clazz) {
        if (!loggers.containsKey(clazz.getName())) {
            loggers.put(clazz.getName(), new Logger(clazz));
        }

        return loggers.get(clazz.getName());
    }

    /**
     * Changes the log level of the given logger
     */
    public static void setLevel(Class<?> clazz, Level level) {
        setLevels(clazz.getName(), level);
    }

    /**
     * Sets the log level of all registered loggers
     */
    public static void setLevels(Level level) {
        loggers.forEach((name, logger) -> logger.setLevel(level));
    }

    /**
     * Sets the level of all loggers which class name starts with
     */
    public static void setLevels(String nameStartsWith, Level level) {
        loggers.forEach((name, logger) -> {
            if (name.startsWith(nameStartsWith)) {
                logger.setLevel(level);
            }
        });
    }
}
