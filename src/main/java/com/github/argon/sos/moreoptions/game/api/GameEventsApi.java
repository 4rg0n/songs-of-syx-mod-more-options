package com.github.argon.sos.moreoptions.game.api;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import game.GAME;
import game.events.EVENTS;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameEventsApi {

    private final static Logger log = Loggers.getLogger(GameEventsApi.class);

    @Getter(lazy = true)
    private final static GameEventsApi instance = new GameEventsApi();


    public Map<String, EVENTS.EventResource> getSettlementEvents() {
        Map<String, EVENTS.EventResource> events = new HashMap<>();
        EVENTS gameEvents = GAME.events();

        events.put("accident", gameEvents.accident);
        events.put("advice", gameEvents.advice);
        events.put("disease", gameEvents.disease);
        events.put("farm", gameEvents.farm);
        events.put("fish", gameEvents.fish);
        events.put("killer", gameEvents.killer);
        events.put("orchard", gameEvents.orchard);
        events.put("pasture", gameEvents.pasture);
        events.put("raceWars", gameEvents.raceWars);
        events.put("riot", gameEvents.riot);
        events.put("slaver", gameEvents.slaver);
        events.put("temperature", gameEvents.temperature);
        events.put("uprising", gameEvents.uprising);

        return events;
    }

    public Map<String, EVENTS.EventResource> getWorldEvents() {
        Map<String, EVENTS.EventResource> events = new HashMap<>();
        EVENTS gameEvents = GAME.events();

        events.put("world.factionExpand", gameEvents.world.factionExpand);
        events.put("world.factionBreak", gameEvents.world.factionBreak);
        events.put("world.popup", gameEvents.world.popup);
        events.put("world.war", gameEvents.world.war);
        events.put("world.warPlayer", gameEvents.world.warPlayer);
        events.put("world.raider", gameEvents.world.raider);
        events.put("world.rebellion", gameEvents.world.rebellion);
        events.put("world.plague", gameEvents.world.plague);

        return events;
    }
}
