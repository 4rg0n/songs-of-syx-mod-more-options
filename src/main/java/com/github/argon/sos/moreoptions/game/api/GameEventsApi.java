package com.github.argon.sos.moreoptions.game.api;

import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.MathUtil;
import com.github.argon.sos.moreoptions.util.ReflectionUtil;
import game.GAME;
import game.events.EVENTS;
import game.events.EventDisease;
import game.events.world.EventWorldRaider;
import init.disease.DISEASES;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import util.data.DOUBLE;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameEventsApi {

    private final static Logger log = Loggers.getLogger(GameEventsApi.class);

    private Map<String, EVENTS.EventResource> settlementEvents;
    private Map<String, EVENTS.EventResource> worldEvents;
    private Map<String, EVENTS.EventResource> eventsChance;

    @Getter(lazy = true)
    private final static GameEventsApi instance = new GameEventsApi();

    public Map<String, EVENTS.EventResource> getSettlementEvents() {
        if (settlementEvents == null) {
            settlementEvents = new HashMap<>();
            EVENTS gameEvents = GAME.events();

            settlementEvents.put("event.settlement.accident", gameEvents.accident);
            settlementEvents.put("event.settlement.advice", gameEvents.advice);
            settlementEvents.put("event.settlement.disease", gameEvents.disease);
            settlementEvents.put("event.settlement.farm", gameEvents.farm);
            settlementEvents.put("event.settlement.fish", gameEvents.fish);
            settlementEvents.put("event.settlement.killer", gameEvents.killer);
            settlementEvents.put("event.settlement.orchard", gameEvents.orchard);
            settlementEvents.put("event.settlement.pasture", gameEvents.pasture);
            settlementEvents.put("event.settlement.raceWars", gameEvents.raceWars);
            settlementEvents.put("event.settlement.riot", gameEvents.riot);
            settlementEvents.put("event.settlement.slaver", gameEvents.slaver);
            settlementEvents.put("event.settlement.temperature", gameEvents.temperature);
            settlementEvents.put("event.settlement.uprising", gameEvents.uprising);
        }

        return settlementEvents;
    }

    public Map<String, EVENTS.EventResource> getWorldEvents() {
        if (worldEvents == null) {
            worldEvents = new HashMap<>();
            EVENTS gameEvents = GAME.events();

            worldEvents.put("event.world.factionExpand", gameEvents.world.factionExpand);
            worldEvents.put("event.world.factionBreak", gameEvents.world.factionBreak);
            worldEvents.put("event.world.popup", gameEvents.world.popup);
            worldEvents.put("event.world.war", gameEvents.world.war);
            worldEvents.put("event.world.warPlayer", gameEvents.world.warPlayer);
            worldEvents.put("event.world.raider", gameEvents.world.raider);
            worldEvents.put("event.world.rebellion", gameEvents.world.rebellion);
            worldEvents.put("event.world.plague", gameEvents.world.plague);
        }

        return worldEvents;
    }

    public Map<String, EVENTS.EventResource> getEventsChance() {
        if (eventsChance == null) {
            eventsChance = new HashMap<>();
            EVENTS gameEvents = GAME.events();

            eventsChance.put("event.chance.raider", gameEvents.world.raider);
            eventsChance.put("event.chance.disease", gameEvents.disease);
        }

        return eventsChance;
    }

    public boolean setChance(EVENTS.EventResource event, int chance) {
        if (event instanceof EventDisease) {
            DISEASES.EPIDEMIC_CHANCE = MathUtil.toPercentage(chance);

            return true;
        } else if (event instanceof EventWorldRaider) {
            DOUBLE raiderChance = GAME.events().world.raider.CHANCE;
            double currentChance = raiderChance.getD();
            double newChance = currentChance * MathUtil.toPercentage(chance);

            try {
                ReflectionUtil.setField("cache", raiderChance, newChance);
                return true;
            } catch (Exception e) {
                log.warn("Could not set %s chance to %s", event.getClass().getSimpleName(), newChance, e);
                return false;
            }
        }

        return false;
    }

    public Map<String, Boolean> readEventsEnabledStatus() {
        Map<String, EVENTS.EventResource> events = getSettlementEvents();
        Map<String, EVENTS.EventResource> worldEvents = getWorldEvents();
        events.putAll(worldEvents);

        return events.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> isEnabled(entry.getValue())));
    }

    public void enableEvent(EVENTS.EventResource event, Boolean enabled) {
        ReflectionUtil.getDeclaredField("supress", EVENTS.EventResource.class).ifPresent(field -> {
            try {
                ReflectionUtil.setField(field, event, !enabled);
            } catch (Exception e) {
                log.warn("Could not set '%s.supress' to %s", event.getClass().getSimpleName(), !enabled, e);
            }
        });
    }

    /**
     * @return whether given event will execute or not
     */
    public Boolean isEnabled(EVENTS.EventResource event) {
        return ReflectionUtil.getDeclaredField("supress", EVENTS.EventResource.class).map(field -> {
            try {
                // checks whether event is suppressed
                return !(Boolean) ReflectionUtil.getDeclaredFieldValue(field, event)
                    .orElseThrow(() -> new RuntimeException("Got empty 'supress' from event class " + event.getClass().getName()));
            } catch (Exception e) {
                log.warn("Could not read '%s.supress' field", event.getClass(), e);
                return true;
            }
        }).orElse(true);
    }

    /**
     * Calls the {@link EVENTS.EventResource#clear()} for the given event.
     * This will reset any timers and progress in the event.
     */
    public boolean reset(EVENTS.EventResource event) {
        try {
            _reset(event);
        } catch (Exception e) {
            log.warn("Could not reset %s", event.getClass().getName(), e);
            return false;
        }

        return true;
    }

    private void _reset(EVENTS.EventResource event) {
        log.trace("Resetting event %s", event.getClass().getSimpleName());
        ReflectionUtil.invokeMethod("clear", event.getClass(), event);
    }
}
