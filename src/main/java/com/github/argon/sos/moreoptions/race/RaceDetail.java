package com.github.argon.sos.moreoptions.race;

import init.race.Race;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Just a container for future additions.
 * No real usage yet.
 */
@ToString
@Getter
@Builder
@RequiredArgsConstructor
public class RaceDetail {
    private final Race race;
}
