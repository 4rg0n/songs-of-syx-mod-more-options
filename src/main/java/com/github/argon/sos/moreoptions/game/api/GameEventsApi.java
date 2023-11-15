package com.github.argon.sos.moreoptions.game.api;

import com.github.argon.sos.moreoptions.MoreOptionsScript;
import com.github.argon.sos.moreoptions.game.booster.FactionWarBooster;
import com.github.argon.sos.moreoptions.log.Logger;
import com.github.argon.sos.moreoptions.log.Loggers;
import com.github.argon.sos.moreoptions.util.MathUtil;
import com.github.argon.sos.moreoptions.util.ReflectionUtil;
import game.GAME;
import game.events.EVENTS;
import game.events.EventDisease;
import game.events.world.EventWorldRaider;
import game.faction.npc.ruler.opinion.ROpinions;
import init.disease.DISEASES;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class GameEventsApi {

    @Getter
    private final FactionWarBooster factionWarBooster;

    private final static Logger log = Loggers.getLogger(GameEventsApi.class);

    private Map<String, EVENTS.EventResource> settlementEvents;
    private Map<String, EVENTS.EventResource> worldEvents;
    private Map<String, EVENTS.EventResource> eventsChance;

    public final static String KEY_PREFIX = "event";
    public final static String FACTION_WAR_ADD = KEY_PREFIX + "chance.factionWarAdd";

    @Getter(lazy = true)
    private final static GameEventsApi instance = new GameEventsApi();

    private GameEventsApi() {
        factionWarBooster = new FactionWarBooster(MoreOptionsScript.MOD_INFO.name, -100, 100, false);
        factionWarBooster.add(ROpinions.GET());
    }


    public void clearCached() {
        settlementEvents = null;
        worldEvents = null;
        eventsChance = null;
    }

    public Map<String, EVENTS.EventResource> getSettlementEvents() {
        if (settlementEvents == null) {
            settlementEvents = readSettlementEvents();
        }

        return settlementEvents;
    }
    public Map<String, EVENTS.EventResource> readSettlementEvents() {
        Map<String, EVENTS.EventResource> settlementEvents = new HashMap<>();
        EVENTS gameEvents = GAME.events();

        settlementEvents.put(KEY_PREFIX + ".settlement.accident", gameEvents.accident);
        settlementEvents.put(KEY_PREFIX + ".settlement.advice", gameEvents.advice);
        settlementEvents.put(KEY_PREFIX + ".settlement.disease", gameEvents.disease);
        settlementEvents.put(KEY_PREFIX + ".settlement.farm", gameEvents.farm);
        settlementEvents.put(KEY_PREFIX + ".settlement.fish", gameEvents.fish);
        settlementEvents.put(KEY_PREFIX + ".settlement.killer", gameEvents.killer);
        settlementEvents.put(KEY_PREFIX + ".settlement.orchard", gameEvents.orchard);
        settlementEvents.put(KEY_PREFIX + ".settlement.pasture", gameEvents.pasture);
        settlementEvents.put(KEY_PREFIX + ".settlement.raceWars", gameEvents.raceWars);
        settlementEvents.put(KEY_PREFIX + ".settlement.riot", gameEvents.riot);
        settlementEvents.put(KEY_PREFIX + ".settlement.slaver", gameEvents.slaver);
        settlementEvents.put(KEY_PREFIX + ".settlement.temperature", gameEvents.temperature);
        settlementEvents.put(KEY_PREFIX + ".settlement.uprising", gameEvents.uprising);

        return settlementEvents;
    }

    public Map<String, EVENTS.EventResource> getWorldEvents() {
        if (worldEvents == null) {
            worldEvents = readWorldEvents();
        }

        return worldEvents;
    }

    public Map<String, EVENTS.EventResource> readWorldEvents() {
        Map<String, EVENTS.EventResource> worldEvents = new HashMap<>();
        EVENTS gameEvents = GAME.events();

        worldEvents.put(KEY_PREFIX + ".world.factionExpand", gameEvents.world.factionExpand);
        worldEvents.put(KEY_PREFIX + ".world.factionBreak", gameEvents.world.factionBreak);
        worldEvents.put(KEY_PREFIX + ".world.popup", gameEvents.world.popup);
        worldEvents.put(KEY_PREFIX + ".world.war", gameEvents.world.war);
        worldEvents.put(KEY_PREFIX + ".world.warPlayer", gameEvents.world.warPlayer);
        worldEvents.put(KEY_PREFIX + ".world.raider", gameEvents.world.raider);
        worldEvents.put(KEY_PREFIX + ".world.rebellion", gameEvents.world.rebellion);
        worldEvents.put(KEY_PREFIX + ".world.plague", gameEvents.world.plague);

        return worldEvents;
    }

    public Map<String, EVENTS.EventResource> getEventsChance() {
        if (eventsChance == null) {
            eventsChance = readEventsChance();
        }

        return eventsChance;
    }

    public Map<String, EVENTS.EventResource> readEventsChance() {
        Map<String, EVENTS.EventResource> eventsChance = new HashMap<>();
        EVENTS gameEvents = GAME.events();

        eventsChance.put(KEY_PREFIX + ".chance.raider", gameEvents.world.raider);
        eventsChance.put(KEY_PREFIX + ".chance.disease", gameEvents.disease);

        return eventsChance;
    }

    public boolean setChance(String eventKey, int chance) {
        // chance for faction war against player is handled extra
        if (FACTION_WAR_ADD.equals(eventKey)) {
            setFactionWarAddValue(chance);
            return true;
        }

        EVENTS.EventResource event = eventsChance.get(eventKey);

        if (event instanceof EventDisease) {
            double current = DISEASES.EPIDEMIC_CHANCE;
            DISEASES.EPIDEMIC_CHANCE = current * MathUtil.toPercentage(chance);
            return true;
        } else if (event instanceof EventWorldRaider) {
            ((EventWorldRaider) event).setChanceMulti(MathUtil.toPercentage(chance));
            return true;
        }

        log.warn("Could not set chance for %s", event.getClass().getSimpleName());
        return false;
    }

    public void setFactionWarAddValue(int value) {
        log.trace("Set faction war adder to %s", value);
        factionWarBooster.set(value);
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
