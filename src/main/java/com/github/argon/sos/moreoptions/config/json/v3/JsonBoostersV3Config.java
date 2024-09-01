package com.github.argon.sos.moreoptions.config.json.v3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.argon.sos.moreoptions.config.domain.BoostersConfig;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonBoostersV3Config {
    @Builder.Default
    private Map<String, Map<String, BoostersConfig.Booster>> faction = new HashMap<>();
}
