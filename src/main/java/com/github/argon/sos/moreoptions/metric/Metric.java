package com.github.argon.sos.moreoptions.metric;

import game.time.TIME;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

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
        private double currentSecond = TIME.currentSecond();

        @Builder.Default
        private double playTimeSeconds = TIME.playedGame();

        public String formatted() {
            return "age=" + age +
                " seasons=" + seasons +
                " season=" + season +
                " years=" + years +
                " days=" + days +
                " hours=" + hours +
                " currentSecond=" + currentSecond +
                " playTimeSeconds=" + playTimeSeconds;
        }
    }
}
