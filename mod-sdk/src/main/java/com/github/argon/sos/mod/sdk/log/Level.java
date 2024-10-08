package com.github.argon.sos.mod.sdk.log;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/**
 * Levels describing the severity of a log message
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class Level {
    private final String name;
    private final int value;

    public static final Level CRIT = new Level("CRIT",1000);
    public static final Level ERROR = new Level("ERROR",900);
    public static final Level WARN = new Level("WARN", 800);
    public static final Level INFO = new Level("INFO", 700);
    public static final Level DEBUG = new Level("DEBUG", 600);
    public static final Level TRACE = new Level("TRACE", 500);

    public static Optional<Level> fromName(String name) {
        switch (name.toLowerCase()) {
            case "crit":
            case "critical":
                return Optional.of(CRIT);
            case "error":
            case "err":
                return Optional.of(ERROR);
            case "warning":
            case "warn":
                return Optional.of(WARN);
            case "info":
                return Optional.of(INFO);
            case "debug":
                return Optional.of(DEBUG);
            case "trace":
                return Optional.of(TRACE);
            default:
                return Optional.empty();
        }
    }

    @Override
    public String toString() {
        return getName();
    }
}
