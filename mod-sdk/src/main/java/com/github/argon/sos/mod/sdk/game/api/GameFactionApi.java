package com.github.argon.sos.mod.sdk.game.api;

import com.github.argon.sos.mod.sdk.game.action.Resettable;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.phase.Phases;
import com.github.argon.sos.mod.sdk.phase.UninitializedException;
import com.github.argon.sos.mod.sdk.util.Lists;
import game.faction.FACTIONS;
import game.faction.Faction;
import game.faction.npc.FactionNPC;
import game.faction.player.Player;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor
public class GameFactionApi implements Phases, Resettable {

    private final Logger log = Loggers.getLogger(GameFactionApi.class);

    @Getter
    private final Map<String, Faction> factions = new HashMap<>();
    @Getter
    private final Map<String, FactionNPC> factionNPCs = new HashMap<>();

    @Nullable
    private Player player;

    /**
     * @return the player faction
     */
    public Player getPlayer() {
        if (player == null) {
            throw new UninitializedException("No player faction available");
        }

        return player;
    }

    /**
     * @return whether a faction is the player faction
     */
    public boolean isPlayer(Faction faction) {
        return faction.equals(getPlayer());
    }

    @Nullable
    public Faction getByName(String name) {
        return getFactions().get(name);
    }

    @Override
    public void reset() {
        factions.clear();
        factionNPCs.clear();
        player = null;
    }

    @Override
    public void initSettlementUiPresent() {
        reloadFactions();
    }

    private void reloadFactions() {
        // add player faction
        player = FACTIONS.player();
        factions.put(player.name.toString(), player);

        // add npc factions
        Map<String, FactionNPC> factionNPCs = Lists.fromGameLIST(FACTIONS.NPCs()).stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(faction -> faction.name.toString(), faction -> faction));
        this.factions.putAll(factionNPCs);
        this.factionNPCs.putAll(factionNPCs);

        log.debug("Initialized %s npc factions", factionNPCs.size());
        log.debug("Player faction: %s", player.name.toString());
        log.trace("Factions: %s", factionNPCs.keySet());
    }
}
