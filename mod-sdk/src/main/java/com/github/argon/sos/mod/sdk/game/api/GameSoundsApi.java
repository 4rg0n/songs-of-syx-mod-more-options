package com.github.argon.sos.mod.sdk.game.api;

import com.github.argon.sos.mod.sdk.game.action.Resettable;
import com.github.argon.sos.mod.sdk.game.asset.GameAssets;
import com.github.argon.sos.mod.sdk.game.asset.GameFolder;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.util.MathUtil;
import game.audio.AUDIO;
import game.audio.Ambiance;
import game.audio.SoundFile;
import game.audio.SoundRace;
import game.audio.SoundRaceAccess;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;
import settlement.main.SETT;

import java.util.HashMap;
import java.util.IdentityHashMap;
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
    private static final List<String> RACE_FILES = List.of("AA_HUMANOID");
    private static final List<String> SETTLEMENT_FILES = List.of("AA_BATTLE", "AA_SERVICE", "AA_WORK");

    /**
     * Sounds with this key prefix are available via {@link #getRoomWorkSounds()}.
     */
    private static final String ROOM_WORK_KEY_PREFIX = "ROOM_WORK";

    @Nullable private Map<String, Ambiance> ambienceSounds;
    @Nullable private Map<String, SoundRace> raceSounds;
    @Nullable private Map<String, SoundRace> animalSounds;
    @Nullable private Map<String, SoundRace> roomWorkSounds;
    @Nullable private Map<String, SoundRace> settlementSounds;

    /**
     * Remembers the gains the game gave to the files of a sound.
     */
    private final Map<SoundRace, SoundGain> soundGains = new IdentityHashMap<>();

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
        soundGains.clear();
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
     * Set the volume of a given {@link SoundRace}.
     * The volume is relative to the gain the game gave to each file of the sound.
     *
     * @param sound to set the volume
     * @param value of the volume
     */
    public void setSoundGain(SoundRace sound, int value) {
        soundGains
            .computeIfAbsent(sound, SoundGain::new)
            .apply(MathUtil.toPercentage(value));
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
                    // wildcards are no sounds themselves; room work sounds have their own category
                    if (key.endsWith("*") || key.startsWith(ROOM_WORK_KEY_PREFIX)) {
                        continue;
                    }

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

    /**
     * Holds the files of a {@link SoundRace} with the gains the game uses for them.
     */
    private static class SoundGain {
        private final List<SoundFile> files;
        private final float[] gameGains;
        private final float[] appliedGains;

        SoundGain(SoundRace sound) {
            files = SoundRaceAccess.isolate(sound);
            gameGains = new float[files.size()];
            appliedGains = new float[files.size()];

            for (int i = 0; i < files.size(); i++) {
                gameGains[i] = files.get(i).gain;
                appliedGains[i] = files.get(i).gain;
            }
        }

        void apply(double factor) {
            for (int i = 0; i < files.size(); i++) {
                SoundFile file = files.get(i);

                // the game changed the gain meanwhile, e.g. when it applies its audio settings
                if (file.gain != appliedGains[i]) {
                    gameGains[i] = file.gain;
                }

                file.gain = (float) (gameGains[i] * factor);
                appliedGains[i] = file.gain;
            }
        }
    }
}
