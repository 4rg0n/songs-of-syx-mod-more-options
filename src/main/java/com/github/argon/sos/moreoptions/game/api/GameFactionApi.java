package com.github.argon.sos.moreoptions.game.api;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.phase.Phase;
import com.github.argon.sos.moreoptions.phase.Phases;
import com.github.argon.sos.moreoptions.phase.UninitializedException;
import com.github.argon.sos.moreoptions.util.Lists;
import game.faction.FACTIONS;
import game.faction.Faction;
import game.faction.npc.FactionNPC;
import game.faction.player.Player;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameFactionApi implements Phases {

    private final Logger log = Loggers.getLogger(GameFactionApi.class);

    @Getter(lazy = true)
    private final static GameFactionApi instance = new GameFactionApi();
    @Getter
    private final Map<String, Faction> factions = new HashMap<>(FACTIONS.MAX);
    @Getter
    private final Map<String, FactionNPC> factionNPCs = new HashMap<>(FACTIONS.NPCS_MAX);

    @Nullable
    private Player player;

    public Player getPlayer() {
        if (player == null) {
            throw new UninitializedException(Phase.INIT_SETTLEMENT_UI_PRESENT);
        }

        return player;
    }

    public boolean isPlayer(Faction faction) {
        return faction.equals(getPlayer());
    }

    @Nullable
    public Faction getByName(String name) {
        return getFactions().get(name);
    }

    public void clearCached() {
        factions.clear();
        factionNPCs.clear();
        player = null;
    }

    @Override
    public void onViewSetup() {
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
