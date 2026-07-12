package com.github.argon.sos.mod.sdk.game.api;

import com.github.argon.sos.mod.sdk.util.Lists;
import lombok.RequiredArgsConstructor;
import settlement.entity.animal.AnimalSpecies;
import settlement.main.SETT;

import java.util.List;
import java.util.Optional;

/**
 * For interacting with the game animals
 */
@RequiredArgsConstructor
public class GameAnimalsApi {

    /**
     * Returns all available {@link AnimalSpecies} in the game
     *
     * @return list of all available animal species
     */
    public List<AnimalSpecies> getAnimalSpecies() {
        return Lists.fromGameLIST(SETT.ANIMALS().species);
    }

    /**
     * Will look for an {@link AnimalSpecies} by its sound key.
     *
     * @param soundKey to look for
     * @return found animal species
     */
    public Optional<AnimalSpecies> getBySound(String soundKey) {
        String roomKey = soundKey.replace(GameSoundsApi.KEY_PREFIX + ".", "");

        return getByRoom(roomKey);
    }

    /**
     * Will look for an {@link AnimalSpecies} by its room key.
     *
     * @param roomKey to look for
     * @return found animal species
     */
    public Optional<AnimalSpecies> getByRoom(String roomKey) {
        return getAnimalSpecies().stream()
            .filter(animalSpecies -> animalSpecies.key().equals(roomKey))
            .findFirst();
    }
}
