package com.github.argon.sos.mod.sdk.game.api;

import com.github.argon.sos.mod.sdk.util.Lists;
import lombok.RequiredArgsConstructor;
import settlement.entity.animal.AnimalSpecies;
import settlement.main.SETT;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class GameAnimalsApi {

    public List<AnimalSpecies> getAnimalSpecies() {
        return Lists.fromGameLIST(SETT.ANIMALS().species.all());
    }

    public Optional<AnimalSpecies> getBySound(String soundKey) {
        String roomKey = soundKey.replace(GameSoundsApi.KEY_PREFIX + ".", "");

        return getAnimalSpecies().stream()
            .filter(animalSpecies -> animalSpecies.key().equals(roomKey))
            .findFirst();
    }
}
