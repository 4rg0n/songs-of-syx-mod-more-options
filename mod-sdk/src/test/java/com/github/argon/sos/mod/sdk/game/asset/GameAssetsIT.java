package com.github.argon.sos.mod.sdk.game.asset;

import com.github.argon.sos.mod.testing.GameExtension;
import com.github.argon.sos.mod.testing.TestMods;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

@TestMods({"Test Mod"})
@ExtendWith(GameExtension.class)
class GameAssetsIT {

    @Test
    void test() {
        List<String> races = GameAssets.getRaces();

        Assertions.assertThat(races).contains("TEST_HUMAN");
        Assertions.assertThat(races).contains("HUMAN");

        Assertions.assertThat(GameAssets.getAnimals()).isNotEmpty();
        Assertions.assertThat(GameAssets.getBoosters()).isNotEmpty();
        Assertions.assertThat(GameAssets.getDamages()).isNotEmpty();
        Assertions.assertThat(GameAssets.getClimates()).isNotEmpty();
        Assertions.assertThat(GameAssets.getDrinkables()).isNotEmpty();
        Assertions.assertThat(GameAssets.getEdibles()).isNotEmpty();
        Assertions.assertThat(GameAssets.getEnvironments()).isNotEmpty();
        Assertions.assertThat(GameAssets.getFences()).isNotEmpty();
        Assertions.assertThat(GameAssets.getFloors()).isNotEmpty();
        Assertions.assertThat(GameAssets.getGrowables()).isNotEmpty();
        Assertions.assertThat(GameAssets.getHumanClasses()).isNotEmpty();
        Assertions.assertThat(GameAssets.getMinables()).isNotEmpty();
        Assertions.assertThat(GameAssets.getMonuments()).isNotEmpty();
        Assertions.assertThat(GameAssets.getNeeds()).isNotEmpty();
        Assertions.assertThat(GameAssets.getRooms()).isNotEmpty();
        Assertions.assertThat(GameAssets.getStats()).isNotEmpty();
        Assertions.assertThat(GameAssets.getStructures()).isNotEmpty();
        Assertions.assertThat(GameAssets.getTerrains()).isNotEmpty();
        Assertions.assertThat(GameAssets.getResources()).isNotEmpty();
        Assertions.assertThat(GameAssets.getReligions()).isNotEmpty();
    }
}