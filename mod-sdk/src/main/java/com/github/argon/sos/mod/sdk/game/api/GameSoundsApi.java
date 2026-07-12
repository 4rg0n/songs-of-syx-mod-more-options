package com.github.argon.sos.mod.sdk.game.api;

import com.github.argon.sos.mod.sdk.game.action.Resettable;
import com.github.argon.sos.mod.sdk.game.asset.GameAssets;
import com.github.argon.sos.mod.sdk.game.asset.GameFolder;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.util.MathUtil;
import com.github.argon.sos.mod.sdk.util.ReflectionUtil;
import game.audio.AUDIO;
import game.audio.Ambiance;
import game.audio.Sound;
import game.audio.SoundRace;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;
import settlement.main.SETT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Access to the game sound effects
 */
@NoArgsConstructor
public class GameSoundsApi implements Resettable {

    private final Logger log = Loggers.getLogger(GameSoundsApi.class);

    /**
     * Used as prefix for sound keys to identify them as a sound.
     */
    public final static String KEY_PREFIX = "sounds";

    /**
     * Hardcoded files that contain sound.
     */
    private static final List<String> RACE_FILES = List.of("AA_Race");
    private static final List<String> SETTLEMENT_FILES = List.of("AA_Effects");

    @Nullable private Map<String, Ambiance> ambienceSounds;
    @Nullable private Map<String, SoundRace> raceSounds;
    @Nullable private Map<String, SoundRace> animalSounds;
    @Nullable private Map<String, SoundRace> roomWorkSounds;
    @Nullable private Map<String, SoundRace> settlementSounds;

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        ambienceSounds = null;
        raceSounds = null;
        animalSounds = null;
        roomWorkSounds = null;
        settlementSounds = null;
    }

    /**
     * Returns a map with sound keys and the corresponding {@link Ambiance} sounds.
     *
     * @return map of keys with ambience sounds
     */
    public Map<String, Ambiance> getAmbienceSounds() {
        if (ambienceSounds == null) {
            ambienceSounds = new HashMap<>();
            AUDIO.AMBI().all().forEach(ambiance ->
                ambienceSounds.put(KEY_PREFIX + "." + ambiance.key(), ambiance));
        }
        return ambienceSounds;
    }

    /**
     * Returns a map with sound keys and the corresponding {@link SoundRace} sounds.
     *
     * @return map of keys with race sounds
     */
    public Map<String, SoundRace> getRaceSounds() {
        if (raceSounds == null) raceSounds = loadCategory(RACE_FILES);
        return raceSounds;
    }

    /**
     * Returns a map with sound keys and the corresponding {@link SoundRace} animal sounds.
     *
     * @return map of keys with animal sounds
     */
    public Map<String, SoundRace> getAnimalSounds() {
        if (animalSounds == null) {
            animalSounds = new HashMap<>();
            SETT.ANIMALS().species.forEach(animalSpecies ->
                animalSounds.put(KEY_PREFIX + "." + animalSpecies.key(), animalSpecies.sound));
        }
        return animalSounds;
    }

    /**
     * Returns a map with sound keys and the corresponding {@link SoundRace} room work sounds.
     *
     * @return map of keys with room work sounds
     */
    public Map<String, SoundRace> getRoomWorkSounds() {
        if (roomWorkSounds == null) {
            roomWorkSounds = new HashMap<>();
            SETT.ROOMS().imps().forEach(room -> {
                if (room.employment() == null) return;
                roomWorkSounds.put(KEY_PREFIX + "." + room.key, room.employment().sound());
            });
        }
        return roomWorkSounds;
    }

    /**
     * Returns a map with sound keys and the corresponding {@link SoundRace} settlement sounds.
     *
     * @return map of keys with settlement sounds
     */
    public Map<String, SoundRace> getSettlementSounds() {
        if (settlementSounds == null) settlementSounds = loadCategory(SETTLEMENT_FILES);
        return settlementSounds;
    }

    /**
     * Set the volume of a given {@link Sound}.
     *
     * @param sound to set the volume
     * @param value of the volume
     */
    public void setSoundGain(Sound sound, int value) {
        sound.setGainLimit(MathUtil.toPercentage(value));
    }

    /**
     * Set the volume of a given {@link SoundRace}.
     *
     * @param sound to set the volume
     * @param value of the volume
     */
    public void setSoundGain(SoundRace sound, int value) {
        Sound[] sounds = (Sound[]) ReflectionUtil.getDeclaredFieldValue("all", sound)
            .orElse(null);

        if (sounds == null) {
            log.warn("No sounds in SoundRace %s to set. Bug?", sound.key());
            return;
        }

        for (Sound ssound : sounds) {
            setSoundGain(ssound, value);
        }
    }

    /**
     * Set the volume of a given {@link Ambiance} sound.
     *
     * @param sound to set the volume
     * @param value of the volume
     */
    public void setSoundGain(Ambiance sound, int value) {
        sound.setGainLimit(MathUtil.toPercentage(value));
    }

    /**
     * Loads all SoundRace keys declared in the given AA_*.txt files under audio/config/mono/.
     */
    private Map<String, SoundRace> loadCategory(List<String> fileTitles) {
        Map<String, SoundRace> map = new HashMap<>();
        GameFolder mono = GameAssets.audio().mono();

        for (String title : fileTitles) {
            mono.json(title).ifPresent(json -> {
                for (String key : json.keys()) {
                    SoundRace sr = AUDIO.race(key);
                    if (sr == null) {
                        log.warn("SoundRace not found in game for key %s (file %s)", key, title);
                    } else {
                        map.put(KEY_PREFIX + "." + key, sr);
                    }
                }
            });
        }

        return map;
    }
}
