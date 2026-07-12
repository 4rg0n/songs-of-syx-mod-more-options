package com.github.argon.sos.mod.sdk.game;

import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
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
 * Can extract data from different sorts of game statistics.
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
    private final static List<String> STAT_KEYS_BLACK_LIST = List.of(
        "HOME_FURNITURE"
    );

    /**
     * Used for summed stats for all races
     */
    private final static String TOTAL_SUFFIX = "_TOTAL";

    /**
     * For checking whether a game stat shall be collected.
     *
     * @param statName to check whether it is whitelisted
     * @return whether a stat is whitelisted
     */
    public boolean isWhitelisted(String statName) {
        // no whitelist = accept all
        if (whitelist.isEmpty()) {
            return true;
        }

        return whitelist.contains(statName);
    }

    /**
     * For checking whether a stat shall not be collected.
     *
     * @param statName to check whether it is blacklisted
     * @return whether stat is blacklisted
     */
    public boolean isBlacklisted(String statName) {
        return !isWhitelisted(statName);
    }

    /**
     * Extracts a map of religion stats from a list of {@link StatsReligion.StatReligion}.
     *
     * @param statList to extract from
     * @return map with stat name and the actual religion stat
     */
    public Map<String, Integer> extractReligionStats(LIST<StatsReligion.StatReligion> statList) {
        Map<String, Integer> stats = new HashMap<>();

        for (StatsReligion.StatReligion statReligion : statList) {
            stats.putAll(extractReligionStats(statReligion));
        }

        return stats;
    }

    /**
     * Extracts a map of race religion stats from a list of {@link StatsReligion.StatReligion}.
     *
     * @param statList to extract from
     * @return map with stat name and the actual race religion stat
     */
    public Map<String, Integer> extractReligionRaceStats(LIST<StatsReligion.StatReligion> statList) {
        Map<String, Integer> stats = new HashMap<>();

        for (StatsReligion.StatReligion statReligion : statList) {
            stats.putAll(extractReligionRaceStats(statReligion));
        }

        return stats;
    }

    /**
     * Extracts a map of religion race stat key with its value from a {@link StatsReligion.StatReligion}.
     *
     * @param stat to extract from
     * @return map with stat name and the value
     */
    private Map<String, Integer> extractReligionRaceStats(StatsReligion.StatReligion stat) {
        Map<String, Integer> stats = new HashMap<>();
        String key = stat.religion.key;

        stats.putAll(extractRaceStats(key, stat.followers));

        return stats;
    }

    /**
     * Extracts a map of religion stat key with its value from a {@link StatsReligion.StatReligion}.
     *
     * @param stat to extract from
     * @return map with stat key and the value
     */
    private Map<String, Integer> extractReligionStats(StatsReligion.StatReligion stat) {
        String key = stat.religion.key;

        return new HashMap<>(extractStat(key, stat.followers));
    }

    /**
     * Extracts a map of settlement stat key with its value from a {@link SETT_STATISTICS.SettStatistics}.
     *
     * @param keyPrefix to identify the stat
     * @param stat to extract the name and key from
     * @return map with stat key and the value
     */
    public Map<String, Integer> extractStat(String keyPrefix, SETT_STATISTICS.SettStatistics stat) {
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

    /**
     * Extracts a map of settlement stat key with its value from a {@link SETT_STATISTICS}.
     *
     * @param keyPrefix to identify the stat
     * @param stat to extract the name and key from
     * @return map with stat key and the value
     */
    public Map<String, Integer> extractStat(String keyPrefix, SETT_STATISTICS stat) {
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

    /**
     * Extracts a map of race stat key with its value from a {@link SETT_STATISTICS.SettStatistics}.
     *
     * @param keyPrefix to identify the stat
     * @param stat to extract the name and key from
     * @return map with stat key and the value
     */
    public Map<String, Integer> extractRaceStats(@Nullable String keyPrefix, SETT_STATISTICS.SettStatistics stat) {
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

    /**
     * Extracts a map of a stat with its value from a {@link STAT}.
     *
     * @param stat to extract the name and key from
     * @return map with stat key and the value
     */
    public Map<String, Integer> extractStat(STAT stat) {

        String statKey = stat.key();
        if (statKey == null) {
            statKey = stat.info().name.toString();
        }

        if (STAT_KEYS_BLACK_LIST.contains(statKey)) {
            return Map.of();
        }

        String key = statKey + ":" + TOTAL_SUFFIX;
        if (isBlacklisted(key)) {
            return Map.of();
        }

        try {
            return Map.of(key, stat.data().get(null));
        } catch (Exception e) {
            log.trace("Could not collect stat %s. Skipping", key, e);

            return Map.of();
        }
    }

    /**
     * Extracts a map of a race stat with its value from a {@link STAT}.
     *
     * @param stat to extract the name and key from
     * @return map with stat key and the value
     */
    public Map<String, Integer> extractRaceStats(STAT stat) {
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
