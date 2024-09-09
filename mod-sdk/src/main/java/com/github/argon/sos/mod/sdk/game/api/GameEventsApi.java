package com.github.argon.sos.mod.sdk.game.api;

import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.util.ReflectionUtil;
import game.GAME;
import game.events.EVENTS;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Access to the games events like raids
 */
public class GameEventsApi {

    private final static Logger log = Loggers.getLogger(GameEventsApi.class);

    private Map<String, EVENTS.EventResource> events;
    private Map<String, EVENTS.EventResource> eventsChances;

    public final static String KEY_PREFIX = "event";

    public void clearCached() {
        events = null;
        eventsChances = null;
    }

    public Map<String, EVENTS.EventResource> getEvents() {
        if (events == null) {
            events = readWorldEvents();
        }

        return events;
    }

    public Optional<EVENTS.EventResource> getEvent(String key) {
        return Optional.ofNullable(getEvents().get(key));
    }

    public Map<String, EVENTS.EventResource> readWorldEvents() {
        Map<String, EVENTS.EventResource> worldEvents = new HashMap<>();
        for (EVENTS.EventResource eventResource : GAME.events().all()) {
            worldEvents.put(KEY_PREFIX + "." + eventResource.key, eventResource);
        }

        return worldEvents;
    }

    public Map<String, EVENTS.EventResource> getEventsChances() {
        if (eventsChances == null) {
            eventsChances = readEventsChance();
        }

        return eventsChances;
    }

    public Map<String, EVENTS.EventResource> readEventsChance() {
        Map<String, EVENTS.EventResource> eventsChance = new HashMap<>();
        EVENTS gameEvents = GAME.events();

        eventsChance.put(KEY_PREFIX + ".chance.disease", gameEvents.disease);

        return eventsChance;
    }

    public boolean setChance(String eventKey, int chance) {
        EVENTS.EventResource event = eventsChances.get(eventKey);

        // FIXME there are now multiple DISEASES.all()
//        if (event instanceof EventDisease) {
//            double current = DISEASES.EPIDEMIC_CHANCE;
//            DISEASES.EPIDEMIC_CHANCE = current * MathUtil.toPercentage(chance);
//            return true;
//        }

        log.warn("Could not set chance for %s", event.getClass().getSimpleName());
        return false;
    }

    public Map<String, Boolean> readEventsEnabledStatus() {
        return getEvents().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> isEnabled(entry.getValue())));
    }

    public void enableEvent(EVENTS.EventResource event, Boolean enabled) {
        event.supress(!enabled);
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
        ReflectionUtil.invokeMethod("clear", event);
    }
}
