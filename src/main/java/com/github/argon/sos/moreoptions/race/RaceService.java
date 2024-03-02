package com.github.argon.sos.moreoptions.race;

import com.github.argon.sos.moreoptions.game.api.GameRaceApi;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import init.race.Race;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * For accessing and manipulating the games {@link Race}s.
 */
public class RaceService {
    private final static Logger log = Loggers.getLogger(RaceService.class);
    private final List<String> gameRaces;

    private final GameRaceApi gameRaceApi = GameRaceApi.getInstance();

    public RaceService(List<String> gameRaces) {
        this.gameRaces = gameRaces;
    }

    public List<RaceDetail> getAllRaceInfo() {
        return gameRaceApi.getAll().stream().map(race ->
            getRaceInfo(race.key).orElse(null)
        ).collect(Collectors.toList());
    }

    public Optional<RaceDetail> getRaceInfo(String name) {
        return gameRaceApi.getRace(name).map(race ->
            RaceDetail.builder().race(race).build()
        );
    }

    /**
     * Sets the liking between two races
     */
    public void setLiking(String raceName, String otherRaceName, double liking) {
        gameRaceApi.getRace(raceName)
            .ifPresent(race -> gameRaceApi.getRace(otherRaceName)
                .ifPresent(otherRace -> gameRaceApi.setLiking(race, otherRace, liking)));
    }

    /**
     * @return whether race is a vanilla or modded
     */
    public boolean isCustom(String name) {
        return !gameRaces.contains(name);
    }

    public List<Race> getGameRaces() {
        return gameRaceApi.getAll().stream()
                .filter(race -> gameRaces.contains(race.key))
                .collect(Collectors.toList());
    }

    /**
     * @return list of custom modded races
     */
    public List<Race> getCustomRaces() {
        return GameRaceApi.getInstance().getAll().stream()
                .filter(race -> !gameRaces.contains(race.key))
                .collect(Collectors.toList());
    }

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class RaceLiking {
        private final String race;
        private final String otherRace;

        private final double liking;
    }
}
