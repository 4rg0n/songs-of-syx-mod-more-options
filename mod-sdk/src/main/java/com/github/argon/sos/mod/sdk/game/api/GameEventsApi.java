package com.github.argon.sos.mod.sdk.game.api;

import com.github.argon.sos.mod.sdk.game.action.Resettable;
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
public class GameEventsApi implements Resettable {

    private final static Logger log = Loggers.getLogger(GameEventsApi.class);

    private Map<String, EVENTS.EventResource> events;

    public final static String KEY_PREFIX = "event";

    @Override
    public void reset() {
        events = null;
    }

    /**
     * @return a map with all game events
     */
    public Map<String, EVENTS.EventResource> getEvents() {
        if (events == null) {
            events = readEvents();
        }

        return events;
    }

    public Optional<EVENTS.EventResource> getEvent(String key) {
        return Optional.ofNullable(getEvents().get(key));
    }

    public Map<String, EVENTS.EventResource> readEvents() {
        Map<String, EVENTS.EventResource> events = new HashMap<>();
        for (EVENTS.EventResource eventResource : GAME.events().all()) {
            events.put(KEY_PREFIX + "." + eventResource.key, eventResource);
        }

        return events;
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
