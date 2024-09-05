package com.github.argon.sos.moreoptions.config.domain;

import com.github.argon.sos.moreoptions.config.ConfigDefaults;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RacesConfig {
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
