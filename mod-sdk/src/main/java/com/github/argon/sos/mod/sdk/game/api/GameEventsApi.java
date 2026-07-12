package com.github.argon.sos.mod.sdk.game.api;

import com.github.argon.sos.mod.sdk.data.TreeNode;
import com.github.argon.sos.mod.sdk.game.action.Resettable;
import com.github.argon.sos.mod.sdk.game.util.GameEventUtil;
import com.github.argon.sos.mod.sdk.log.Logger;
import com.github.argon.sos.mod.sdk.log.Loggers;
import com.github.argon.sos.mod.sdk.util.ReflectionUtil;
import game.GAME;
import game.event.actions.EventAction;
import game.event.actions._EVENT;
import game.event.engine.Event;
import game.events.EVENTS;
import game.faction.FACTIONS;
import game.faction.Faction;
import init.value.Lock;
import init.value.Locker;
import init.sprite.UI.UI;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import snake2d.util.sets.ArrayListGrower;
import snake2d.util.sets.LIST;
import snake2d.util.sprite.SPRITE;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Access to the games events like raids
 */
public class GameEventsApi implements Resettable {

    /**
     * Creates a new {@link GameEventsApi}.
     */
    public GameEventsApi() {
    }

    private final static Logger log = Loggers.getLogger(GameEventsApi.class);

    @Nullable
    private Map<String, EVENTS.EventResource> eventResources;
    @Nullable
    private Map<String, Event> events;
    @Nullable
    private Map<String, TreeNode<EventContainer>> eventTrees;
    @Nullable
    private Map<String, EventLocker> eventLocks;

