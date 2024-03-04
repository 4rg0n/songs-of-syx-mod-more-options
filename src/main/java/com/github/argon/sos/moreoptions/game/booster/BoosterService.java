package com.github.argon.sos.moreoptions.game.booster;

import com.github.argon.sos.moreoptions.config.MoreOptionsV2Config;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.BoosterUtil;
import com.github.argon.sos.moreoptions.util.MathUtil;
import game.boosting.BoostableCat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * For accessing the games Booster mechanics with custom {@link MoreOptionsBooster}s.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class BoosterService {

    private final static Logger log = Loggers.getLogger(BoosterService.class);

    @Getter(lazy = true)
    private final static BoosterService instance = new BoosterService();

    private Map<String, MoreOptionsBoosters> boosters = new HashMap<>();
    private Map<String, BoostableCat> boosterCategories = new HashMap<>();

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

    public void setBoosterValues(MoreOptionsV2Config config) {
       setBoosterValues(config.getBoosters());
    }

    public void setBoosterValues(Map<String, MoreOptionsV2Config.Range> ranges) {
        ranges.forEach((key, range) -> {
            getBoosters().ifPresent(boosters ->
                boosters.computeIfPresent(key, (keyAgain, moreOptionsBoosters) -> {
                log.trace("Apply booster config for: %s", key);

                switch (range.getApplyMode()) {
                    case MULTI:
                        log.trace("Booster %s: %s", range.getApplyMode(), range.getValue());
                        moreOptionsBoosters.getMulti().set(MathUtil.toPercentage(range.getValue()));
                        moreOptionsBoosters.getAdd().reset();
                        break;
                    case ADD:
                        log.trace("Booster %s: %s", range.getApplyMode(), range.getValue());
                        moreOptionsBoosters.getAdd().set(BoosterUtil.toBoosterValue(range.getValue()));
                        moreOptionsBoosters.getMulti().reset();
                        break;
                }

                return moreOptionsBoosters;
            }));
        });
    }

    public void reset(MoreOptionsV2Config config) {
        reset();
        setBoosterValues(config);
    }

    public void reset() {
        Map<String, MoreOptionsBoosters> boosters = MoreOptionsBoosterFactory.createDefault(this.boosters);

        this.boosters = boosters;
        this.boosterCategories = boosters.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> entry.getValue().getAdd().getOrigin().cat));
    }
}
