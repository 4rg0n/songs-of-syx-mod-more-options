package com.github.argon.sos.mod.sdk.booster;

import game.boosting.BSourceInfo;
import game.boosting.Boostable;
import game.boosting.BoosterImp;
import game.faction.npc.FactionNPC;
import game.faction.player.Player;
import game.faction.royalty.Royalty;
import init.type.HCLASS_RACE;
import lombok.Getter;
import game.battle.div.Div;
import settlement.stats.Induvidual;
import world.map.regions.Region;

/**
 * Base class for game boosters
 */
public abstract class AbstractBooster extends BoosterImp {

    /**
     * Used as default value for boosts having no value
     */
    protected final double noValue;

    @Getter
    private final Boostable origin;

    /**
     * Which mode the booster will use to calculate its values
     */
    protected final BoostMode boostMode;

    /**
     * Will be multiplied with the calculated value
     */
    protected final double scale;

    /**
     * Default constructor has to be called from child class
     *
     * @param origin other booster this one is attached to
     * @param bSourceInfo general information about this booster
     * @param min minimum value
     * @param max maximum value
     * @param boostMode which mode shall be used to calculate the value
     */
    public AbstractBooster(
        Boostable origin,
        BSourceInfo bSourceInfo,
        int min,
        int max,
        BoostMode boostMode
    ) {
        super(
            bSourceInfo,
            min,
            max,
            !boostMode.equals(BoostMode.ADD)
        );
        this.boostMode = boostMode;

        switch (boostMode) {
            case PERCENT:
                this.scale = 0.01D;
                this.noValue = 1D;
                break;
            case MULTI:
                this.noValue = 1D;
                this.scale = 1D;
                break;
            case ADD:
            default:
                this.noValue = 0.0D;
                this.scale = 1D;
                break;
        }

        this.origin = origin;
    }

    /**
     * Returns the boost value for a citizen
     *
     * @param induvidual to get the value for
     * @return boost value
     */
    @Override
    public double vGet(Induvidual induvidual) {
        return noValue;
    }

    /**
     * Returns the boost value for a region
     *
     * @param region to get the value for
     * @return boost value
     */
    @Override
    public double vGet(Region region) {
        return noValue;
    }

    /**
     * Returns the boost value for a leader / king
     *
     * @param royalty to get the value for
     * @return boost value
     */
    @Override
    public double vGet(Royalty royalty) {
        return noValue;
    }

    /**
     * Returns the boost value for a citizen class
     *
     * @param populationClass to get the value for
     * @return boost value
     */
    @Override
    public double vGet(HCLASS_RACE populationClass) {
        return noValue;
    }

    /**
     * Returns the boost value for an army division
     *
     * @param division to get the value for
     * @return boost value
     */
    @Override
    public double vGet(Div division) {
        return noValue;
    }

    /**
     * Returns the boost value for the player faction
     *
     * @param factionPlayer to get the value for
     * @return boost value
     */
    @Override
    public double vGet(Player factionPlayer) {
        return noValue;
    }

    /**
     * Returns the boost value for the npc faction
     *
     * @param factionNPC to get the value for
     * @return boost value
     */
    @Override
    public double vGet(FactionNPC factionNPC) {
        return noValue;
    }
}
