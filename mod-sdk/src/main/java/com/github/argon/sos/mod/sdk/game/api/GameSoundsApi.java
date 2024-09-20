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

    public void setSoundGain(SoundRace raceSound, int value) {
        Sound[] sounds = ReflectionUtil.getDeclaredFieldValue("all", raceSound)
            .map(object -> (Sound[]) object)
            .orElse(null);

        if (sounds == null) {
            log.warn("No sounds in SoundRace %s to set. Bug?", raceSound.key());
            return;
        }

        for (Sound sound : sounds) {
            sound.setGainLimit(MathUtil.toPercentage(value));
        }
    }

    public void setSoundGain(Ambiance ambienceSound, int value) {
        ambienceSound.setGainLimit(MathUtil.toPercentage(value));
    }
}
