package com.github.argon.sos.moreoptions.game.api;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import init.sound.SOUND;
import init.sound.SoundAmbience;
import init.sound.SoundSettlement;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import snake2d.util.sets.LIST;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameSoundsApi {

    private final static Logger log = Loggers.getLogger(GameSoundsApi.class);

    @Getter(lazy = true)
    private final static GameSoundsApi instance = new GameSoundsApi();

    public  Map<String, SoundSettlement.Sound> getSettlementSounds() {
        LIST<SoundSettlement.Sound> gameSounds = SOUND.sett().action.all();
        Map<String, SoundSettlement.Sound> sounds = new HashMap<>();

        gameSounds.forEach(sound -> {
            sounds.put(sound.key, sound);
        });

        return sounds;
    }

    public Map<String, SoundAmbience.Ambience> getAmbienceSounds() {
        SoundAmbience soundAmbience = SOUND.ambience();
        Map<String, SoundAmbience.Ambience> sounds = new HashMap<>();

        sounds.put("nature", soundAmbience.nature);
        sounds.put("night", soundAmbience.night);
        sounds.put("rain", soundAmbience.rain);
        sounds.put("wind", soundAmbience.wind);
        sounds.put("thunder", soundAmbience.thunder);
        sounds.put("water", soundAmbience.water);
        sounds.put("windhowl", soundAmbience.windhowl);
        sounds.put("windTrees", soundAmbience.windTrees);

        return sounds;
    }
}
