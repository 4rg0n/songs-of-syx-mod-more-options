package com.github.argon.sos.moreoptions.config.domain;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EventsConfig {
    @Builder.Default
    private Map<String, Boolean> settlement = new HashMap<>();
    @Builder.Default
    private Map<String, Boolean> world = new HashMap<>();
    @Builder.Default
    private Map<String, Range> chance = new HashMap<>();
}
