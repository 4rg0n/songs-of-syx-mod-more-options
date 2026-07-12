package com.github.argon.sos.mod.sdk.booster;

import lombok.Builder;

/**
 * Container for the different kind of custom boosters
 *
 * @param add booster which adds onto the base value
 * @param multi booster which multiplies the base value
 */
@Builder
public record Boosters(FactionBooster add, FactionBooster multi) {
}
