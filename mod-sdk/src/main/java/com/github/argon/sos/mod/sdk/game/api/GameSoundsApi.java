package com.github.argon.sos.mod.sdk.game.api;

import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.util.MathUtil;
import game.audio.AUDIO;
import game.audio.Ambiance;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Access to the games sound effects
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameSoundsApi {

    private final static Logger log = Loggers.getLogger(GameSoundsApi.class);

    @Getter(lazy = true)
    private final static GameSoundsApi instance = new GameSoundsApi();

    @Nullable
    private Map<String, Ambiance> ambienceSounds;

    public final static String KEY_PREFIX = "sounds";

    public void clearCached() {
        ambienceSounds = null;
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

    public void setSoundGainLimiter(Ambiance ambienceSound, int value) {
        ambienceSound.gainSet(MathUtil.toPercentage(value));
    }
}
