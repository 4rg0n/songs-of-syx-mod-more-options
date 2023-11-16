package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.log.Level;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
    private Range factionOpinionAdd = Range.builder()
        .value(0)
        .min(-100)
        .max(100)
        .displayMode(Range.DisplayMode.ABSOLUTE)
        .build();

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
        private DisplayMode displayMode = DisplayMode.PERCENTAGE;

        public enum DisplayMode {
            NONE,
            ABSOLUTE,
            PERCENTAGE
        }
    }

    @Data
    @Builder
    @EqualsAndHashCode
    public static class Meta {
        @Builder.Default
        private int version = VERSION;

        @Builder.Default
        private Level logLevel = Level.WARN;
    }

    /* TODO
       * day night / cycle; lightning
       * toggle deposit overlay when building

     */
}
