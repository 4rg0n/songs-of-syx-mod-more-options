package com.github.argon.sos.moreoptions.game.api;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.MathUtil;
import init.sound.SOUND;
import init.sound.SoundAmbience;
import init.sound.SoundSettlement;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;
import snake2d.util.sets.LIST;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameSoundsApi {

    private final static Logger log = Loggers.getLogger(GameSoundsApi.class);

    @Getter(lazy = true)
    private final static GameSoundsApi instance = new GameSoundsApi();

    @Nullable
    private Map<String, SoundSettlement.Sound> settlementSounds;
    @Nullable
    private Map<String, SoundSettlement.Sound> roomSounds;
    @Nullable
    private Map<String, SoundAmbience.Ambience> ambienceSounds;

    public final static String KEY_PREFIX = "sounds";

    public void clearCached() {
        settlementSounds = null;
        roomSounds = null;
        ambienceSounds = null;
    }

    public  Map<String, SoundSettlement.Sound> getSettlementSounds() {
        if (settlementSounds == null) {
            LIST<SoundSettlement.Sound> gameSounds = SOUND.sett().action.all();
            settlementSounds = new HashMap<>();

            gameSounds.forEach(sound -> {
                if (sound.key.charAt(0) == '_') {
                    settlementSounds.put(KEY_PREFIX + ".settlement." + sound.key, sound);
                }
            });
        }

        return settlementSounds;
    }

    public  Map<String, SoundSettlement.Sound> getRoomSounds() {
        if (roomSounds == null) {
            LIST<SoundSettlement.Sound> gameSounds = SOUND.sett().action.all();
             roomSounds = new HashMap<>();

            gameSounds.forEach(sound -> {
                if (sound.key.charAt(0) != '_') {
                    roomSounds.put(KEY_PREFIX + ".room." + sound.key, sound);
                }
            });
        }


        return roomSounds;
    }

    public Map<String, SoundAmbience.Ambience> getAmbienceSounds() {
        if (ambienceSounds == null) {
            SoundAmbience soundAmbience = SOUND.ambience();
            ambienceSounds = new HashMap<>();

            ambienceSounds.put(KEY_PREFIX + ".ambience.nature", soundAmbience.nature);
            ambienceSounds.put(KEY_PREFIX + ".ambience.night", soundAmbience.night);
            ambienceSounds.put(KEY_PREFIX + ".ambience.rain", soundAmbience.rain);
            ambienceSounds.put(KEY_PREFIX + ".ambience.wind", soundAmbience.wind);
            ambienceSounds.put(KEY_PREFIX + ".ambience.thunder", soundAmbience.thunder);
            ambienceSounds.put(KEY_PREFIX + ".ambience.water", soundAmbience.water);
            ambienceSounds.put(KEY_PREFIX + ".ambience.windhowl", soundAmbience.windhowl);
            ambienceSounds.put(KEY_PREFIX + ".ambience.windTrees", soundAmbience.windTrees);
        }

        return ambienceSounds;
    }

    public void setSoundGainLimiter(SoundAmbience.Ambience sound, int limit) {
        double limitPerc = MathUtil.toPercentage(limit);

        log.trace("Applying %s%% volume to ambience sound: %s#%s",
            limit, sound.getClass().getSimpleName(), sound.hashCode());
        sound.setGainLimiter(limitPerc);
    }

    public void setSoundsGainLimiter(SoundSettlement.Sound sound, int limit) {
        double limitPerc = MathUtil.toPercentage(limit);

        log.trace("Applying %s%% volume to settlement sound: %s#%s",
            limit, sound.getClass().getSimpleName(), sound.hashCode());
        sound.setGainLimiter(limitPerc);
    }
}
