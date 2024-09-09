package com.github.argon.sos.moreoptions.booster;

import com.github.argon.sos.mod.sdk.i18n.I18nTranslator;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.util.Lists;
import com.github.argon.sos.moreoptions.ModModule;
import com.github.argon.sos.moreoptions.MoreOptionsScript;
import com.github.argon.sos.moreoptions.config.ConfigDefaults;
import com.github.argon.sos.moreoptions.config.domain.Range;
import com.github.argon.sos.moreoptions.util.BoosterUtil;
import game.boosting.BOOSTING;
import game.boosting.BSourceInfo;
import game.boosting.BoostSpec;
import game.boosting.Boostable;
import init.sprite.SPRITES;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * For preparing and creating {@link FactionBooster}s.
 * Boosters influence game bonuses and mechanics.
 *
 * Custom boosters need to be added to a game {@link Boostable} to have an effect.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BoosterFactory {

    private final static Logger log = Loggers.getLogger(BoosterFactory.class);
    private final static I18nTranslator i18n = ModModule.i18n().get(BoosterFactory.class);

    public final static String KEY_PREFIX = "booster";

    private final static List<String> blacklist = Lists.of(
        /*
        Because MoreOptions boosters have a high max value, this results in a very high possible max world population.
        NPC factions will then have a lot more population compared to vanilla.
         */
        "WORLD_POPULATION_CAPACITY"
    );

    /**
     * Creates {@link Boosters} for each available booster in the game.
     * Uses default settings for preparing the boosters.
     *
     * @return map of all custom boosters with their key
     */
    public static Map<String, Boosters> createDefault(Map<String, Boosters> presentBoosters) {
        Map<String, Boosters> boosters = new HashMap<>();

        BOOSTING.ALL().forEach(booster -> {
            // filter broken boosters without names
            if (booster.name == null || booster.name.toString().isEmpty()) {
                return;
            }

            // filter boosters on blacklist
            if (blacklist.contains(booster.key)) {
                return;
            }

            boosters.put(
                key(booster.key),
                createMoreOptionsBoosters(booster, presentBoosters.get(key(booster.key))));
        });

        log.debug("Built %s default boosters", boosters.size());

        return boosters;
    }

    /**
     * Creates a {@link FactionBooster} and attaches it to the given vanilla game {@link Boostable}
     *
     * @param booster on which game booster the custom one shall be attached
     * @param range definition for the booster
     */
    public static FactionBooster createMoreOptionsBooster(Boostable booster, Range range) {
        String suffix = " " +  i18n.t("Boosters.percent.suffix");
        double scale = 1.0D;

        if (range.getApplyMode().equals(Range.ApplyMode.ADD)) {
            suffix = " " +  i18n.t("Boosters.add.suffix");
            scale = 0.01D;
        }

        FactionBooster factionBooster = BoosterMapper.fromRange(
            booster,
            new BSourceInfo(MoreOptionsScript.MOD_INFO.name + suffix, SPRITES.icons().m.cog),
            range,
            scale
        );

        BoostSpec boostSpec = new BoostSpec(factionBooster, booster, MoreOptionsScript.MOD_INFO.name);
        booster.addFactor(boostSpec);

        return factionBooster;
    }

    /**
     * Creates new {@link Boosters} or uses the present booster
     */
    private static Boosters createMoreOptionsBoosters(Boostable booster, @Nullable Boosters presentBooster) {
        FactionBooster boosterMulti;
        if (presentBooster != null && BoosterUtil.alreadyExtended(booster, true)) {
            log.trace("Reuse present multi booster: %s", presentBooster.getMulti().info.name);
            boosterMulti = presentBooster.getMulti();
        } else {
            boosterMulti = createMoreOptionsBooster(booster, ConfigDefaults.boosterPercent());
        }

        FactionBooster boosterAdd;
        if (presentBooster != null && BoosterUtil.alreadyExtended(booster, false)) {
            log.trace("Reuse present add booster: %s", presentBooster.getAdd().info.name);
            boosterAdd = presentBooster.getAdd();
        }  else {
            boosterAdd = createMoreOptionsBooster(booster, ConfigDefaults.boosterAdd());
        }

        return Boosters.builder()
            .add(boosterAdd)
            .multi(boosterMulti)
            .build();
    }

    private static String key(String key) {
        return KEY_PREFIX + "." + key;
    }
}
