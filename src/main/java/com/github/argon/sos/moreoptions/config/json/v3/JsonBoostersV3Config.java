package com.github.argon.sos.moreoptions.config.json.v3;

import com.github.argon.sos.moreoptions.config.domain.*;
import lombok.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonBoostersV3Config {
    @Builder.Default
    private Map<String, Set<BoostersConfig.Booster>> faction = new HashMap<>();
}
