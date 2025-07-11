package com.github.argon.sos.moreoptions.booster;

import com.github.argon.sos.mod.sdk.booster.Boosters;
import com.github.argon.sos.mod.sdk.booster.FactionBooster;
import com.github.argon.sos.mod.sdk.game.action.Resettable;
import com.github.argon.sos.mod.sdk.game.api.GameFactionApi;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.moreoptions.config.domain.BoostersConfig;
import com.github.argon.sos.moreoptions.config.domain.MoreOptionsV5Config;
import com.github.argon.sos.mod.sdk.data.domain.Range;
import game.boosting.BoostableCat;
import game.faction.FACTIONS;
import game.faction.Faction;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * For accessing the games Booster mechanics with custom {@link FactionBooster}s.
 */
@RequiredArgsConstructor
public class BoosterService implements Resettable {
    private final static Logger log = Loggers.getLogger(BoosterService.class);

    private final GameFactionApi factionApi;

    private Map<String, Boosters> boosters = new HashMap<>();
    private Map<String, BoostableCat> boosterCategories = new HashMap<>();

    public Optional<Boosters> get(String key) {
        return getBoosters()
            .map(boosters -> boosters.get(key));
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

    public void setBoosterValues(MoreOptionsV5Config config) {
       setBoosterValues(config.getBoosters());
    }

    public void setBoosterValues(BoostersConfig config) {
        config.getFaction().forEach((factionName, boostersConfig) -> {
            boostersConfig.forEach((boosterKey, boosterConfig) -> {
                Faction faction = factionApi.getByName(factionName);

                if (faction == null) {
                    log.info("Faction %s not found in-game", factionName);
                    return;
                }

                applyBooster(faction, boosterConfig);
            });
        });

        config.getPlayer().forEach((boosterKey, boosterConfig) -> {
            applyBooster(FACTIONS.player(), boosterConfig);
        });
    }

    private void applyBooster(Faction faction, BoostersConfig.Booster boosterConfig) {
        String boosterKey = boosterConfig.getKey();

        getBoosters().ifPresent(gameBoosters -> {
            Range range = boosterConfig.getRange();

            if (!gameBoosters.containsKey(boosterKey)) {
                log.info("Booster %s not present for faction %s", boosterKey, faction.name);
                return;
            }

            Boosters gameBooster = gameBoosters.get(boosterKey);
            log.trace("Apply [%s]%s %s: %s", faction.name, boosterKey, range.getApplyMode(), range.getValue());

            switch (range.getApplyMode()) {
                case PERCENT:
                    gameBooster.getMulti().set(faction, range.getValue());
                    gameBooster.getAdd().reset(faction);
                    break;
                case ADD:
                    gameBooster.getAdd().set(faction, range.getValue());
                    gameBooster.getMulti().reset(faction);
                    break;
            }
        });
    }

    public void reset(MoreOptionsV5Config config) {
        reset();
        setBoosterValues(config);
    }

    @Override
    public void reset() {
        Map<String, Boosters> boosters = BoosterFactory.createDefault(this.boosters);

        this.boosters = boosters;
        this.boosterCategories = boosters.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> entry.getValue().getAdd().getOrigin().cat));
    }
}
