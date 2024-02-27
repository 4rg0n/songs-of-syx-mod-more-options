package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.MoreOptionsScript;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.log.Level;
import com.github.argon.sos.moreoptions.metric.MetricExporter;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@EqualsAndHashCode
public class MoreOptionsConfig {

    public final static int VERSION = 2;

    /**
     * Name of the config file
     */
    public final static String FILE_NAME = "MoreOptions";
    public final static String FILE_NAME_BACKUP = FILE_NAME + ".backup";

    @EqualsAndHashCode.Exclude
    private Path filePath;

    @Builder.Default
    private int version = VERSION;

    @Builder.Default
    private Level logLevel = Level.WARN;

    @Builder.Default
    private Sounds sounds = Sounds.builder().build();

    @Builder.Default
    private Events events = Events.builder().build();
    @Builder.Default
    private Map<String, Range> weather = new HashMap<>();
    @Builder.Default
    private Map<String, Range> boosters = new HashMap<>();
    @Builder.Default
    private Metrics metrics = Metrics.builder().build();

    @Data
    @Builder
    @EqualsAndHashCode
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
    @EqualsAndHashCode
    public static class Metrics {
        @Builder.Default
        private boolean enabled = false;

        @Builder.Default
        private Range collectionRateSeconds = Range.builder()
            .min(1)
            .value(15)
            .max(600)
            .applyMode(Range.ApplyMode.ADD)
            .displayMode(Range.DisplayMode.ABSOLUTE)
            .build();

        @Builder.Default
        private Range exportRateMinutes= Range.builder()
            .min(1)
            .value(30)
            .max(600)
            .applyMode(Range.ApplyMode.ADD)
            .displayMode(Range.DisplayMode.ABSOLUTE)
            .build();

        @Builder.Default
        private List<String> stats = new ArrayList<>();

        @Builder.Default
        private Path exportFolder = MetricExporter.EXPORT_FOLDER;
    }

    @Data
    @Builder
    @EqualsAndHashCode
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

            public static DisplayMode fromValueDisplay(Slider.ValueDisplay valueDisplay) {
                switch (valueDisplay) {
                    case PERCENTAGE:
                        return DisplayMode.PERCENTAGE;
                    case ABSOLUTE:
                        return DisplayMode.ABSOLUTE;
                    default:
                    case NONE:
                        return DisplayMode.NONE;
                }
            }
        }

        public enum ApplyMode {
            ADD,
            MULTI;

            public static ApplyMode fromValueDisplay(Slider.ValueDisplay valueDisplay) {
                switch (valueDisplay) {
                    case ABSOLUTE:
                        return ApplyMode.ADD;
                    default:
                    case NONE:
                        return ApplyMode.MULTI;
                }
            }
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
            return MoreOptionsConfig.Range.builder()
                .value(slider.getValue())
                .max(slider.getMax())
                .min(slider.getMin())
                .displayMode(MoreOptionsConfig.Range.DisplayMode
                    .fromValueDisplay(slider.getValueDisplay()))
                .applyMode(MoreOptionsConfig.Range.ApplyMode
                    .fromValueDisplay(slider.getValueDisplay()))
                .build();
        }
    }

    @Data
    @Builder
    @EqualsAndHashCode
    public static class Meta {
        @Builder.Default
        private int version = VERSION;

        @Builder.Default
        private Level logLevel = MoreOptionsScript.LOG_LEVEL_DEFAULT;
    }

    @Getter
    @Builder
    public static class BoosterEntry {
        private final String key;

        private final Range add;

        private final Range multi;
    }

    /* TODO
       * day night / cycle; lightning
       * toggle deposit overlay when building

     */
}
