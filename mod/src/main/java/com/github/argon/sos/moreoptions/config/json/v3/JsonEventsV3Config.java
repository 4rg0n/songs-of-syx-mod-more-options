package com.github.argon.sos.moreoptions.config.json.v3;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.argon.sos.moreoptions.config.ConfigDefaults;
import com.github.argon.sos.mod.sdk.data.domain.Range;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonEventsV3Config {
    @Builder.Default
    private Map<String, Boolean> settlement = new HashMap<>();
    @Builder.Default
    private Map<String, Boolean> world = new HashMap<>();
    @Builder.Default
    private Map<String, Range> chance = new HashMap<>();

    /**
     * Influences the amount of loot and slaves the player gets when winning a battle
     */
    @Builder.Default
    private Range playerBattleLoot = ConfigDefaults.battleLootPlayer();

    /**
     * Influences the amount of loot and slaves the enemy gets when losing a battle
     */
    @Builder.Default
    private Range enemyBattleLoot = ConfigDefaults.battleLootEnemy();
}
