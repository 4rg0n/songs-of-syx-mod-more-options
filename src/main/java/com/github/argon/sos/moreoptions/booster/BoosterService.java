package com.github.argon.sos.moreoptions.booster;

import com.github.argon.sos.moreoptions.config.MoreOptionsV3Config;
import com.github.argon.sos.moreoptions.game.api.GameFactionApi;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import game.boosting.BoostableCat;
import game.faction.Faction;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * For accessing the games Booster mechanics with custom {@link FactionBooster}s.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class BoosterService {

    private final static Logger log = Loggers.getLogger(BoosterService.class);

    @Getter(lazy = true)
    private final static BoosterService instance = new BoosterService(GameFactionApi.getInstance());

    private final GameFactionApi factionApi;

    private Map<String, Boosters> boosters = new HashMap<>();
    private Map<String, BoostableCat> boosterCategories = new HashMap<>();

    public Optional<Boosters> get(String key) {
        return getBoosters().map(boosters -> boosters.get(key));
    }
    public Optional<BoostableCat> getCat(String key) {
        return getBoosterCategories().map(boosterCategories -> boosterCategories.get(key));
    }

    public Optional<Map<String, Boosters>> getBoosters() {
        return Optional.ofNullable(boosters);
    }

    public Optional<Map<String, BoostableCat>> getBoosterCategories() {
        return Optional.ofNullable(boosterCategories);
    }

    public void setBoosterValues(MoreOptionsV3Config config) {
       setBoosterValues(config.getBoosters());
    }

    public void setBoosterValues(MoreOptionsV3Config.BoostersConfig config) {
        config.getFaction().forEach((factionName, boostersConfig) -> {
            boostersConfig.forEach(boosterConfig -> {
                getBoosters().ifPresent(gameBoosters -> {
                    String key = boosterConfig.getKey();
                    MoreOptionsV3Config.Range range = boosterConfig.getRange();
                    Faction faction = factionApi.getByName(factionName);

                    if (faction == null) {
                        return; // skip for factions not present in game
                    }

                    gameBoosters.computeIfPresent(key, (keyAgain, gameBooster) -> {
                        log.trace("Apply booster config for: %s", key);

                        switch (range.getApplyMode()) {
                            case PERCENT:
                                log.trace("Booster %s: %s", range.getApplyMode(), range.getValue());
                                gameBooster.getMulti().set(faction, range.getValue());
                                gameBooster.getAdd().reset();
                                break;
                            case ADD:
                                log.trace("Booster %s: %s", range.getApplyMode(), range.getValue());
                                gameBooster.getAdd().set(faction, range.getValue());
                                gameBooster.getMulti().reset();
                                break;
                        }

                        return gameBooster;
                    });
                });
            });



        });
    }

    public void reset(MoreOptionsV3Config config) {
        reset();
        setBoosterValues(config);
    }

    public void reset() {
        Map<String, Boosters> boosters = BoosterFactory.createDefault(this.boosters);

        this.boosters = boosters;
        this.boosterCategories = boosters.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> entry.getValue().getAdd().getOrigin().cat));
    }
}
