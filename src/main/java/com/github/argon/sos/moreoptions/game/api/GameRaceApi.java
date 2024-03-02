package com.github.argon.sos.moreoptions.game.api;


import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.race.RaceService;
import com.github.argon.sos.moreoptions.util.Lists;
import com.github.argon.sos.moreoptions.util.ReflectionUtil;
import init.race.RACES;
import init.race.Race;
import lombok.Getter;
import settlement.entity.humanoid.HCLASS;
import settlement.stats.STATS;
import settlement.stats.standing.STANDINGS;
import settlement.stats.standing.StandingCitizen;
import world.WORLD;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class GameRaceApi {
    private final static Logger log = Loggers.getLogger(GameRaceApi.class);
    private static List<RaceService.RaceLiking> vanillaLikings;

    private final Map<String, Integer> raceIndexMap = new HashMap<>();

    @Getter(lazy = true)
    private final static GameRaceApi instance = new GameRaceApi();

    private GameRaceApi() {
        for (Race race : getAll()) {
            raceIndexMap.put(race.key, race.index);
        }
    }

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

    /**
     * For storing the game's default likings between races in memory
     */
    public void initVanillaLikings() {
        if (vanillaLikings == null) {
            vanillaLikings = getAllLikings();
        }
    }

    public List<RaceService.RaceLiking> getVanillaLikings() {
        return vanillaLikings;
    }

    /**
     * @return flat list of likings of each race to another race
     */
    public List<RaceService.RaceLiking> getAllLikings() {
        List<RaceService.RaceLiking> likings = new ArrayList<>();

        for (Race race : getAll()) {
            for (Race otherRace : getAll()) {
                if (race.key.equals(otherRace.key)) {
                    continue;
                }

                double liking = getLiking(race, otherRace);
                likings.add(RaceService.RaceLiking.builder()
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
        return Lists.listFromLIST(RACES.all());
    }

    public int citizenCount(Race race) {
        HCLASS cl = HCLASS.CITIZEN;
        return STATS.POP().POP.data(cl).get(race, 0) + WORLD.ARMIES().cityDivs().total(race);
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
            .data(HCLASS.CITIZEN)
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
}
