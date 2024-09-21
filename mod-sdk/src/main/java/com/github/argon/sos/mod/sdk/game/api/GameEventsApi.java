package com.github.argon.sos.mod.sdk.game.api;

import com.github.argon.sos.mod.sdk.game.action.Resettable;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.util.ReflectionUtil;
import game.GAME;
import game.events.EVENTS;
import game.events.general.EventAbs;
import game.faction.Faction;
import game.values.Lock;
import game.values.Locker;
import init.sprite.UI.UI;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import snake2d.util.sprite.SPRITE;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Access to the games events like raids
 */
public class GameEventsApi implements Resettable {

    private final static Logger log = Loggers.getLogger(GameEventsApi.class);

    @Nullable
    private Map<String, EVENTS.EventResource> eventResources;
    @Nullable
    private Map<String, EventAbs> events;
    @Nullable
    private Map<String, EventLocker> eventLocks;

    public final static String KEY_PREFIX = "event";

    @Override
    public void reset() {
        eventResources = null;
        events = null;
        eventLocks = null;
    }

    /**
     * @return a map with all game events
     */
    public Map<String, EVENTS.EventResource> getEventResources() {
        if (eventResources == null) {
            eventResources = readEventResources();
        }

        return eventResources;
    }

    public Map<String, EventLocker> getEventLocks() {
        if (eventLocks == null) {
            eventLocks = newEventLocks();
        }

        return eventLocks;
    }

    private Map<String, EventLocker> newEventLocks() {
        Map<String, EventLocker> eventLocks = new HashMap<>();

        getEvents().forEach((key, event) -> {
            EventLocker eventLocker = new EventLocker("Mod SDK", UI.icons().m.cog);
            Lock<Faction> factionLock = new Lock<>(event.plockable, eventLocker);
            eventLocks.put(key, eventLocker);
            event.plockable.push(factionLock);
        });

        return eventLocks;
    }

    public Optional<EVENTS.EventResource> getEventResource(String key) {
        return Optional.ofNullable(getEventResources().get(KEY_PREFIX + "." + key));
    }

    public Map<String, EVENTS.EventResource> readEventResources() {
        Map<String, EVENTS.EventResource> eventResources = new HashMap<>();
        for (EVENTS.EventResource eventResource : GAME.events().all()) {
            eventResources.put(KEY_PREFIX + "." + eventResource.key, eventResource);
        }

        return eventResources;
    }

    /**
     * @return a map with all game events
     */
    public Map<String, EventAbs> getEvents() {
        if (events == null) {
            events = readEvents();
        }

        return events;
    }

    public Map<String, EventAbs> readEvents() {
        Map<String, EventAbs> events = new HashMap<>();
        for (EventAbs event : EventAbs.ALL()) {
            events.put(KEY_PREFIX + "." + event.key, event);
        }

        return events;
    }

    public Map<String, Boolean> readEventResourcesEnabledStatus() {
        return getEventResources().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> isEnabled(entry.getValue())));
    }

    public void enableEventResource(EVENTS.EventResource event, Boolean enabled) {
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

    public static class EventLocker extends Locker<Faction> {
        @Nullable
        private final Faction faction;

        @Setter
        private boolean locked = false;
        public EventLocker(CharSequence name, SPRITE icon) {
            this(null, name, icon);
        }

        public EventLocker(@Nullable Faction faction, CharSequence name, SPRITE icon) {
            super(name, icon);
            this.faction = faction;
        }

        @Override
        public boolean inUnlocked(Faction faction) {
            boolean sameFaction = true;
            if (this.faction != null) {
                sameFaction = this.faction.equals(faction);
            }

            return sameFaction && !locked;
        }
    }
}
