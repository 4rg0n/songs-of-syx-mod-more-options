package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.json.Json;
import com.github.argon.sos.moreoptions.json.JsonMapper;
import com.github.argon.sos.moreoptions.log.Level;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.ui.UiMapper;
import lombok.*;

import java.util.*;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MoreOptionsV3Config {

    public final static int VERSION = 3;

    @Builder.Default
    private int version = VERSION;
    @Builder.Default
    private Level logLevel = ConfigDefaults.CONFIG_DEFAULT_LOG_LEVEL;

    @Builder.Default
    private Sounds sounds = Sounds.builder().build();
    @Builder.Default
    private Events events = Events.builder().build();
    @Builder.Default
    private Map<String, Range> weather = new HashMap<>();
    @Builder.Default
    private BoostersConfig boosters = BoostersConfig.builder().build();
    @Builder.Default
    private Set<BoostersPreset> boostersPresets = new HashSet<>();
    @Builder.Default
    private Metrics metrics = Metrics.builder().build();
    @Builder.Default
    private RacesConfig races = RacesConfig.builder().build();

    @Data
    @Builder
    @EqualsAndHashCode
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Events {
        @Builder.Default
        private Map<String, Boolean> settlement = new HashMap<>();
        @Builder.Default
        private Map<String, Boolean> world = new HashMap<>();
        @Builder.Default
        private Map<String, Range> chance = new HashMap<>();
    }

    @Data
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Sounds {
        @Builder.Default
        private Map<String, Range> ambience = new HashMap<>();
        @Builder.Default
        private Map<String, Range> settlement = new HashMap<>();
        @Builder.Default
        private Map<String, Range> room = new HashMap<>();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @EqualsAndHashCode
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Metrics {
        @Builder.Default
        private boolean enabled = false;
        @Builder.Default
        private Range collectionRateSeconds = ConfigDefaults.metricCollectionRate();
        @Builder.Default
        private Range exportRateMinutes= ConfigDefaults.metricExportRate();
        @Builder.Default
        private Set<String> stats = new HashSet<>();
    }

    @Data
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Range {
        @Builder.Default
        private int value = 0;
        @Builder.Default
        private int min = 0;
        @Builder.Default
        private int max = 10000;
        @Builder.Default
        private ApplyMode applyMode = ApplyMode.MULTI;
        @Builder.Default
        private DisplayMode displayMode = DisplayMode.PERCENTAGE;

        public enum DisplayMode {
            NONE,
            ABSOLUTE,
            PERCENTAGE;
        }

        public enum ApplyMode {
            ADD,
            MULTI,
            PERCENT;
        }

        public Range clone() {
            return Range.builder()
                .displayMode(displayMode)
                .applyMode(applyMode)
                .max(max)
                .min(min)
                .value(value)
                .build();
        }

        public static Range fromSlider(Slider slider) {
            return MoreOptionsV3Config.Range.builder()
                .value(slider.getValue())
                .max(slider.getMax())
                .min(slider.getMin())
                .displayMode(UiMapper.toDisplayMode(slider.getValueDisplay()))
                .applyMode(UiMapper.toApplyMode(slider.getValueDisplay()))
                .build();
        }
    }

    @Data
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Meta {
        @Builder.Default
        private int version = VERSION;
        @Builder.Default
        private Level logLevel = Loggers.LOG_LEVEL_DEFAULT;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @EqualsAndHashCode
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class BoosterEntry {
        private String key;
        private Range add;
        private Range multi;
    }

    /* TODO
       * day night / cycle; lightning
       * toggle deposit overlay when building

     */

    @Data
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class BoostersPreset {
        private String name;
        @Builder.Default
        private Set<BoostersConfig.Booster> boosters = new HashSet<>();
    }

    @Data
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class BoostersConfig {

        @Builder.Default
        private Map<String, Set<Booster>> faction = new HashMap<>();

        @Builder.Default
        private Set<BoostersConfig.Booster> player = new HashSet<>();

        @Data
        @Builder
        @EqualsAndHashCode
        @NoArgsConstructor
        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Booster {
            private String key;
            private Range range;
        }
    }

    @Data
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RacesConfig {
        @Builder.Default
        private Set<Liking> likings = new HashSet<>();

        @Data
        @Builder
        @EqualsAndHashCode
        @NoArgsConstructor
        @AllArgsConstructor(access = AccessLevel.PRIVATE)
        public static class Liking {
            private String race;
            private String otherRace;
            @Builder.Default
            private Range range = ConfigDefaults.raceLiking();
        }
    }

    /**
     * Only for debugging purposes
     */
    public String toJson() {
        Json json = new Json(JsonMapper.mapObject(this));
        return json.toString();
    }
}
