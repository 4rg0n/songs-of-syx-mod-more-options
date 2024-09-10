package com.github.argon.sos.moreoptions.config.domain;

import com.github.argon.sos.mod.sdk.data.domain.Range;
import com.github.argon.sos.moreoptions.config.ConfigDefaults;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EventsConfig {
    @Builder.Default
    private Map<String, Boolean> events = new HashMap<>();
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
