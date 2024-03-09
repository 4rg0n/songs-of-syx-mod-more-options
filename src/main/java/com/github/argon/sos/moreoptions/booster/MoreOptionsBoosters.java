package com.github.argon.sos.moreoptions.booster;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Container for the different kind of custom boosters
 */
@Getter
@Builder
@RequiredArgsConstructor
public class MoreOptionsBoosters {
    private final MoreOptionsBooster add;

    private final MoreOptionsBooster multi;
}
