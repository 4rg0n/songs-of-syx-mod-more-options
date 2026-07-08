package com.github.argon.sos.mod.sdk.metric;

import game.time.TIME;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Serves as holder for the collected game stats.
 * Has some information about time.
 */
@Getter
@Builder
@AllArgsConstructor
public class Metric {
    @Builder.Default
    private Instant timestamp = Instant.now();

    @Builder.Default
    private GameTime gameTime = GameTime.builder().build();

    @Builder.Default
    private Map<String, Object> values = new HashMap<>();

    /**
     * Returns the header names to be used in a table format like CSV
     *
     * @return all header names
     */
    public static List<String> getHeaders() {
        List<String> headersMetric = new ArrayList<>();
        headersMetric.add("TIMESTAMP");
        headersMetric.addAll(GameTime.getHeaders());

        return headersMetric;
    }

    /**
     * Returns the header names of stats only.
     *
     * @return stat header names
     */
    public List<String> getHeaderStats() {
        return getValues().keySet().stream()
            .sorted()
            .toList();
    }

    /**
     * Returns the value for the given header or null when there is no value for it.
     *
     * @param headerName to get the value from
     * @return header value or null if not present
     */
    @Nullable
    public Object get(String headerName) {
        return switch (headerName) {
            case "TIMESTAMP" -> getTimestamp().getEpochSecond();
            case "AGE" -> getGameTime().getAge();
            case "SEASONS" -> getGameTime().getSeasons();
            case "SEASON" -> getGameTime().getSeason();
            case "YEARS" -> getGameTime().getYears();
            case "DAYS" -> getGameTime().getDays();
            case "HOURS" -> getGameTime().getHours();
            case "CURRENT_SECONDS" -> getGameTime().getCurrentSeconds();
            case "PLAY_TIME_SECONDS" -> getGameTime().getPlayTimeSeconds();
            default -> null;
        };
    }

    @Getter
    @Builder
    @ToString
    @AllArgsConstructor
    public static class GameTime {
        @Builder.Default
        private int age = TIME.age().bitCurrent();

        @Builder.Default
        private int seasons = TIME.seasons().bitCurrent();

        @Builder.Default
        private int season = TIME.season().index();

        @Builder.Default
        private int years = TIME.years().bitCurrent();

        @Builder.Default
        private int days = TIME.days().bitCurrent();

        @Builder.Default
        private int hours = TIME.hours().bitCurrent();

        @Builder.Default
        private double currentSeconds = TIME.currentSecond();

        @Builder.Default
        private double playTimeSeconds = TIME.playedGame();

        /**
         * Returns the header names for the game time.
         *
         * @return header names
         */
        public static List<String> getHeaders() {
            return List.of("AGE", "SEASONS", "SEASON", "YEARS", "DAYS", "HOURS", "CURRENT_SECONDS", "PLAY_TIME_SECONDS");
        }
    }
}