    /**
     * Used as prefix for event keys to identify them as an event.
     */
    public final static String KEY_PREFIX = "event";

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        eventResources = null;
        events = null;
        eventLocks = null;
    }

    /**
     * Returns all available {@link EventLocker}s.
     *
     * @return map with event key and their locks
     */
    public Map<String, EventLocker> getEventLockers() {
        if (eventLocks == null) {
            eventLocks = newEventLocks();
        }

        return eventLocks;
    }

    /**
     * Will lock or unlock an event by the given key.
     *
     * @param key of the event to lock or unlock
     * @param locked whether the event shall be locked
     */
    public void lockEvent(String key, boolean locked) {
        GameEventsApi.EventLocker locker = getEventLockers().get(key);

        if (locker == null) {
            log.warn("Could not find event lock %s in game api result.", key);
            log.trace("API Result: %s", eventLocks);
            return;
        }

        locker.setLocked(locked);
    }

    /**
     * Returns a map with event keys and their {@link game.events.EVENTS.EventResource}s.
     *
     * @return a map with all game events
     */
    public Map<String, EVENTS.EventResource> getEventResources() {
        if (eventResources == null) {
            eventResources = readEventResources();
        }

        return eventResources;
    }

    /**
     * Will enable or disable an {@link game.events.EVENTS.EventResource} with the given key.
     *
     * @param key of the event to enable or disable
     * @param enabled whether an event shall be enabled or disabled
     */
    public void enableEventResource(String key, boolean enabled) {
        getEventResource(key).ifPresent(eventResource -> {
            enableEventResource(eventResource, enabled);
        });
    }

    /**
     * Returns the {@link game.events.EVENTS.EventResource} with the given key.
     *
     * @param key of the event
     * @return event resource if present
     */
    public Optional<EVENTS.EventResource> getEventResource(String key) {
        return Optional.ofNullable(getEventResources().get(KEY_PREFIX + "." + key));
    }

    /**
     * Checks whether an event is suppressed or not.
     *
     * @param event to check
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
     * Checks whether an event is locked for the player.
     * E.g. when he doesn't meet the requirements for a certain event.
     *
     * @param event to check
     * @return whether it is locked or not
     */
    public boolean isLockedForPlayer(Event event) {
        return event.occurence.plockable.passes(FACTIONS.player());
    }

    /**
     * Returns all event chains as a tree.
     *
     * @return event chains as tree
     */
    public Map<String, TreeNode<EventContainer>> getEventTrees() {
        if (eventTrees == null) {
            eventTrees = readEventTrees();
        }

        return eventTrees;
    }

    /**
     * Returns a map with the event key and the corresponding {@link Event}.
     *
     * @return a map with all game events
     */
    public Map<String, Event> getEvents() {
        if (events == null) {
            events = readEvents();
        }

        return events;
    }

    /**
     * Reads a map with the event key and the corresponding {@link Event} directly from the game.
     *
     * @return a map with all game events
     */
    public Map<String, Event> readEvents() {
        Map<String, Event> events = new HashMap<>();
        try {
            ArrayListGrower<Event> all = ReflectionUtil.getDeclaredField("all", Event.class)
                .map(field ->
                     ReflectionUtil.getDeclaredFieldValue(field, Event.class)
                        .orElseThrow(() -> new RuntimeException("Got empty 'all' from event class " + Event.class.getName()))
                ).filter(ArrayListGrower.class::isInstance)
                .map(list -> (ArrayListGrower<Event>) list)
                .orElse(new ArrayListGrower<>());


            for (Event event : all) {
                events.put(KEY_PREFIX + "." + event.key, event);
            }

        } catch (Exception e) {
            log.warn("Could not read '%s.all' field", Event.class, e);
        }

        return events;
    }

    /**
     * Reads the enabled status of all event resources directly from the game.
     *
     * @return a map with event keys and whether they are enabled
     */
    public Map<String, Boolean> readEventResourcesEnabledStatus() {
        return getEventResources().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> GameEventUtil.isEnabled(entry.getValue())));
    }

    /**
     * Will enable or disable the given {@link game.events.EVENTS.EventResource}.
     *
     * @param event to enable or disable
     * @param enabled whether the event shall be enabled or disabled
     */
    public void enableEventResource(EVENTS.EventResource event, Boolean enabled) {
        event.supress(!enabled);
    }

    /**
     * Calls the private clear() method of {@link EVENTS.EventResource} for the given event.
     * This will reset any timers and progress in the event.
     *
     * @param event to reset
     * @return whether the reset was successful or not
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

    private Map<String, EVENTS.EventResource> readEventResources() {
        Map<String, EVENTS.EventResource> eventResources = new HashMap<>();
        for (EVENTS.EventResource eventResource : GAME.events().all()) {
            eventResources.put(KEY_PREFIX + "." + eventResource.key, eventResource);
        }

        return eventResources;
    }

    private Map<String, TreeNode<EventContainer>> readEventTrees() {
        Map<String, TreeNode<EventContainer>> eventTrees = getEvents().values().stream()
            .map(EventContainer::new)
            .map(TreeNode::new)
            .map(this::readEventTree)
            .collect(Collectors.toMap(
                eventTreeNode -> KEY_PREFIX +  "." + eventTreeNode.get().getEvent().key,
                eventTreeNode -> eventTreeNode
            ));

        // remove events when they are used as child in other events
        eventTrees.entrySet().removeIf(entry -> {
            String eventKeyToFind = entry.getKey();

            for (Map.Entry<String, TreeNode<EventContainer>> e : eventTrees.entrySet()) {
                TreeNode<EventContainer> eventTree = e.getValue();

                for (TreeNode<EventContainer> eventTreeNode : eventTree) {
                    // skipping, because it would always match
                    if (eventTreeNode.isRoot()) {
                        continue;
                    }

                    if (eventKeyToFind.equals(KEY_PREFIX +  "." + eventTreeNode.get().getEvent().key)) {
                        return true;
                    }
                }
            }

            return false;
        });

        return eventTrees;
    }

    private TreeNode<EventContainer> readEventTree(TreeNode<EventContainer> node) {
        EventContainer eventContainer = node.get();
        Event event = eventContainer.getEvent();

        if (event.on_spawn != null) {
            extracted(EventContainer.Context.ON_SPAWN, node, event.on_spawn);
        }

        if (event.selection != null) {
            extracted(EventContainer.Context.SELECTION, node, event.selection.onFail);
        }

        if (event.condition != null) {
            extracted(EventContainer.Context.CONDITION, node, event.condition.on_fulfill);
        }

        if (event.duration != null) {
            extracted(EventContainer.Context.DURATION, node, event.duration.on_expire);
        }

        event.choices.forEach(choice -> {
            extracted(EventContainer.Context.CHOICE, node, choice.actions);
        });

        event.aborters.forEach(aborter -> {
            extracted(EventContainer.Context.ABORTER, node, aborter.on_fulfill);
        });
        return node;
    }

    private void extracted(EventContainer.Context context, TreeNode<EventContainer> node, LIST<EventAction> actions) {
        actions.forEach(eventAction -> {
            // only pull eventAction triggering other events
            if (!(eventAction instanceof _EVENT.Imp)) {
                return;
            }

            Event otherEvent = ((_EVENT.Imp) eventAction).other;
            EventContainer eventContainer1 = EventContainer.builder()
                .context(context)
                .event(otherEvent)
                .build();
            TreeNode<EventContainer> nodeChild = node.node(eventContainer1);

            // recursion
            readEventTree(nodeChild);
        });
    }

    private void _reset(EVENTS.EventResource event) {
        log.trace("Resetting event %s", event.getClass().getSimpleName());
        ReflectionUtil.invokeMethod("clear", event);
    }

    private Map<String, EventLocker> newEventLocks() {
        Map<String, EventLocker> eventLocks = new HashMap<>();

        getEvents().forEach((key, event) -> {
            EventLocker eventLocker = new EventLocker("Mod SDK", UI.icons().m.cog);
            Lock<Faction> factionLock = new Lock<>(event.occurence.plockable, eventLocker);
            eventLocks.put(key, eventLocker);
            event.occurence.plockable.push(factionLock);
        });

        return eventLocks;
    }

    /**
     * A {@link Locker} to lock or unlock an event for a specific or any faction.
     */
    public static class EventLocker extends Locker<Faction> {
        @Nullable
        private final Faction faction;

        @Setter
        private boolean locked = false;

        /**
         * Creates a new {@link EventLocker} unbound to any specific faction.
         *
         * @param name of the locker
         * @param icon of the locker
         */
        public EventLocker(CharSequence name, SPRITE icon) {
            this(null, name, icon);
        }

        /**
         * Creates a new {@link EventLocker} bound to the given faction.
         *
         * @param faction the locker is bound to, or null for any faction
         * @param name of the locker
         * @param icon of the locker
         */
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

    /**
     * Wraps an {@link Event} together with the {@link Context} it is used in inside an event tree.
     */
    @Getter
    @Builder
    @AllArgsConstructor
    public static class EventContainer {
        /**
         * Used as prefix for event keys to identify them as an event.
         */
        public final static String KEY_PREFIX = "event";

        private Event event;
        @Builder.Default
        private Context context = Context.ROOT;

        /**
         * Creates a new {@link EventContainer} with {@link Context#ROOT} as context.
         *
         * @param event to wrap
         */
        public EventContainer(Event event) {
            this.event = event;
            this.context = Context.ROOT;
        }

        /**
         * The place inside an event where another event can be triggered from.
         */
        public enum Context {
            /**
             * The root/topmost event, not triggered from within another event.
             */
            ROOT,
            /**
             * Triggered when the events duration expires.
             */
            DURATION,
            /**
             * Triggered by one of the events choices.
             */
            CHOICE,
            /**
             * Triggered on spawn of the event.
             */
            ON_SPAWN,
            /**
             * Triggered when the events selection fails.
             */
            SELECTION,
            /**
             * Triggered when one of the events aborters is fulfilled.
             */
            ABORTER,
            /**
             * Triggered when the events condition is fulfilled.
             */
            CONDITION
        }
    }
}
