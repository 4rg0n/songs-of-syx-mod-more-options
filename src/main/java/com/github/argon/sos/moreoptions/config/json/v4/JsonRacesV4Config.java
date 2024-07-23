package com.github.argon.sos.moreoptions.config.json.v4;

import com.github.argon.sos.moreoptions.config.domain.RacesConfig;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonRacesV4Config {
    @Builder.Default
    private Set<RacesConfig.Liking> likings = new HashSet<>();
}
