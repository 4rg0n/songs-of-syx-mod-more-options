package com.github.argon.sos.moreoptions.log;

import java.util.HashMap;
import java.util.Map;

public class Loggers {
    private final static Map<String, Logger> loggers = new HashMap<>();


    public static Logger getLogger(Class<?> clazz) {
        if (!loggers.containsKey(clazz.getName())) {
            loggers.put(clazz.getName(), new Logger(clazz));
        }

        return loggers.get(clazz.getName());
    }


    public static void setLevel(Class<?> clazz, Level level) {
        setLevels(clazz.getName(), level);
    }

    public static void setLevels(Level level) {
        loggers.forEach((name, logger) -> logger.setLevel(level));
    }

    public static void setLevels(String nameStartsWith, Level level) {
        loggers.forEach((name, logger) -> {
            if (name.startsWith(nameStartsWith)) {
                logger.setLevel(level);
            }
        });
    }
}
