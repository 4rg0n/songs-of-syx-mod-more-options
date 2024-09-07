package com.github.argon.sos.mod.sdk.game;

import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.util.Lists;
import com.github.argon.sos.mod.sdk.util.Maps;
import init.race.RACES;
import init.race.Race;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import settlement.stats.colls.StatsReligion;
import settlement.stats.stat.SETT_STATISTICS;
import settlement.stats.stat.STAT;
import snake2d.util.sets.LIST;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * For collecting game stats.
 */
@RequiredArgsConstructor
public class StatsExtractor {
    private final static Logger log = Loggers.getLogger(StatsExtractor.class);

    /**
     * Reads only stats from given whitelist. An empty list means read all stats.
     */
    private final Set<String> whitelist;

    /**
     * Some stats seem broken for some reason
     */
    private final static List<String> STAT_KEYS_BLACK_LIST = Lists.of(
        "HOME_FURNITURE"
    );

    /**
     * Used for summed stats for all races
     */
    private final static String TOTAL_SUFFIX = "_TOTAL";

    /**
     * For checking whether a game stat shall be collected or not
     */
    public boolean isWhitelisted(String statName) {
        // no whitelist = accept all
        if (whitelist.isEmpty()) {
            return true;
        }

        return whitelist.contains(statName);
    }

    public boolean isBlacklisted(String statName) {
        return !isWhitelisted(statName);
    }

    public Map<String, Integer> getReligionStats(LIST<StatsReligion.StatReligion> statList) {
        Map<String, Integer> stats = new HashMap<>();

        for (StatsReligion.StatReligion statReligion : statList) {
            stats.putAll(getReligionStats(statReligion));
        }

        return stats;
    }

    public Map<String, Integer> getReligionRaceStats(LIST<StatsReligion.StatReligion> statList) {
        Map<String, Integer> stats = new HashMap<>();

        for (StatsReligion.StatReligion statReligion : statList) {
            stats.putAll(getReligionRaceStats(statReligion));
        }

        return stats;
    }

    private Map<String, Integer> getReligionRaceStats(StatsReligion.StatReligion stat) {
        Map<String, Integer> stats = new HashMap<>();
        String key = stat.religion.key;

        stats.putAll(getRaceStats(key, stat.followers));

        return stats;
    }

    private Map<String, Integer> getReligionStats(StatsReligion.StatReligion stat) {
        Map<String, Integer> stats = new HashMap<>();
        String key = stat.religion.key;

        stats.putAll(getStat(key, stat.followers));

        return stats;
    }

    public Map<String, Integer> getStat(String keyPrefix, SETT_STATISTICS.SettStatistics stat) {
        Map<String, Integer> stats = new HashMap<>();
        String key = keyPrefix + ":" + stat.info().name + ":" + TOTAL_SUFFIX;

        if (isBlacklisted(key)) {
            return stats;
        }

        try {
            stats.put(key, stat.data().get(null));
        } catch (Exception e) {
            log.trace("Could not collect settlement stat %s. Skipping", key, e);
        }

        return stats;
    }

    public Map<String, Integer> getRaceStats(@Nullable String keyPrefix, SETT_STATISTICS.SettStatistics stat) {
        Map<String, Integer> stats = new HashMap<>();

        LIST<Race> races = RACES.all();
        int size = races.size();
        for (int i = 0; i < size; i++) {
            Race race = races.get(i);

            String key;
            if (keyPrefix != null) {
                key = keyPrefix + ":" + stat.info().name + ":" + race.key;
            } else {
                key = stat.info().name + ":" + race.key;
            }

            if (isBlacklisted(key)) {
                continue;
            }

            try {
                int value = stat.data().get(race);
                stats.put(key, value);
            } catch (Exception e) {
                log.trace("Could not collect settlement stat %s. Skipping", key, e);
            }
        }

        return stats;
    }

    public Map<String, Integer> getStat(STAT stat) {

        String statKey = stat.key();
        if (statKey == null) {
            statKey = stat.info().name.toString();
        }

        if (STAT_KEYS_BLACK_LIST.contains(statKey)) {
            return Maps.of();
        }

        String key = statKey + ":" + TOTAL_SUFFIX;
        if (isBlacklisted(key)) {
            return Maps.of();
        }

        try {
            return Maps.of(key, stat.data().get(null));
        } catch (Exception e) {
            log.trace("Could not collect stat %s. Skipping", key, e);

            return Maps.of();
        }
    }

    public Map<String, Integer> getRaceStats(STAT stat) {
        Map<String, Integer> stats = new HashMap<>();

        // filter not race stats
        if (!stat.info().indu()) {
            return stats;
        }

        String statKey = stat.key();
        if (statKey == null) {
            statKey = stat.info().name.toString();
        }

        if (STAT_KEYS_BLACK_LIST.contains(statKey)) {
            return stats;
        }

        LIST<Race> races = RACES.all();
        int size = races.size();
        for (int i = 0; i < size; i++) {
            Race race = races.get(i);

            String key = statKey + ":" + race.key;
            if (isBlacklisted(key)) {
                continue;
            }

            try {
                int value = stat.data().get(race);
                stats.put(key, value);
            } catch (Exception e) {
                log.trace("Could not collect stat %s. Skipping", key, e);
            }
        }

        return stats;
    }
}
