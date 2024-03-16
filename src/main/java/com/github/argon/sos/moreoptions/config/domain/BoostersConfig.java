package com.github.argon.sos.moreoptions.config.domain;

import lombok.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BoostersConfig {

    @Builder.Default
    private Map<String, Set<Booster>> faction = new HashMap<>();

    @Builder.Default
    private Set<Booster> player = new HashSet<>();

    @Builder.Default
    private Map<String, BoostersPreset> boostersPresets = new HashMap<>();

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
        private Set<Booster> boosters = new HashSet<>();
    }

}
