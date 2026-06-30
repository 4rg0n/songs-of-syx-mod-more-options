package com.github.argon.sos.mod.sdk.game.api;


import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.phase.Phases;
import com.github.argon.sos.mod.sdk.util.Lists;
import com.github.argon.sos.mod.sdk.util.ReflectionUtil;
import init.race.RACES;
import init.race.Race;
import init.type.HCLASS;
import init.type.HCLASSES;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import settlement.stats.STATS;
import settlement.stats.standing.STANDINGS;
import settlement.stats.standing.StandingCitizen;
import world.army.AD;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Access to the games races
 */
@RequiredArgsConstructor
public class GameRaceApi implements Phases {
    private final static Logger log = Loggers.getLogger(GameRaceApi.class);
    @Getter
    private List<RaceLiking> vanillaLikings;
    private final Map<String, Integer> raceIndexMap = new HashMap<>();

    /**
     * List of vanilla game races
     */
    private final List<String> vanillaRaces = List.of(
        "ARGONOSH",
        "CANTOR",
        "CRETONIAN",
        "DONDORIAN",
        "GARTHIMI",
        "HUMAN",
        "Q_AMEVIA",
        "TILAPI"
    );

    /**
     * {@inheritDoc}
     */
    @Override
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
     * Increases or decreases the happiness value of a given race.
     *
     * @param race to increase or decrease happiness
     * @param inc amount to increase or decrease (when negative)
     */
    public void increaseHappiness(Race race, double inc) {
        increaseStanding(STANDINGS.CITIZEN().happiness, race, inc);
    }

    /**
     * Increases or decreases the expectation value of a given race.
     *
     * @param race to increase or decrease expectation
     * @param inc amount to increase or decrease (when negative)
     */
    public void increaseExpectation(Race race, double inc) {
        increaseStanding(STANDINGS.CITIZEN().expectation, race, inc);
    }

    /**
     * Increases or decreases the fulfillment value of a given race.
     *
     * @param race to increase or decrease fulfillment
     * @param inc amount to increase or decrease (when negative)
     */
    public void increaseFulfillment(Race race, double inc) {
        increaseStanding(STANDINGS.CITIZEN().fullfillment, race, inc);
    }

    /**
     * Increases or decreases the loyalty value of a given race.
     *
     * @param race to increase or decrease loyalty
     * @param inc amount to increase or decrease (when negative)
     */
    public void increaseLoyalty(Race race, double inc) {
        increaseStanding(STANDINGS.CITIZEN().loyalty, race, inc);
    }

    /**
     * Standings influence the races Expectations, Fulfillment, Happiness and Loyalty.
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

    /**
     * Sets the liking between two races.
     *
     * @param raceKey of the first race
     * @param otherRaceKey of the race to set the liking for
     * @param liking the actual liking. 1.0 would be 100%
     */
    public void setLiking(String raceKey, String otherRaceKey, double liking) {
        getRace(raceKey)
            .ifPresent(race -> getRace(otherRaceKey)
                .ifPresent(otherRace -> setLiking(race, otherRace, liking)));
    }

    /**
     * Checks whether a given race is a vanilla race or a newly added modded one.
     *
     * @return whether race is a vanilla or modded
     */
    public boolean isCustom(String key) {
        return !vanillaRaces.contains(key);
    }

    public List<Race> getVanillaRaces() {
        return getAll().stream()
            .filter(race -> vanillaRaces.contains(race.key))
            .toList();
    }

    /**
     * Returns a list of all newly added races.
     *
     * @return list of custom modded races
     */
    public List<Race> getCustomRaces() {
        return getAll().stream()
            .filter(race -> !vanillaRaces.contains(race.key))
            .toList();
    }

    /**
     * Returns a list of all likings for all races.
     *
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

    /**
     * Returns the liking value between two races. 1.0 is 100%
     *
     * @param race to check the liking between the other race
     * @param otherRace to check the liking between the race
     * @return liking value (1.0 is 100%)
     */
    public double getLiking(Race race, Race otherRace) {
        return race.pref().race(otherRace);
    }

    /**
     * Injects liking into the games races
     *
     * @param race to set the liking for the other race
     * @param otherRace to set the liking for the race
     * @param liking between the two races (1.0 is 100%).
     */
    public void setLiking(Race race, Race otherRace, double liking) {
        try {
            ReflectionUtil.getDeclaredFieldValue("others", race.pref())
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

    /**
     * Returns the {@link Race} by the given race key.
     *
     * @param key of the race
     * @return the found race
     */
    public Optional<Race> getRace(String key) {
        Integer raceIdx = raceIndexMap.get(key);
        if (raceIdx == null) {
            return Optional.empty();
        }

        return Optional.of(getAll().get(raceIdx));
    }

    /**
     * Returns a list of all modded and vanilla races.
     *
     * @return all races in the game
     */
    public List<Race> getAll() {
        return Lists.fromGameLIST(RACES.all());
    }

    /**
     * Returns the amount of citizens of a given {@link Race}
     *
     * @param race to receive the citizen count
     * @return the count of citizens of the race
     */
    public int citizenCount(Race race) {
        HCLASS cl = HCLASSES.CITIZEN();
        return STATS.POP().POP.data(cl).get(race, 0) + AD.cityDivs().total(race);
    }

    /**
     * Return the average of happiness for all races in the settlement.
     *
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
     * Return the average of loyalty for all races in the settlement.
     *
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

    /**
     * Returns the happiness of a given {@link Race}.
     *
     * @param race to look for
     * @return happiness value (1.0 is 100%)
     */
    public double getHappiness(Race race) {
        StandingCitizen standings = STANDINGS.CITIZEN();
        return standings.happiness.getD(race);
    }

    /**
     * Returns the loyalty of a given {@link Race}.
     *
     * @param race to look for
     * @return loyalty value (1.0 is 100%)
     */
    public double getLoyalty(Race race) {
        StandingCitizen standings = STANDINGS.CITIZEN();
        return standings.loyalty.getD(race);
    }

    /**
     * Checks whether a given {@link Race} is present as citizen in your settlement.
     *
     * @param race to check
     * @return whether the race is a citizen or not
     */
    public boolean isCitizen(Race race) {
        return citizenCount(race) != 0;
    }

    /**
     * Return all races present in your settlement
     *
     * @return races in the settlement
     */
    public List<Race> getCitizenRaces() {
       return getAll().stream()
           .filter(this::isCitizen)
           .toList();
    }

    /**
     * Data container presenting the liking between two races
     */
    @Builder
    public record RaceLiking(String race, String otherRace, double liking) {}
}
