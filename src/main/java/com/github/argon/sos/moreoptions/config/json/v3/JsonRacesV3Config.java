package com.github.argon.sos.moreoptions.config.json.v3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.argon.sos.moreoptions.config.domain.RacesConfig;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonRacesV3Config {
    @Builder.Default
    private Set<RacesConfig.Liking> likings = new HashSet<>();
}
