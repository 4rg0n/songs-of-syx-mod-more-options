package com.github.argon.sos.mod.sdk.game.api;

import com.github.argon.sos.mod.sdk.game.action.Resettable;
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
import snake2d.util.sets.KeyMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Access to the games sound effects
 */
@NoArgsConstructor
public class GameSoundsApi implements Resettable {

    private final Logger log = Loggers.getLogger(GameSoundsApi.class);

    @Nullable
    private Map<String, Ambiance> ambienceSounds;
    @Nullable
    private Map<String, SoundRace> raceSounds;
    @Nullable
    private Map<String, Sound> sounds;
    @Nullable
    private Map<String, Sound> animalSounds;
    @Nullable
    private Map<String, Sound> roomWorkSounds;

    public final static String KEY_PREFIX = "sounds";



    @Override
    public void reset() {
        ambienceSounds = null;
        raceSounds = null;
    }

    public Map<String, Ambiance> getAmbienceSounds() {
        if (ambienceSounds == null) {
            ambienceSounds = new HashMap<>();
            AUDIO.AMBI().all().forEach(ambiance -> {
                ambienceSounds.put(KEY_PREFIX + "." +  ambiance.key(), ambiance);
            });
        }

        return ambienceSounds;
    }

    public Map<String, SoundRace> getRaceSounds() {
        if (raceSounds == null) {
            raceSounds = new HashMap<>();
            AUDIO.races().RMAP().all().forEach(soundRace -> {
                raceSounds.put(KEY_PREFIX + "." + soundRace.key(), soundRace);
            });
        }

        return raceSounds;
    }

    public Map<String, Sound> getAnimalSounds() {
        if (animalSounds == null) {
            animalSounds = new HashMap<>();

            SETT.ANIMALS().species.all().forEach(animalSpecies -> {
                animalSounds.put(KEY_PREFIX + "." + animalSpecies.key(), animalSpecies.sounds);
            });
        }

        return animalSounds;
    }

    public Map<String, Sound> getRoomWorkSounds() {
        if (roomWorkSounds == null) {
            roomWorkSounds = new HashMap<>();
            SETT.ROOMS().imps().forEach(room -> {
                if (room.employment() == null) {
                    return;
                }

                roomWorkSounds.put(KEY_PREFIX + "." + room.key, room.employment().sound());
            });
        }

        return roomWorkSounds;
    }

    public Map<String, Sound> getSounds() {
        if (sounds == null) {
            sounds = new HashMap<>();
            sounds.put(KEY_PREFIX + ".BUILD", AUDIO.mono().BUILD);
            sounds.put(KEY_PREFIX + ".SLAUGHTER", AUDIO.mono().SLAUGHTER);
            sounds.put(KEY_PREFIX + ".SWORD", AUDIO.mono().SWORD);
            sounds.put(KEY_PREFIX + ".CLEAR_GRASS2", AUDIO.mono().clearGrass);

            //noinspection unchecked
            ReflectionUtil.getDeclaredFieldValue("map", AUDIO.mono())
                .map(object -> (KeyMap<Sound>) object)
                .ifPresent(soundMap -> {
                    soundMap.keys().forEach(key -> {
                        Sound sound = soundMap.get(key);
                        sounds.put(KEY_PREFIX + "." + key, sound);
                    });
                });
        }

        return sounds;
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
