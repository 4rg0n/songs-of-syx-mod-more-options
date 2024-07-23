package com.github.argon.sos.moreoptions.config.json.v3;

import com.github.argon.sos.moreoptions.config.domain.Range;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonSoundsV3Config {
    @Builder.Default
    private Map<String, Range> ambience = new HashMap<>();
    @Builder.Default
    private Map<String, Range> settlement = new HashMap<>();
    @Builder.Default
    private Map<String, Range> room = new HashMap<>();
}
