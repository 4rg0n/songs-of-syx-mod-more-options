package com.github.argon.sos.moreoptions.game.api;


import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.phase.Phases;
import com.github.argon.sos.moreoptions.util.Lists;
import com.github.argon.sos.moreoptions.util.ReflectionUtil;
import init.race.RACES;
import init.race.Race;
import init.type.HCLASS;
import init.type.HCLASSES;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import settlement.stats.STATS;
import settlement.stats.standing.STANDINGS;
import settlement.stats.standing.StandingCitizen;
import world.army.AD;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Access to the games races
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GameRaceApi implements Phases {
    private final static Logger log = Loggers.getLogger(GameRaceApi.class);

    @Getter(lazy = true)
    private final static GameRaceApi instance = new GameRaceApi();
    private List<RaceLiking> vanillaLikings;
    private final Map<String, Integer> raceIndexMap = new HashMap<>();

    /**
     * List of vanilla game races
     */
    // todo is there a better way to find these?
    private final List<String> vanillaRaces = Lists.of(
        "ARGONOSH",
        "CANTOR",
        "CRETONIAN",
        "DONDORIAN",
        "GARTHIMI",
        "HUMAN",
        "Q_AMEVIA",
        "TILAPI"
    );

    public void increaseHappiness(Race race, double inc) {
        increaseStanding(STANDINGS.CITIZEN().happiness, race, inc);
    }

    public void increaseExpectation(Race race, double inc) {
        increaseStanding(STANDINGS.CITIZEN().expectation, race, inc);
    }

    public void increaseFulfillment(Race race, double inc) {
        increaseStanding(STANDINGS.CITIZEN().fullfillment, race, inc);
    }

    public void increaseLoyalty(Race race, double inc) {
        increaseStanding(STANDINGS.CITIZEN().loyalty, race, inc);
    }

    /**
     * Standings influence the races Expectations, Fulfillment, Happiness and Loyalty
     *
     * @param standing which of the 4 standings to influence
     * @param race which race
     * @param inc amount to increase
     */
    private void increaseStanding(StandingCitizen.CitizenThing standing, Race race, double inc) {
        double current = standing.getD(race);
        double newStanding = current + inc;

        if (newStanding > 1) {
            newStanding = 1d;
        } else if (newStanding < 0) {
            newStanding = 0d;
        }

        try {
            Method declaredMethod = StandingCitizen.CitizenThing.class
                    .getDeclaredMethod("set", Race.class, double.class);
            ReflectionUtil.invokeMethod(declaredMethod, standing, race, newStanding);
            log.trace("Increased %s of %s by %s to now %s",
                    standing.info().name, race.key, inc, newStanding);
        } catch (Exception e) {
            log.warn("Could not increase %s for %s", standing.info().name, race.key, e);
        }
    }

    public void initModCreateInstance() {
        // initialize all game races
        for (Race race : getAll()) {
            raceIndexMap.put(race.key, race.index);
        }

        // initialize vanilla race likings
        if (vanillaLikings == null) {
            vanillaLikings = getAllLikings();
        }

        log.debug("Initialized %s races", raceIndexMap.size());
    }

    /**
     * Sets the liking between two races
     */
    public void setLiking(String raceKey, String otherRaceKey, double liking) {
        getRace(raceKey)
            .ifPresent(race -> getRace(otherRaceKey)
                .ifPresent(otherRace -> setLiking(race, otherRace, liking)));
    }

    /**
     * @return whether race is a vanilla or modded
     */
    public boolean isCustom(String key) {
        return !vanillaRaces.contains(key);
    }

    public List<Race> getVanillaRaces() {
        return getAll().stream()
            .filter(race -> vanillaRaces.contains(race.key))
            .collect(Collectors.toList());
    }

    /**
     * @return list of custom modded races
     */
    public List<Race> getCustomRaces() {
        return GameRaceApi.getInstance().getAll().stream()
            .filter(race -> !vanillaRaces.contains(race.key))
            .collect(Collectors.toList());
    }

    public List<RaceLiking> getVanillaLikings() {
        return vanillaLikings;
    }

    /**
     * @return flat list of likings of each race to another race
     */
    public List<RaceLiking> getAllLikings() {
        List<RaceLiking> likings = new ArrayList<>();

        for (Race race : getAll()) {
            for (Race otherRace : getAll()) {
                if (race.key.equals(otherRace.key)) {
                    continue;
                }

                double liking = getLiking(race, otherRace);
                likings.add(RaceLiking.builder()
                        .race(race.key)
                        .otherRace(otherRace.key)
                        .liking(liking)
                        .build());
            }
        }

        return likings;
    }

    public double getLiking(Race race, Race otherRace) {
        return race.pref().race(otherRace);
    }

    /**
     * Injects liking into the games races
     */
    public void setLiking(Race race, Race otherRace, double liking) {
        try {
            ReflectionUtil.getDeclaredField("others", race.pref())
                .map(o -> (double[]) o)
                .ifPresent(racePrefs -> {
                    Integer otherRaceIdx = raceIndexMap.get(otherRace.key);
                    racePrefs[otherRaceIdx] = liking;
                    log.trace("Set %s liking %s to %s", race.key, otherRace.key, liking);
                });
        } catch (Exception e) {
            log.error("Could not set %s liking %s to %s", race.key, otherRace.key, liking, e);
        }
    }

    public Optional<Race> getRace(String name) {
        Integer raceIdx = raceIndexMap.get(name);
        if (raceIdx == null) {
            return Optional.empty();
        }

        return Optional.of(getAll().get(raceIdx));
    }

    public List<Race> getAll() {
        return Lists.fromGameLIST(RACES.all());
    }

    public int citizenCount(Race race) {
        HCLASS cl = HCLASSES.CITIZEN();
        return STATS.POP().POP.data(cl).get(race, 0) + AD.cityDivs().total(race);
    }

    /**
     * @return average happiness of races in the current settlement
     */
    public double getAvgHappiness() {
        List<Race> races = getCitizenRaces();
        double allHappiness = races.stream()
            .map(this::getHappiness)
            .mapToDouble(Double::doubleValue)
            .sum();

        return allHappiness / races.size();
    }

    /**
     * @return average loyalty of races in the current settlement
     */
    public double getAvgLoyalty() {
        List<Race> races = getCitizenRaces();
        double allLoyalty = races.stream()
            .map(this::getLoyalty)
            .mapToDouble(Double::doubleValue)
            .sum();

        return allLoyalty / races.size();
    }

    public double getHappiness(Race race) {
        StandingCitizen standings = STANDINGS.CITIZEN();
        return standings.happiness.getD(race);
    }

    public double getLoyalty(Race race) {
        StandingCitizen standings = STANDINGS.CITIZEN();
        return standings.loyalty.getD(race);
    }

    public boolean isCitizen(Race race) {
        return countCitizen(race) != 0;
    }

    public int countCitizen(Race race) {
        return STATS.POP().POP
            .data(HCLASSES.CITIZEN())
            .get(race, 0);
    }

    /**
     * @return races in the settlement
     */
    public List<Race> getCitizenRaces() {
       return getAll().stream()
           .filter(this::isCitizen)
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
