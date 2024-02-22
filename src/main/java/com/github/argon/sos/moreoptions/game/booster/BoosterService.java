package com.github.argon.sos.moreoptions.game.booster;

import com.github.argon.sos.moreoptions.config.MoreOptionsConfig;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.MathUtil;
import game.boosting.BoostableCat;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class BoosterService {

    private final static Logger log = Loggers.getLogger(BoosterService.class);

    @Getter(lazy = true)
    private final static BoosterService instance = new BoosterService();

    private Map<String, MoreOptionsBoosters> boosters;
    private Map<String, BoostableCat> boosterCategories;

    public Optional<MoreOptionsBoosters> get(String key) {
        return getBoosters().map(boosters -> boosters.get(key));
    }
    public Optional<BoostableCat> getCat(String key) {
        return getBoosterCategories().map(boosterCategories -> boosterCategories.get(key));
    }

    public Optional<Map<String, MoreOptionsBoosters>> getBoosters() {
        return Optional.ofNullable(boosters);
    }

    public Optional<Map<String, BoostableCat>> getBoosterCategories() {
        return Optional.ofNullable(boosterCategories);
    }

    public void setBoosterValues(MoreOptionsConfig config) {
       setBoosterValues(config.getBoosters());
    }

    public void setBoosterValues(Map<String, MoreOptionsConfig.Range> ranges) {
        ranges.forEach((key, range) -> {
            getBoosters().ifPresent(boosters ->
                boosters.computeIfPresent(key, (keyAgain, moreOptionsBoosters) -> {
                log.debug("Apply booster config for: %s", key);
                log.trace("Booster: %s", range.getValue());

                switch (range.getApplyMode()) {
                    case MULTI:
                        moreOptionsBoosters.getMulti().set(MathUtil.toPercentage(range.getValue()));
                        moreOptionsBoosters.getAdditive().reset();
                        break;
                    case ADD:
                        moreOptionsBoosters.getAdditive().set(MathUtil.toPercentage(range.getValue()));
                        moreOptionsBoosters.getMulti().reset();
                        break;
                }

                return moreOptionsBoosters;
            }));
        });
    }

    public void reset(MoreOptionsConfig config) {
        reset();
        setBoosterValues(config);
    }

    public void reset() {
        Map<String, MoreOptionsBoosters> boosters = MoreOptionsBoosterFactory.createDefault();

        this.boosters = boosters;
        this.boosterCategories = boosters.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> entry.getValue().getAdditive().getOrigin().cat));
    }
}
