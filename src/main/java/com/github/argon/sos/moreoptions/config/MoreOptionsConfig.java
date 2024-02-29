package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.MoreOptionsScript;
import com.github.argon.sos.moreoptions.log.Level;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.nio.file.Path;
import java.util.HashMap;
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
    private Map<String, Boolean> eventsSettlement = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> eventsWorld = new HashMap<>();

    @Builder.Default
    private Map<String, Range> eventsChance = new HashMap<>();

    @Builder.Default
    private Map<String, Range> soundsAmbience = new HashMap<>();

    @Builder.Default
    private Map<String, Range> soundsSettlement = new HashMap<>();
    @Builder.Default
    private Map<String, Range> soundsRoom = new HashMap<>();
    @Builder.Default
    private Map<String, Range> weather = new HashMap<>();
    @Builder.Default
    private Map<String, Range> boosters = new HashMap<>();

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
            PERCENTAGE
        }

        public enum ApplyMode {
            ADD,
            MULTI
        }

        public static Range defaultBoosterAdd() {
            return Range.builder()
                .value(0)
                .min(0)
                .max(10000)
                .applyMode(MoreOptionsConfig.Range.ApplyMode.ADD)
                .displayMode(MoreOptionsConfig.Range.DisplayMode.ABSOLUTE)
                .build();
        }

        public static Range defaultBoosterMulti() {
            return Range.builder()
                .value(100)
                .min(1)
                .max(10000)
                .applyMode(ApplyMode.MULTI)
                .displayMode(DisplayMode.PERCENTAGE)
                .build();
        }

        public Range clone() {
            return Range.builder()
                .displayMode(displayMode)
                .max(max)
                .min(min)
                .value(value)
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
