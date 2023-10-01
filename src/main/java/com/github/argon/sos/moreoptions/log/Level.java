package com.github.argon.sos.moreoptions.log;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class Level {
    @Getter
    private final String name;

    @Getter
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
}
