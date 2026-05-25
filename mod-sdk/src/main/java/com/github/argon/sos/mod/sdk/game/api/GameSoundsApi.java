package com.github.argon.sos.mod.sdk.game.api;

import com.github.argon.sos.mod.sdk.game.action.Resettable;
import com.github.argon.sos.mod.sdk.game.asset.GameAssets;
import com.github.argon.sos.mod.sdk.game.asset.GameFolder;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.util.Lists;
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

    public final static String KEY_PREFIX = "sounds";

    /*
     * Hardcoded files that contain sound.
     */
    private static final List<String> RACE_FILES = Lists.of("AA_Race");
    private static final List<String> SETTLEMENT_FILES = Lists.of("AA_Effects");

    @Nullable private Map<String, Ambiance> ambienceSounds;
    @Nullable private Map<String, SoundRace> raceSounds;
    @Nullable private Map<String, SoundRace> animalSounds;
    @Nullable private Map<String, SoundRace> roomWorkSounds;
    @Nullable private Map<String, SoundRace> settlementSounds;

    @Override
    public void reset() {
        ambienceSounds = null;
        raceSounds = null;
        animalSounds = null;
        roomWorkSounds = null;
        settlementSounds = null;
    }

    public Map<String, Ambiance> getAmbienceSounds() {
        if (ambienceSounds == null) {
            ambienceSounds = new HashMap<>();
            AUDIO.AMBI().all().forEach(ambiance ->
                ambienceSounds.put(KEY_PREFIX + "." + ambiance.key(), ambiance));
        }
        return ambienceSounds;
    }

    public Map<String, SoundRace> getRaceSounds() {
        if (raceSounds == null) raceSounds = loadCategory(RACE_FILES);
        return raceSounds;
    }

    public Map<String, SoundRace> getAnimalSounds() {
        if (animalSounds == null) {
            animalSounds = new HashMap<>();
            SETT.ANIMALS().species.forEach(animalSpecies ->
                animalSounds.put(KEY_PREFIX + "." + animalSpecies.key(), animalSpecies.sound));
        }
        return animalSounds;
    }

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

    public Map<String, SoundRace> getSounds() {
        if (settlementSounds == null) settlementSounds = loadCategory(SETTLEMENT_FILES);
        return settlementSounds;
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

    public void setSoundGain(Sound sound, int value) {
        sound.setGainLimit(MathUtil.toPercentage(value));
    }

    public void setSoundGain(SoundRace raceSound, int value) {
        Sound[] sounds = ReflectionUtil.getDeclaredFieldValue("all", raceSound)
            .map(object -> (Sound[]) object)
            .orElse(null);

        if (sounds == null) {
            log.warn("No sounds in SoundRace %s to set. Bug?", raceSound.key());
            return;
        }

        for (Sound sound : sounds) {
            setSoundGain(sound, value);
        }
    }

    public void setSoundGain(Ambiance ambienceSound, int value) {
        ambienceSound.setGainLimit(MathUtil.toPercentage(value));
    }
}
