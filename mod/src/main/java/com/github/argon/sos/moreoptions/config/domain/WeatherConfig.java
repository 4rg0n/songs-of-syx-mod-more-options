package com.github.argon.sos.moreoptions.config.domain;

import com.github.argon.sos.mod.sdk.data.domain.Range;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WeatherConfig {
    @Builder.Default
    private Map<String, Range> effects = new HashMap<>();
}
