package com.github.argon.sos.moreoptions.config.domain;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SoundsConfig {
    @Builder.Default
    private Map<String, Range> ambience = new HashMap<>();
    @Builder.Default
    private Map<String, Range> settlement = new HashMap<>();
    @Builder.Default
    private Map<String, Range> room = new HashMap<>();
}
