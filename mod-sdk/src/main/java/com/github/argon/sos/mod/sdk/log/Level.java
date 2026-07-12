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
    /**
     * Log level name.
     */
    private final String name;
    /**
     * Severity of the level.
     * A higher number means a level is more important.
     */
    private final int value;

    /**
     * Critical is the highest severity and is used for log messages,
     * which describe a critical event where the mod isn't able to recover from.
     */
    public static final Level CRIT = new Level("CRIT",1000);

    /**
     * Error is the second-highest severity and is used for log messages,
     * which describe an error event where the mod is able to recover from.
     */
    public static final Level ERROR = new Level("ERROR",900);

    /**
     * Warning is the third-highest severity and is used for log messages,
     * which describe an event where the mod e.g. is missing something,
     * but is able to work normally even without it.
     * E.g. by using a default value instead.
     */
    public static final Level WARN = new Level("WARN", 800);

    /**
     * Information is the fourth-highest severity and is used for log messages,
     * which contain information about processes, which are worth to report.
     */
    public static final Level INFO = new Level("INFO", 700);

    /**
     * Debug is the fifth-highest severity and is used for log messages,
     * describing process flows in detail.
     * They are used to comprehend what might have gone wrong.
     */
    public static final Level DEBUG = new Level("DEBUG", 600);

    /**
     * Trace is the sixth-highest severity and is used for log messages,
     * containing detailed data dumps or messages for very verbose processes.
     */
    public static final Level TRACE = new Level("TRACE", 500);

    /**
     * Creates a {@link Level} from a given name.
     *
     * @param name to create the level from
     * @return created level if possible
     */
    public static Optional<Level> fromName(String name) {
        return switch (name.toLowerCase()) {
            case "crit", "critical" -> Optional.of(CRIT);
            case "error", "err" -> Optional.of(ERROR);
            case "warning", "warn" -> Optional.of(WARN);
            case "info" -> Optional.of(INFO);
            case "debug" -> Optional.of(DEBUG);
            case "trace" -> Optional.of(TRACE);
            default -> Optional.empty();
        };
    }

    /**
     * Returns the name of the log level.
     *
     * @return name of the log level
     */
    @Override
    public String toString() {
        return getName();
    }
}
