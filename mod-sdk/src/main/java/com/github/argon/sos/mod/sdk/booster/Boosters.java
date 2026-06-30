package com.github.argon.sos.mod.sdk.booster;

import lombok.Builder;

/**
 * Container for the different kind of custom boosters
 */
@Builder
public record Boosters(FactionBooster add, FactionBooster multi) {
}
