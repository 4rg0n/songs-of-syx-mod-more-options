package com.github.argon.sos.moreoptions.metric;

import com.github.argon.sos.moreoptions.util.Lists;
import game.time.TIME;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

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

    public static List<String> getHeaders() {
        List<String> headersMetric = new ArrayList<>();
        headersMetric.add("TIMESTAMP");
        headersMetric.addAll(GameTime.getHeaders());

        return headersMetric;
    }

    // todo doesn't scale well and is ugly... but works
    public Object get(String key) {
        switch (key) {
            case "TIMESTAMP":
                return getTimestamp().getEpochSecond();
            case "AGE":
                return getGameTime().getAge();
            case "SEASONS":
                return getGameTime().getSeasons();
            case "SEASON":
                return getGameTime().getSeason();
            case "YEARS":
                return getGameTime().getYears();
            case "DAYS":
                return getGameTime().getDays();
            case "HOURS":
                return getGameTime().getHours();
            case "CURRENT_SECONDS":
                return getGameTime().getCurrentSeconds();
            case "PLAY_TIME_SECONDS":
                return getGameTime().getPlayTimeSeconds();
            default:
                return null;
        }

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

        public String formatted() {
            return "age=" + age +
                " seasons=" + seasons +
                " season=" + season +
                " years=" + years +
                " days=" + days +
                " hours=" + hours +
                " currentSeconds=" + currentSeconds +
                " playTimeSeconds=" + playTimeSeconds;
        }

        public static List<String> getHeaders() {
            return Lists.of("AGE", "SEASONS", "SEASON", "YEARS", "DAYS", "HOURS", "CURRENT_SECONDS", "PLAY_TIME_SECONDS");
        }
    }
}
