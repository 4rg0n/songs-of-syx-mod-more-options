package com.github.argon.sos.moreoptions.config;

import com.github.argon.sos.moreoptions.log.Level;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@EqualsAndHashCode
public class MoreOptionsConfig {

    public final static int VERSION = 1;

    /**
     * Name of the config file
     */
    public final static String FILE_NAME = "MoreOptions";

    @Builder.Default
    private int version = VERSION;

    @Builder.Default
    private Level logLevel = Level.WARN;
    @Builder.Default
    private Map<String, Boolean> eventsSettlement = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> eventsWorld = new HashMap<>();

    @Builder.Default
    private Map<String, Integer> eventsChance = new HashMap<>();

    @Builder.Default
    private Map<String, Integer> soundsAmbience = new HashMap<>();

    @Builder.Default
    private Map<String, Integer> soundsSettlement = new HashMap<>();
    @Builder.Default
    private Map<String, Integer> soundsRoom = new HashMap<>();
    @Builder.Default
    private Map<String, Integer> weather = new HashMap<>();
    @Builder.Default
    private Map<String, Integer> boosters = new HashMap<>();

    /* TODO
       * day night / cycle; lightning
       * toggle deposit overlay when building

     */
}
