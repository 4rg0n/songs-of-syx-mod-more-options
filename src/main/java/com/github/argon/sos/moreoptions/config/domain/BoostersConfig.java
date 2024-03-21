package com.github.argon.sos.moreoptions.config.domain;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BoostersConfig {

    @Builder.Default
    private Map<String, Map<String, Booster>> faction = new HashMap<>();

    @Builder.Default
    private Map<String, Booster> player = new HashMap<>();

    @Builder.Default
    private Map<String, BoostersPreset> presets = new HashMap<>();

    @Data
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Booster {
        private String key;
        private Range range;
    }

    @Data
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class BoostersPreset {
        @Builder.Default
        private String name = "";
        @Builder.Default
        private Map<String, Booster> boosters = new HashMap<>();
    }
}
